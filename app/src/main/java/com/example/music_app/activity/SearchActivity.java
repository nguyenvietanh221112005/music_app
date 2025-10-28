package com.example.music_app.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.music_app.R;

import com.example.music_app.fragment.Fragment_Tim_Kiem;

public class SearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new Fragment_Tim_Kiem())
                .commit();
    }
}
