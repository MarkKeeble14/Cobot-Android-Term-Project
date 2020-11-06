package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class ChatActivity extends AppCompatActivity {
    EditText input = null;
    LinearLayout ll;
    public static Cobot cobot = new Cobot();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        input = (EditText) findViewById(R.id.chat_box_send_message);
    }

    public void processMessageSent(View view) {
        String msg = input.getText().toString();

        // Get linear layout
        ll = (LinearLayout) findViewById(R.id.chat_box_messages);

        // Create new TextView
        TextView newText = new TextView(new ContextThemeWrapper(this, R.style.SentMessage), null, 0);
        newText.setText(msg);
        ll.addView(newText);

        // Get Response
        cobot.giveResponseToMessage(this, ll, msg);

        // Reset Input Field
        input.setText("");
    }

    public void goToAuth(View view) {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
    }
}