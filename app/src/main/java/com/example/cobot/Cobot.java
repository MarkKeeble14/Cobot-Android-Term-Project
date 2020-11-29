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

/*
    The class which acts as a director for the chatting functionality.
 */
public class Cobot extends NewsActivity {
    private Random r = new Random();

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;

    // The object must recieve the context when it is first instantiated, so that it can
    // send toasts and the like.
    private Context context;
    // The linear layout to append the messages to.
    private LinearLayout ll;

    // Chatting logic
    // The current topic country
    private String currentTargetCountry;

    // Stage 0 = Base level prompt
    // Stage 1 = Asking for country specifics
    // Stage 2 = Asking for stat specifics
    // Stage 3 = Asking if the user is done with the current country
    private int stage = 0;

    // These are the prompts that the bot sends out to the user
    private String menuPrompt;
    private String statMenuPrompt;
    private String countrySpecificMenuPrompt;
    private String doneWithData;

    // Constructor
    public Cobot(Context context, LinearLayout ll) {
        this.context = context;
        this.ll = ll;

        // Assign firebase variables
        dRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Assign Prompt Strings from resources
        menuPrompt = context.getResources().getString(R.string.menu_prompt);
        statMenuPrompt = context.getResources().getString(R.string.statistic_menu_prompt);;
        countrySpecificMenuPrompt = context.getResources().getString(R.string.country_specific_prompt);;
        doneWithData = context.getResources().getString(R.string.done_with_country);;
    }

    // Responds to message x;
    public void giveResponseToMessage(final String x, final boolean saveToFirebase) {
        generateResponse(x, saveToFirebase);
    }

    // Prompts the user
    public void preparePrompt(final boolean saveToFirebase) {
        sendPrompt(saveToFirebase);
    }

    // Prompts the user and appends the message
    private void sendPrompt(boolean saveToFirebase) {
        TextView newText;
        newText = new TextView(new ContextThemeWrapper(context, R.style.ReceivedMessage), null, 0);
        newText.setText(menuPrompt);
        if (saveToFirebase)
            addMessageToFirebase(menuPrompt, context.getString(R.string.cobot_name));
        MyUtilities.setMargins(newText, 0, 10, 0, 10);

        ll.addView(newText);
    }

    // Responds to message x. If saveToFirebase is true, then the message is saved to firebase.
    private void generateResponse(final String x, boolean saveToFirebase) {
        TextView newText;
        newText = new TextView(new ContextThemeWrapper(context, R.style.ReceivedMessage), null, 0);
        boolean reset = false;
        String response = "";
        if (stage == 0) {
            if (x.equals("1") || x.contains("1")) {
                response = statMenuPrompt;
                currentTargetCountry = "global";
                stage = 1;
            } else if (x.equals("2") || x.contains("2")) {
                response = countrySpecificMenuPrompt;
                stage = 2;
            } else {
                response = giveErrorResponse();
                stage = 0;
                reset = true;
            }
        } else if (stage == 1) {
            String stat = "";
            if (x.contains("1")) {
                stat = context.getResources().getString(R.string.all);
            } else if (x.contains("2")) {
                stat = context.getResources().getString(R.string.total_cases);
            } else if (x.contains("3")) {
                stat = context.getResources().getString(R.string.new_cases);
            } else if (x.contains("4")) {
                stat = context.getResources().getString(R.string.total_deaths);
            } else if (x.contains("5")) {
                stat = context.getResources().getString(R.string.new_deaths);
            } else if (x.contains("6")) {
                stat = context.getResources().getString(R.string.total_recoveries);
            } else if (x.contains("7")) {
                stat = context.getResources().getString(R.string.new_recoveries);
            } else {
                response = giveErrorResponse();
                stage = 0;
                reset = true;
            }

            if (stage != 0) {
                // Get response according to stat given
                response = getResult(stat, currentTargetCountry);
                stage = 3;
            }
        } else if (stage == 2) {
            if (getCountriesData(x) != null) {
                currentTargetCountry = x;
                response = statMenuPrompt;
                stage = 1;
            } else {
                response = giveErrorResponse();
                stage = 0;
                reset = true;
            }
        } else if (stage == 3) {
            if (x.equalsIgnoreCase(context.getResources().getString(R.string.yes)) || x.equalsIgnoreCase(context.getResources().getString(R.string.y))) {
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

        if (stage == 0 && response.equalsIgnoreCase("") || reset) {
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

    // Randomly decides an error response to send
    private String giveErrorResponse() {
        String[] responses = context.getResources().getStringArray(R.array.error_responses);

        int i = r.nextInt(responses.length);
        return responses[i];
    }

    // Retrieves a string containing all the stats for global data.
    private String retrieveGlobalData() {
        String globalData = context.getResources().getString(R.string.total_confirmed_label) + totalConfirmed + "\n"
                + context.getResources().getString(R.string.new_confirmed_label) + newConfirmed + "\n"
                + context.getResources().getString(R.string.total_deaths_label) + totalDeaths + "\n"
                + context.getResources().getString(R.string.new_deaths_label) + newDeaths + "\n"
                + context.getResources().getString(R.string.total_recovered_label) + totalRecovered + "\n"
                + context.getResources().getString(R.string.new_recovered_label) + newRecovered;
        return globalData;
    }

    // Gets the result of the query.
    private String getResult(String stat, String country) {
        // Global data
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
            // Including a country
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

    // Adds a message to firebase
    private void addMessageToFirebase(String content, String sender) {
        String id = dRef.push().getKey();
        String curTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String curDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Message m = new Message(id, content, sender, curTime, curDate);
        dRef.child(mAuth.getUid()).child("conversations").child(m.getDate()).child(id).setValue(m);
    }

    // Given the name of a country, finds that countries data from the ArrayList of countries
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
