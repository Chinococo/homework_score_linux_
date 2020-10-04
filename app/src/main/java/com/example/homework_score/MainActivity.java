package com.example.homework_score;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    selection_screen selectionScreen;
    ViewPager viewPager;
    String input;
    SharedPreferences DB;
    HashMap<String, HashMap<String, HashMap<String, String>>> data = new HashMap<>();
    private DatabaseReference fireDB = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        selectionScreen = new selection_screen(getSupportFragmentManager());
        viewPager = findViewById(R.id.viewpapger);
        viewPager.setAdapter(selectionScreen);
        DB = getSharedPreferences("database", MODE_PRIVATE);

        if (DB.getString("score", "null").equals("null")) {
            DB.edit().clear().apply();
            DB.edit().putString("score", getString(R.string.score)).apply();
            DB.edit().putString("homework", getString(R.string.score)).apply();
        }


    }


    void nofition(String data)//顯示泡泡資料
    {
        Toast.makeText(this, data, Toast.LENGTH_SHORT).show();

    }

    class selection_screen extends FragmentPagerAdapter {
        public selection_screen(FragmentManager fragment) {
            super(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new homework();
                case 1:
                    return new score();
                case 2:
                    return new reading_process();
            }
            return new Fragment();
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "功課輸入器";
                case 1:
                    return "分數輸入器";
                case 2:
                    return "書本進度";
            }
            return "error";
        }


    }
}


