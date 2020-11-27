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
        mDbRef = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid()).child("conversations");

        date = findViewById(R.id.chatDatePicker);
        dateButton = findViewById(R.id.datePickerButton);

        messageListView = findViewById(R.id.msgListView);
        messageList = new ArrayList<Message>();

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshReadings();
            }
        });
    }


    public void refreshReadings() {
        int day = date.getDayOfMonth();
        int month = date.getMonth();
        int year = date.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formatDate = sdf.format(calendar.getTime());
        System.out.println(formatDate);
        DatabaseReference newDbRef = mDbRef.child(formatDate);


        newDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Message msg = ds.getValue(Message.class);
                        messageList.add(msg);
                    }
                    OldChatListAdapter adapter = new OldChatListAdapter(OldChatActivity.this, messageList);
                    messageListView.setAdapter(adapter);

                } else {
                    Toast.makeText(OldChatActivity.this, "Sorry, there doesn't seem to" +
                            " be any messages from this date, try a new one", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}