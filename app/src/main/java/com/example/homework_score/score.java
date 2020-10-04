package com.example.homework_score;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.Collator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link score#newInstance} factory method to
 * create an instance of this fragment.
 */
public class score extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String now, max_page;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference();
    private EditText score;
    private Spinner Chapter, Chaptername, subject;
    private Button enter, datepicker;
    private TextView date;
    HashMap<String, HashMap<String, ArrayList<String>>> data = new HashMap<>();
    ArrayList<String> chartpter_data = new ArrayList<>();
    ArrayList<String> subject_data = new ArrayList<>();
    ArrayList<String> chartptername_data = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Calendar calendar;
    private ArrayAdapter<String> tmep;
    private SharedPreferences DB;

    public score() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment score.
     */
    // TODO: Rename and change types and number of parameters
    public static score newInstance(String param1, String param2) {
        score fragment = new score();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        DB = this.getActivity().getSharedPreferences("database", Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_score, container, false);
        calendar = Calendar.getInstance();
        enter = view.findViewById(R.id.enter2);
        date = view.findViewById(R.id.date_t2);
        datepicker = view.findViewById(R.id.datepicker2);
        subject = view.findViewById(R.id.subject2);
        Chapter = view.findViewById(R.id.Chapter);
        Chaptername = view.findViewById(R.id.Chaptername);
        score = view.findViewById(R.id.score);
        getsharebasedata();
        datepicker.setOnClickListener(new View.OnClickListener() {
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
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkdata()) {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
                    Date date1 = new Date();
                    String today = dateFormat.format(date1).toString();
                    today = today.replaceAll("/", "_");
                    //ArrayList<String> t=new ArrayList<String>(Arrays.asList(subject.getSelectedItem().toString(), Chapter.getSelectedItem().toString(),Chaptername.getSelectedItem().toString()));
                    fireDB.child("score")
                            .child(today.substring(0, 10))
                            .child(subject.getSelectedItem().toString())
                            .child(Chapter.getSelectedItem().toString())
                            .child(Chaptername.getSelectedItem().toString())
                            .child(date.getText().toString().replaceAll("/", "_") + "_" + today)
                            .setValue(score.getText().toString());
                    clear();
                } else
                    show("資料不完全");

            }
        });
        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!subject.getSelectedItem().toString().equals("請選擇")) {
                    //Comparator comparator = Collator.getInstance(java.util.Locale.TRADITIONAL_CHINESE);
                    for (String key : data.get(subject.getSelectedItem().toString()).keySet())
                        chartpter_data.add(key);
                    Chapter.setAdapter(new ArrayAdapter(getActivity(),
                            android.R.layout.simple_list_item_1,
                            chartpter_data));

                } else {
                    chartpter_data.clear();
                    chartpter_data.add("請選擇");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Chapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!Chapter.getSelectedItem().toString().equals("請選擇")) {
                    chartptername_data.clear();
                    chartptername_data.add("請選擇");
                    chartptername_data.addAll(1, data.get(subject.getSelectedItem().toString()).get(Chapter.getSelectedItem().toString()));
                    Chaptername.setAdapter(new ArrayAdapter(getActivity(),
                            android.R.layout.simple_list_item_1,
                            chartptername_data));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    void show(String data) {
        Toast.makeText(getActivity(), data, Toast.LENGTH_SHORT).show();
    }

    private void getsharebasedata() {
        String[] split = DB.getString("score", "null").split(",");


        for (int i = 3; i < split.length; i += 3) {
            data.put(split[i], new HashMap<>());
        }
        for (int i = 4; i < split.length; i += 3) {
            data.get(split[i - 1]).put(split[i], new ArrayList<String>());
        }
        for (int i = 5; i < split.length; i += 3) {
            data.get(split[i - 2]).get(split[i - 1]).add(split[i]);
        }
        //for(int i=3;i<split.length;i+=3)
        //fireDB.child("homework").child(split[i]).child(split[i+1]).child(split[i+2]).setValue(0);
        subject_data.add("請選擇");
        chartpter_data.add("請選擇");
        chartptername_data.add("請選擇");
        for (String key : data.keySet())
            if (!key.equals(""))
                subject_data.add(key);
        Log.e("subject", subject_data.toString());
        subject.setAdapter(new ArrayAdapter(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                subject_data));
        Chapter.setAdapter(new ArrayAdapter(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                chartpter_data));
        Chaptername.setAdapter(new ArrayAdapter(getActivity(),
                R.layout.support_simple_spinner_dropdown_item,
                chartptername_data));
    }


    boolean checkdata() {
        return !(date.equals("") || subject.getSelectedItem().toString().equals("請選擇") || score.getText().toString().equals(""));
    }

    void clear() {
        Log.e("k", "12.3");
        date.setText("");
        subject.setAdapter(new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                subject_data));
        chartpter_data.clear();
        chartptername_data.clear();
        chartptername_data.add("請選擇");
        chartpter_data.add("請選擇");
        Chapter.setAdapter(new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                chartpter_data));
        Chaptername.setAdapter(new ArrayAdapter(getActivity(),
                android.R.layout.simple_list_item_1,
                chartptername_data));
        score.setText("");
    }
}
