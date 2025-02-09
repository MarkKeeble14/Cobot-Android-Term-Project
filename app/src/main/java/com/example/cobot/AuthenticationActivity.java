package com.example.cobot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthenticationActivity extends AppCompatActivity {
    EditText emailField = null;
    EditText passwordField = null;

    TextView accDisplay = null;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        ((Button) findViewById(R.id.sign_up_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        ((Button) findViewById(R.id.log_in_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithEmailAndPassword();
            }
        });

        ((Button) findViewById(R.id.log_out_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                goToChat();
            }
        });

        ((Button) findViewById(R.id.oldChats)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldChat();
            }
        });

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        emailField = (EditText) findViewById(R.id.email_field);
        passwordField = (EditText) findViewById(R.id.password_field);
        accDisplay = (TextView) findViewById(R.id.temp_account_display);

        BottomNavigationView bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.nav_news:
                        Intent iHome = new Intent(AuthenticationActivity.this, NewsActivity.class);
                        startActivity(iHome);
                        break;
                    case R.id.nav_chat:
                        Intent iChat = new Intent(AuthenticationActivity.this, ChatActivity.class);
                        startActivity(iChat);
                        break;
                    case R.id.nav_auth:
                        break;
                }
                return false;
            }
        });
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null)
            accDisplay.setText(currentUser.getEmail());
        else
            accDisplay.setText(R.string.no_user_signed_in);
    }

    //Function to create an account to firebase
    private void createAccount() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            goToChat();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(AuthenticationActivity.this, R.string.failed_authentication,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //A function for signing in with email and password
    private void signInWithEmailAndPassword() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            goToChat();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(AuthenticationActivity.this, R.string.failed_authentication,
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    //A function to get user data, unused
    private void getUserData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
    }

    //When the chat button is clicked, will take you to the chat activity
    public void goToChat() {
        emailField.setText("");
        passwordField.setText("");

        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }

    //When the old chat button is clicked, will take you to the old chat activity
    public void oldChat() {
        if (mAuth.getCurrentUser() != null) {
            Intent i = new Intent(this, OldChatActivity.class);
            startActivity(i);
        } else {
            Toast.makeText(AuthenticationActivity.this, R.string.not_signed_in,
                    Toast.LENGTH_SHORT).show();
        }
    }
}