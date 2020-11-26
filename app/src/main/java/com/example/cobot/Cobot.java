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

    private String menuPrompt = "What would you like to know about?:\n1: Global Statistics\n2: Country Specific Statistics\n";

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

        String response;
        if (x.equals("1")) {
            response = retrieveGlobalData();
        } else if (x.equals("2")) {
            response = retrieveCountrySpecificData();
        } else {
            response = giveErrorResponse();
        }
        newText.setText(response);
        MyUtilities.setMargins(newText, 0, 15, 0, 15);
        ll.addView(newText);
        if (saveToFirebase)
            addMessageToFirebase(response, context.getString(R.string.cobot_name));

        preparePrompt(saveToFirebase);
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
}
