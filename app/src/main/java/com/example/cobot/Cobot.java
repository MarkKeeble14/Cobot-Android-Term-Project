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

public class Cobot extends NewsActivity {
    Random r = new Random();
    final int MIN_RESPONSE_TIME = 0;
    final int MAX_RESPONSE_TIME = 1;

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

    private String menuPrompt;
    private String statMenuPrompt;
    private String countrySpecificMenuPrompt;
    private String doneWithData;

    public Cobot(Context context, LinearLayout ll) {
        this.context = context;
        this.ll = ll;
        dRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        menuPrompt = context.getResources().getString(R.string.menu_prompt);
        statMenuPrompt = context.getResources().getString(R.string.statistic_menu_prompt);;
        countrySpecificMenuPrompt = context.getResources().getString(R.string.country_specific_prompt);;
        doneWithData = context.getResources().getString(R.string.done_with_country);;
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
    }

    private String giveErrorResponse() {
        String[] responses = context.getResources().getStringArray(R.array.error_responses);

        int i = r.nextInt(responses.length);
        return responses[i];
    }

    private String retrieveGlobalData() {
        String globalData = context.getResources().getString(R.string.total_confirmed_label) + totalConfirmed + "\n"
                + context.getResources().getString(R.string.new_confirmed_label) + newConfirmed + "\n"
                + context.getResources().getString(R.string.total_deaths_label) + totalDeaths + "\n"
                + context.getResources().getString(R.string.new_deaths_label) + newDeaths + "\n"
                + context.getResources().getString(R.string.total_recovered_label) + totalRecovered + "\n"
                + context.getResources().getString(R.string.new_recovered_label) + newRecovered;
        return globalData;
    }

    private String getResult(String stat, String country) {
        if (country.equalsIgnoreCase(context.getResources().getString(R.string.global))) {
            if (stat.equalsIgnoreCase(context.getResources().getString(R.string.all))) {
                return retrieveGlobalData();
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.total_cases))) {
                return context.getResources().getString(R.string.total_confirmed_label) + totalConfirmed + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.new_cases))) {
                return context.getResources().getString(R.string.new_confirmed_label) + newConfirmed + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.total_deaths))) {
                return context.getResources().getString(R.string.total_deaths_label) + totalDeaths + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.new_deaths))) {
                return context.getResources().getString(R.string.new_deaths_label) + newDeaths + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.total_recoveries))) {
                return context.getResources().getString(R.string.total_recovered_label) + totalRecovered + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.new_recoveries))) {
                return context.getResources().getString(R.string.new_recovered_label) + newRecovered + "\n";
            } else {
                return context.getResources().getString(R.string.err);
            }
        } else {
            CountryData data = getCountriesData(country);
            if (stat.equalsIgnoreCase(context.getResources().getString(R.string.all))) {
                return retrieveCountryData(country);
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.total_cases))) {
                return context.getResources().getString(R.string.total_confirmed_label) + country + " - " + data.getTotalCases() + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.new_cases))) {
                return context.getResources().getString(R.string.new_confirmed_label) + country + " - " + data.getNewCases() + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.total_deaths))) {
                return context.getResources().getString(R.string.total_deaths_label) + country + " - " + data.getTotalDeaths() + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.new_deaths))) {
                return context.getResources().getString(R.string.new_deaths_label) + country + " - " + data.getNewDeaths() + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.total_recoveries))) {
                return context.getResources().getString(R.string.total_recovered_label) + country + " - " + data.getTotalRecovered() + "\n";
            } else if (stat.equalsIgnoreCase(context.getResources().getString(R.string.new_recoveries))) {
                return context.getResources().getString(R.string.new_recovered_label) + country + " - "+ data.getNewRecovered() + "\n";
            } else {
                return context.getResources().getString(R.string.err);
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
        String globalData = context.getResources().getString(R.string.total_confirmed_label) + data.getTotalCases() + "\n"
                + context.getResources().getString(R.string.new_confirmed_label) + data.getNewCases() + "\n"
                + context.getResources().getString(R.string.total_deaths_label) + data.getTotalDeaths() + "\n"
                + context.getResources().getString(R.string.new_deaths_label) + data.getNewDeaths() + "\n"
                + context.getResources().getString(R.string.total_recovered_label) + data.getTotalRecovered() + "\n"
                + context.getResources().getString(R.string.new_recovered_label) + data.getNewRecovered();
        return globalData;
    }
}
