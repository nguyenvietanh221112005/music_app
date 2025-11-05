package com.example.music_app.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.music_app.R;
import com.example.music_app.fragment.AccountFragment;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // 🔹 Hiển thị AccountFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AccountFragment())
                    .commit();
        }
    }
}
