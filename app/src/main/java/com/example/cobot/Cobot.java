package com.example.cobot;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Cobot {
    Random r = new Random();
    final int MIN_RESPONSE_TIME = 100;
    final int MAX_RESPONSE_TIME = 2000;

    public Cobot() { }

    public void giveResponseToMessage(final Context c, final LinearLayout lin, final String x) {
        int i1 = (r.nextInt(MAX_RESPONSE_TIME) + MIN_RESPONSE_TIME);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                generateResponse(c, lin, x);
            }
        }, i1);
    }

    private void generateResponse(final Context c, final LinearLayout ll, final String x) {
        TextView newText;
        newText = new TextView(new ContextThemeWrapper(c, R.style.ReceivedMessage), null, 0);
        newText.setText("You asked: " + x);
        ll.addView(newText);
    }
}
