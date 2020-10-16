package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;

public class ChatActivity extends AppCompatActivity {
    EditText input = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        input = (EditText) findViewById(R.id.chat_box_send_message);
    }

    public void processMessageSent(View view) {
        String msg = input.getText().toString();
        Log.d("Sent Message", msg);
        input.setText("");
    }
}