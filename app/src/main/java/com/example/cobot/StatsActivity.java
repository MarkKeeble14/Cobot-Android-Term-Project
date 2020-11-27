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
    static ArrayList<CountryData> countryDataList = new ArrayList<CountryData>();

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

            // Change json array to gson
            Gson gson = new Gson();

            for (int i = 0; i < jsonArray.length(); i++) {
                String aData = gson.toJson(jsonArray.get(i));
                String name = parseFor("Country", aData);
                String tc = parseFor("TotalConfirmed", aData);
                String nc = parseFor("NewConfirmed", aData);
                String td = parseFor("TotalDeaths", aData);
                String nd = parseFor("NewDeaths", aData);
                String tr = parseFor("TotalRecovered", aData);
                String nr = parseFor("NewRecovered", aData);
                CountryData cd = new CountryData(name.substring(1, name.length()),
                        tc.substring(0, tc.length() - 1),
                        nc.substring(0, nc.length() - 1),
                        td.substring(0, td.length() - 1),
                        nd.substring(0, nd.length() - 1),
                        tr.substring(0, tr.length() - 1),
                        nr.substring(0, nr.length() - 1));
                countryDataList.add(cd);
            }

            //  hide progess
            progressBar.setVisibility(View.GONE );
        }
        catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(StatsActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String parseFor(String lookingFor, String parse) {
        String res = "";
        String track = "";
        int trackAt = 0;
        String lowercaseLookingFor = lookingFor.toLowerCase();
        String lowercaseParse = parse.toLowerCase();
        for (int i = 0; i < lowercaseParse.length(); i++) {
            if (lowercaseParse.charAt(i) == lowercaseLookingFor.charAt(trackAt)) {
                trackAt++;
                track += lowercaseParse.charAt(i);
            } else {
                trackAt = 0;
                track = "";
            }
            if ((track.length() - lowercaseLookingFor.length()) == 0) {
                int startAt = i + 3;
                while (lowercaseParse.charAt(startAt) != '\"' || startAt < i + 4) {
                    res += lowercaseParse.charAt(startAt);
                    startAt++;
                }
                return res;
            }
        }
        return "err";
    }

    public static CountryData getCountriesData(String country) {
        for (CountryData cd : countryDataList) {
            if (cd.getName().equalsIgnoreCase(country))
                return cd;
        }
        return null;
    }
}