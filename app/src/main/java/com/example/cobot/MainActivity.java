package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    // UI Views
    private TextView titleTv;
    private ImageButton refreshBtn;
    private BottomNavigationView navigationView;

    // Fragments
    private Fragment Fragment1, Fragment2;
    private Fragment activeFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init UI Views
        titleTv = findViewById(R.id.titleTv);
        refreshBtn = findViewById(R.id.refreshBtn);
        navigationView = findViewById(R.id.navigationView);

        initFragments();

        // refresh button click, refresh records
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment1.onResume();
            }
        });

        navigationView.setOnNavigationItemSelectedListener(this);

//        testPrint();
    }

    private void initFragments() {
        // init Fragments
        Fragment1 = new Fragment1();
        Fragment2 = new Fragment2();

        fragmentManager = getSupportFragmentManager();
        activeFragment = Fragment1;

        fragmentManager.beginTransaction()
                .add(R.id.frame, Fragment1, "homeFragment")
                .commit();
        fragmentManager.beginTransaction()
                .add(R.id.frame, Fragment2, "statsFragment")
                .hide(Fragment2)
                .commit();

    }

    private void loadHomeFragment() {
        titleTv.setText("Home");
        fragmentManager.beginTransaction().hide(activeFragment).show(Fragment1).commit();
        activeFragment = Fragment1;

    }

    private void loadStatsFragment() {
        titleTv.setText("Cobot");
        fragmentManager.beginTransaction().hide(activeFragment).show(Fragment2).commit();
        activeFragment = Fragment2;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // handle bottom na item clicks
        switch (item.getItemId()) {
            case R.id.nav_home:
                loadHomeFragment();
                return true;
            case R.id.nav_stats:
                loadStatsFragment();
                return true;
        }
        return false;
    }

}