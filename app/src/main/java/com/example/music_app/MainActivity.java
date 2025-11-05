package com.example.music_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.music_app.fragment.AccountFragment;
import com.example.music_app.fragment.HomeFragment;
import com.example.music_app.fragment.LibraryFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Hiển thị HomeFragment khi mở app
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Xử lý sự kiện click bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_library) {
                fragment = new LibraryFragment();
            } else if (itemId == R.id.nav_account) {
                fragment = new AccountFragment();
            }

            return loadFragment(fragment);
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}