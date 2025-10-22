package com.example.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import com.example.musicapp.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {

    TextView tvHello;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvHello = findViewById(R.id.tvHello);
        etSearch = findViewById(R.id.etSearch);

        tvHello.setText("Xin chào, Anh 👋");

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentHome, new HomeFragment())
                .commit();
    }
}
