package com.example.homework_score;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link reading_process#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reading_process extends Fragment {
    String now, maxpage;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Spinner spinner;
    Button enter;
    ArrayList<String> spinner_data = new ArrayList<>();
    EditText preview;
    private DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public reading_process() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment reading_process.
     */
    // TODO: Rename and change types and number of parameters
    public static reading_process newInstance(String param1, String param2) {
        reading_process fragment = new reading_process();
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
        //upload();
        View view = inflater.inflate(R.layout.fragment_reading_process, container, false);
        spinner_data.clear();
        spinner_data.add("請選擇");
        spinner = view.findViewById(R.id.book);
        enter = view.findViewById(R.id.enter_data);
        preview = view.findViewById(R.id.book_preview);
        fireDB.child("book").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot key : dataSnapshot.getChildren()) {
                    spinner_data.add(key.getKey());
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        spinner.setAdapter(new ArrayAdapter(getActivity(),
                                android.R.layout.simple_list_item_1,
                                spinner_data));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fireDB.child("book").child(spinner.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            now = dataSnapshot.child("now").getValue().toString();
                            maxpage = dataSnapshot.child("maxpage").getValue().toString();
                            preview.setText(now);
                        } else {
                            preview.setText("null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spinner.getSelectedItem().toString().equals("請選擇")) {
                    fireDB.child("book").child(spinner.getSelectedItem().toString()).child("now").setValue(preview.getText().toString());
                    show("successful");
                }

            }
        });
        return view;
    }

    void upload() {
        HashMap<String, String> t = new HashMap<>();
        t.put("now", "0");
        t.put("maxpage", "300");
        fireDB.child("book").child("123").setValue(t);
    }

    void show(String data) {
        Toast.makeText(getActivity(), data, Toast.LENGTH_LONG).show();
    }
}