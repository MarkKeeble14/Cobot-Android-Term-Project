package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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
        navigationView = findViewById(R.id.bottom_navigation);

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_news:
                        break;
                    case R.id.nav_chat:
                        Intent iChat = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(iChat);
                        break;
                    case R.id.nav_auth:
                        Intent iAuth = new Intent(MainActivity.this, AuthenticationActivity.class);
                        startActivity(iAuth);
                        break;
                }
                return false;
            }
        });
    }
}