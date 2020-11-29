package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OldChatActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;
    ListView messageListView;
    List<Message> messageList;
    DatePicker date;
    Button dateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_old_chat);

        mAuth = FirebaseAuth.getInstance();
        //Database reference
        mDbRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).child("conversations");

        date = findViewById(R.id.chatDatePicker);
        dateButton = findViewById(R.id.datePickerButton);

        messageListView = findViewById(R.id.msgListView);
        messageList = new ArrayList<Message>();

        //Date picker for which date you would like to view readings
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshReadings();
            }
        });
    }


    //A function to create the array adapter of the old messages you'd like to view
    public void refreshReadings() {
        //Creating the date key
        int day = date.getDayOfMonth();
        int month = date.getMonth();
        int year = date.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formatDate = sdf.format(calendar.getTime());

        //Creating a new database reference to read from
        DatabaseReference newDbRef = mDbRef.child(formatDate);

        newDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        //For each message in the snapshot, add it to teh messageList
                        Message msg = ds.getValue(Message.class);
                        messageList.add(msg);
                    }
                    //Create a list adapter out of the messages
                    OldChatListAdapter adapter = new OldChatListAdapter(OldChatActivity.this, messageList);
                    messageListView.setAdapter(adapter);

                } else {
                    Toast.makeText(OldChatActivity.this, "Sorry, there doesn't seem to" +
                            " be any messages from this date, try a new one", Toast.LENGTH_LONG).show();
                }
            }

            //If cancelled nothing happens
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}