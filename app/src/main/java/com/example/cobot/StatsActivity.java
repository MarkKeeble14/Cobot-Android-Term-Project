package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {
    private static final String STATS_URL = "https://api.covid19api.com/summary";


    // Static string variables
    static String totalConfirmed, newConfirmed, totalDeaths, newDeaths, totalRecovered, newRecovered;
    static String arrayData;


    // UI Views
    private ProgressBar progressBar;
    private TextView totalCasesTv, newCasesTv, totalDeathsTv, newDeathsTv, totalRecoveredTv, newRecoveredTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // init UI Views
        progressBar = findViewById(R.id.progressBar);
        totalCasesTv = findViewById(R.id.totalCasesTv);
        newCasesTv = findViewById(R.id.newCasesTv);
        totalDeathsTv = findViewById(R.id.totalDeathsTv);
        newDeathsTv = findViewById(R.id.newDeathsTv);
        totalRecoveredTv = findViewById(R.id.totalRecoveredTv);
        newRecoveredTv = findViewById(R.id.newRecoveredTv);

        progressBar.setVisibility(View.GONE);

        loadHomeData();

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_chat:
                        Intent iChat = new Intent(StatsActivity.this, ChatActivity.class);
                        startActivity(iChat);
                        break;
                    case R.id.nav_auth:
                        Intent iAuth = new Intent(StatsActivity.this, AuthenticationActivity.class);
                        startActivity(iAuth);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadHomeData();
    }

    private void loadHomeData() {
        // show progress
        progressBar.setVisibility(View.VISIBLE);

        // JSON String request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, STATS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response received, handle response
                handleResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // some error occurred, hide progress, show error message
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StatsActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // add request to queue
        RequestQueue requestQueue = Volley.newRequestQueue(StatsActivity.this);
        requestQueue.add(stringRequest);
    }

    public void handleResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            Log.d("response", jsonObject.toString());
            JSONObject globalJo = jsonObject.getJSONObject("Global");

            // get data from it
            newConfirmed = globalJo.getString("NewConfirmed");
            totalConfirmed = globalJo.getString("TotalConfirmed");
            newDeaths = globalJo.getString("NewDeaths");
            totalDeaths = globalJo.getString("TotalDeaths");
            newRecovered = globalJo.getString("NewRecovered");
            totalRecovered = globalJo.getString("TotalRecovered");

            // set data
            totalCasesTv.setText(totalConfirmed);
            newCasesTv.setText(newConfirmed);
            totalDeathsTv.setText(totalDeaths);
            newDeathsTv.setText(newDeaths);
            totalRecoveredTv.setText(totalRecovered);
            newRecoveredTv.setText(newRecovered);

            // Load data
            JSONObject jsonObject2 = new JSONObject(response);
            JSONArray jsonArray = jsonObject2.getJSONArray("Countries");

            // * API WEBSITE https://api.covid19api.com/summary *
            // Change json array to gson
            Gson gson = new Gson();
            // String type Arraydata
            arrayData = gson.toJson(jsonArray.get(30)); // 30th object from the array = Canada

//            for (int i = 0; i < jsonArray.length(); i++) {
//                for (int j = 0; jsonArray. j++ ) {
//
//                }
//            }


//            GsonBuilder gsonBuilder = new GsonBuilder();
//            gsonBuilder.setDateFormat("dd/MM/yyyy hh:mm a");
//            Gson gson = gsonBuilder.create();
//
//            // Get data into ArrayList
//            for (int i = 0; i < jsonArray.length(); i++) {
//            }
//
//            for (int i = 0; i < 1; i++) {
//                country = statArrayList.get(i);
//            }

            //  hide progess
            progressBar.setVisibility(View.GONE );
        }
        catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(StatsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}