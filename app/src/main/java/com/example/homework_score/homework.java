package com.example.homework_score;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homework#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homework extends Fragment {
    private ArrayList<String> t = new ArrayList<>();//subject
    private TextView date, start_t, end_t;
    private Spinner subject, charter, chartername;
    private Button data_b, start_b, end_b;
    int min1, min2;
    private Calendar calendar;
    private Button enter;
    private DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference();
    private ArrayAdapter<String> tmep;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SharedPreferences DB;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public homework() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homework.
     */
    // TODO: Rename and change types and number of parameters
    public static homework newInstance(String param1, String param2) {
        homework fragment = new homework();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_homework, container, false);
        setup(view);
        event();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    void setup(View view) {
        calendar = Calendar.getInstance();
        date = (TextView) view.findViewById(R.id.date_textview);
        start_t = (TextView) view.findViewById(R.id.starttime);
        end_t = (TextView) view.findViewById(R.id.endtime);
        subject = (Spinner) view.findViewById(R.id.subject_homework);
        data_b = (Button) view.findViewById(R.id.datepicker);
        start_b = (Button) view.findViewById(R.id.starttimepicker);
        end_b = (Button) view.findViewById(R.id.endtimeoicker);
        enter = view.findViewById(R.id.enter);
        charter = view.findViewById(R.id.charter_homework);
        chartername = view.findViewById(R.id.chartername_homework);
        tmep = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, new String[]{"請選擇", "國文", "英文", "數學", "基本電學", "電子學", "數位邏輯"});
        subject.setAdapter(tmep);
        fireDB.child("homework").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t.clear();
                t.add("請選擇");
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    t.add(key.getKey());
                }
                subject.setAdapter(new ArrayAdapter(getActivity(),
                        R.layout.support_simple_spinner_dropdown_item,
                        t));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void event()
    {
        data_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String dateTime = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
                        date.setText(dateTime);
                    }

                }, year, month, day).show();
            }

        });
        start_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        start_t.setText(tran(hourOfDay) + ":" + tran(minute));
                        min1 = hourOfDay * 60 + minute;
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        });
        end_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        end_t.setText(tran(hourOfDay) + ":" + tran(minute));
                        min2 = hourOfDay * 60 + minute;
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show();
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkdata()) {
                    fireDB.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int min = Math.abs(
                                    (Integer.parseInt(
                                            start_t.getText().toString().substring(0, 2)) * 60 + Integer.parseInt(
                                            start_t.getText().toString().substring(3, 5))) -
                                            (Integer.parseInt(
                                                    end_t.getText().toString().substring(0, 2)) * 60 + Integer.parseInt(
                                                    end_t.getText().toString().substring(3, 5))));
                            fireDB.child("homework").child(subject.getSelectedItem().toString()).child(charter.getSelectedItem().toString()).child(chartername.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    HashMap<String,String> t=new HashMap<>();
                                    t.put("time",""+Integer.parseInt(dataSnapshot.getValue().toString())+min);
                                    t.put("start_time",start_t.getText().toString());
                                    t.put("end_time",end_t.getText().toString());
                                    fireDB.child("homework")
                                            .child(subject.getSelectedItem()
                                            .toString()).child(charter
                                            .getSelectedItem().toString())
                                            .child(chartername.getSelectedItem()
                                            .toString())
                                            .setValue(t);

                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            clear();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else
                    show("資料不完全");
            }
        });
        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (subject.getSelectedItem().toString().equals("請選擇")) {
                    ArrayList<String> clear = new ArrayList<>();
                    charter.setAdapter(new ArrayAdapter(getActivity(),
                            android.R.layout.simple_list_item_1,
                            clear));
                    chartername.setAdapter(new ArrayAdapter(getActivity(),
                            android.R.layout.simple_list_item_1,
                            clear));
                } else {
                    fireDB
                            .child("homework")
                            .child(subject.getSelectedItem().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArrayList<String> temp = new ArrayList<>();
                                    temp.add("請選擇");
                                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                                        temp.add(key.getKey());
                                    }
                                    charter.setAdapter(new ArrayAdapter(getActivity(),
                                            R.layout.support_simple_spinner_dropdown_item,
                                            temp));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        charter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (subject.getSelectedItem().toString().equals("請選擇")) {
                    ArrayList<String> clear = new ArrayList<>();
                    chartername.setAdapter(new ArrayAdapter(getActivity(),
                            android.R.layout.simple_list_item_1,
                            clear));
                } else {
                    fireDB
                            .child("homework")
                            .child(subject.getSelectedItem().toString())
                            .child(charter.getSelectedItem().toString())
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    ArrayList<String> temp = new ArrayList<>();
                                    temp.add("請選擇");
                                    for (DataSnapshot key : dataSnapshot.getChildren()) {
                                        temp.add(key.getKey());
                                    }
                                    chartername.setAdapter(new ArrayAdapter(getActivity(),
                                            R.layout.support_simple_spinner_dropdown_item,
                                            temp));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    boolean checkdata() {
        return !( subject.getSelectedItem().toString().equals("請選擇") || start_t.getText().toString().equals("") || end_t.getText().toString().equals(""));
    }

    void show(String data) {
        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
    }

    String tran(int i) {
        if (i < 10)
            return "0" + i;
        return i + "";
    }

    void clear() {
        date.setText("");
        start_t.setText("");
        end_t.setText("");
        subject.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item,t));
        ArrayList<String> clear = new ArrayList<>();
        charter.setAdapter(new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                clear));
        chartername.setAdapter(new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                clear));
    }
}
