package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private EditText input = null;
    private LinearLayout ll;
    private Cobot cobot;

    private FirebaseAuth mAuth;
    private DatabaseReference dRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        input = (EditText) findViewById(R.id.chat_box_send_message);

        //Bottom navigation
        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_news:
                        Intent iMain = new Intent(ChatActivity.this, NewsActivity.class);
                        startActivity(iMain);
                        break;
                    case R.id.nav_chat:
                        break;
                    case R.id.nav_auth:
                        Intent iSched = new Intent(ChatActivity.this, AuthenticationActivity.class);
                        startActivity(iSched);
                        break;
                }
                return false;
            }
        });

        dRef = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Prompt
        ll = (LinearLayout) findViewById(R.id.chat_box_messages);
        cobot = new Cobot(ChatActivity.this, ll);

        boolean saveToFirebase = mAuth.getCurrentUser() != null;
        cobot.preparePrompt(saveToFirebase);
        TextView lgad = (TextView) findViewById(R.id.logged_in_as_display);
        if (saveToFirebase) {
            lgad.setVisibility(View.VISIBLE);
            lgad.setText(getString(R.string.logged_in_as) + mAuth.getCurrentUser().getEmail());
        } else {
            lgad.setVisibility(View.INVISIBLE);
        }
    }

    // A function to process messages sent
    public void processMessageSent(View view) {
        String msg = input.getText().toString();

        // Create new TextView
        TextView newText = new TextView(new ContextThemeWrapper(this, R.style.SentMessage), null, 0);
        newText.setText(msg);
        MyUtilities.setMargins(newText, 0, 15, 0, 15);
        ll.addView(newText);

        // Get Response
        boolean saveToFirebase = mAuth.getCurrentUser() != null;
        cobot.giveResponseToMessage(msg, saveToFirebase);
        if (saveToFirebase)
            addMessageToFirebase(msg, getString(R.string.sent_message_sender));

        // Reset Input Field
        input.setText("");
    }

    public void goToAuth(View view) {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        startActivity(intent);
    }

    //A function to add messages to firebase
    private void addMessageToFirebase(String content, String sender) {
        String id = dRef.push().getKey();
        //Setting current time and date for firebase key
        String curTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String curDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        Message m = new Message(id, content, sender, curTime, curDate);
        Task setValueMSG = dRef.child(mAuth.getUid()).child("conversations").child(m.getDate()).child(id).setValue(m);

        //On success make success toast
        setValueMSG.addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(ChatActivity.this, R.string.message_added,Toast.LENGTH_LONG).show();
            }
        });

        //On failure make fail toast
        setValueMSG.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this,
                        getString(R.string.unable_to_save_msg) + e.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}