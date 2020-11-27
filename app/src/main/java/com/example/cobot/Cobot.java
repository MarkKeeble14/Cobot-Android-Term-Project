package com.example.cobot;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Cobot extends StatsActivity {
    Random r = new Random();
    final int MIN_RESPONSE_TIME = 100;
    final int MAX_RESPONSE_TIME = 2000;

    private FirebaseAuth mAuth;
    private DatabaseReference dRef;

    private Context context;
    private LinearLayout ll;

    private String currentTargetCountry;

    // Stage 0 = Base level prompt
    // Stage 1 = Asking for country specifics
    // Stage 2 = Asking for stat specifics
    // Stage 3 = Asking if the user is done with the current country
    private int stage = 0;

    private String menuPrompt = "What would you like to know about?:\n1: Global Statistics\n2: Country Specific Statistics\n";
    private String statMenuPrompt = "What statistic are you curious about?:\n1: Give me everything!\n2: Total Cases\n3: New Cases\n4: Total Deaths\n5: New Deaths\n6: Total Recoveries\n7: New Recoveries\n";
    private String countrySpecificMenuPrompt = "What country are you curious about? (Please type in the name of a country):\n";
    private String doneWithData = "Do you have more questions?\n";

    public Cobot(Context context, LinearLayout ll) {
        this.context = context;
        this.ll = ll;
        dRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();
    }

    public void giveResponseToMessage(final String x, final boolean saveToFirebase) {
        int i1 = (r.nextInt(MAX_RESPONSE_TIME) + MIN_RESPONSE_TIME);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                generateResponse(x, saveToFirebase);
            }
        }, i1);
    }

    public void preparePrompt(final boolean saveToFirebase) {
        int i1 = (r.nextInt(MAX_RESPONSE_TIME) + MIN_RESPONSE_TIME);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                sendPrompt(saveToFirebase);
            }
        }, i1);
    }

    private void sendPrompt(boolean saveToFirebase) {
        TextView newText;
        newText = new TextView(new ContextThemeWrapper(context, R.style.ReceivedMessage), null, 0);
        newText.setText(menuPrompt);
        if (saveToFirebase)
            addMessageToFirebase(menuPrompt, context.getString(R.string.cobot_name));
        MyUtilities.setMargins(newText, 0, 10, 0, 10);

        ll.addView(newText);
    }

    private void generateResponse(final String x, boolean saveToFirebase) {
        TextView newText;
        newText = new TextView(new ContextThemeWrapper(context, R.style.ReceivedMessage), null, 0);

        String response = "";
        if (stage == 0) {
            if (x.equals("1")) {
                response = statMenuPrompt;
                currentTargetCountry = "global";
                stage = 1;
            } else if (x.equals("2")) {
                response = countrySpecificMenuPrompt;
                stage = 2;
            } else {
                response = giveErrorResponse();
            }
        } else if (stage == 1) {
            String stat = "";
            switch (x) {
                case "1":
                    stat = "all";
                    break;
                case "2":
                    stat = "total_cases";
                    break;
                case "3":
                    stat = "new_cases";
                    break;
                case "4":
                    stat = "total_deaths";
                    break;
                case "5":
                    stat = "new_deaths";
                    break;
                case "6":
                    stat = "total_recoveries";
                    break;
                case "7":
                    stat = "new_recoveries";
                    break;
                default:
                    break;
            }

            // Get response according to stat given
            response = getResult(stat, currentTargetCountry);
            Log.e("Stage 2 Response: ", stat + ", " + response);
            stage = 3;
        } else if (stage == 2) {
            currentTargetCountry = x;
            response = statMenuPrompt;
            stage = 1;
        } else if (stage == 3) {
            if (x.equalsIgnoreCase("Yes") || x.equalsIgnoreCase("Y")) {
                response = statMenuPrompt;
                stage = 1;
            }
            else {
                response = menuPrompt;
                stage = 0;
                currentTargetCountry = "";
            }
        }
        newText.setText(response);
        MyUtilities.setMargins(newText, 0, 15, 0, 15);
        ll.addView(newText);
        if (saveToFirebase)
            addMessageToFirebase(response, context.getString(R.string.cobot_name));

        if (stage == 0 && response.equalsIgnoreCase("")) {
            preparePrompt(true);
        }
        if (stage == 3) {
            TextView endText;
            endText = new TextView(new ContextThemeWrapper(context, R.style.ReceivedMessage), null, 0);
            endText.setText(doneWithData);
            MyUtilities.setMargins(endText, 0, 15, 0, 15);
            ll.addView(endText);
            if (saveToFirebase)
                addMessageToFirebase(doneWithData, context.getString(R.string.cobot_name));
        }
        Log.e("Stage", String.valueOf(stage));
    }

    private String giveErrorResponse() {
        String[] responses = context.getResources().getStringArray(R.array.error_responses);
        Log.d("size of response", String.valueOf(responses.length));

        int i = r.nextInt(responses.length);
        return responses[i];
    }

    private String retrieveGlobalData() {
        String globalData = "Total Confirmed: " + totalConfirmed + "\n"
                + "New Confirmed: " + newConfirmed + "\n"
                + "Total Deaths: " + totalDeaths + "\n"
                + "New Deaths:  " + newDeaths + "\n"
                + "Total Recovered: " + totalRecovered + "\n"
                + "New Recovered: " + newRecovered;
        return globalData;
    }

    private String getResult(String stat, String country) {
        Log.e("Country & Stat: ", stat + ", " + country);
        if (country.equalsIgnoreCase("global")) {
            if (stat.equalsIgnoreCase("all")) {
                return retrieveGlobalData();
            } else if (stat.equalsIgnoreCase("total_cases")) {
                return "Total Cases: " + totalConfirmed + "\n";
            } else if (stat.equalsIgnoreCase("new_cases")) {
                return "New Cases: " + newConfirmed + "\n";
            } else if (stat.equalsIgnoreCase("total_deaths")) {
                return "Total Deaths: " + totalDeaths + "\n";
            } else if (stat.equalsIgnoreCase("new_deaths")) {
                return "New Deaths: " + newDeaths + "\n";
            } else if (stat.equalsIgnoreCase("total_recoveries")) {
                return "Total Recoveries: " + totalRecovered + "\n";
            } else if (stat.equalsIgnoreCase("new_recoveries")) {
                return "New Recoveries: " + newRecovered + "\n";
            } else {
                return "Wack";
            }
        } else {
            CountryData data = getCountriesData(country);
            if (stat.equalsIgnoreCase("all")) {
                return retrieveCountryData(country);
            } else if (stat.equalsIgnoreCase("total_cases")) {
                return "Total Cases: " + data.getTotalCases() + "\n";
            } else if (stat.equalsIgnoreCase("new_cases")) {
                return "New Cases: " + data.getNewCases() + "\n";
            } else if (stat.equalsIgnoreCase("total_deaths")) {
                return "Total Deaths: " + data.getTotalDeaths() + "\n";
            } else if (stat.equalsIgnoreCase("new_deaths")) {
                return "New Deaths: " + data.getNewDeaths() + "\n";
            } else if (stat.equalsIgnoreCase("total_recoveries")) {
                return "Total Recoveries: " + data.getTotalRecovered() + "\n";
            } else if (stat.equalsIgnoreCase("new_recoveries")) {
                return "New Recoveries: " + data.getNewRecovered() + "\n";
            } else {
                return "Wack";
            }
        }
    }

    private String retrieveCountrySpecificData() {
        return arrayData + "\n";
    }

    private void addMessageToFirebase(String content, String sender) {
        String id = dRef.push().getKey();
        String curTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String curDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Message m = new Message(id, content, sender, curTime, curDate);
        dRef.child(mAuth.getUid()).child("conversations").child(m.getDate()).child(id).setValue(m);
    }

    private String retrieveCountryData(String country) {
        CountryData data = getCountriesData(country);
        String globalData = "Total Confirmed: " + data.getTotalCases() + "\n"
                + "New Confirmed: " + data.getNewCases() + "\n"
                + "Total Deaths: " + data.getTotalDeaths() + "\n"
                + "New Deaths:  " + data.getNewDeaths() + "\n"
                + "Total Recovered: " + data.getTotalRecovered() + "\n"
                + "New Recovered: " + data.getNewRecovered();
        return globalData;
    }
}
