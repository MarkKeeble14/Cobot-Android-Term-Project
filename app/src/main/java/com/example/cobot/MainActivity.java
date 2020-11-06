package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_chat:
                        Intent iChat = new Intent(MainActivity.this, ChatActivity.class);
                        startActivity(iChat);
                        break;
                    case R.id.nav_schedule:
                        Intent iSched = new Intent(MainActivity.this, ScheduleActivity.class);
                        startActivity(iSched);
                        break;
                }
                return false;
            }
        });
    }
}