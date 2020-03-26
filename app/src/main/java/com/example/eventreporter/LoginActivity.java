package com.example.eventreporter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mSubmitButton;
    private Button mRegisterButton;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //write a message to database
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("message");

        //myRef.setValue("Hello, world");


        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUsernameEditText = (EditText) findViewById(R.id.editTextLogin);
        mPasswordEditText = (EditText) findViewById(R.id.editTextPassword);
        mSubmitButton = (Button) findViewById(R.id.submit);
        mRegisterButton = (Button) findViewById(R.id.register);

        // need to update the activity_login.xml with com.google.android.gms.ads.AdView
        //AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameEditText.getText().toString();
                final String password = Utils.md5Encryption(mPasswordEditText.getText().toString());
                //final String password = mPasswordEditText.getText().toString();
                final User user = new User(username, password, System.currentTimeMillis());
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(username)){
                            Toast.makeText(getBaseContext(), "username is already registered, please change one",
                                    Toast.LENGTH_SHORT).show();
                        } else if (!username.equals("") && !password.equals("")) {
                            //put username as key to set value
                            mDatabase.child("users").child(user.getUsername()).setValue(user);
                            Toast.makeText(getBaseContext(), "Successfully registered",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsernameEditText.getText().toString();
                final String password = Utils.md5Encryption(mPasswordEditText.getText().toString());
                //final String password = mPasswordEditText.getText().toString();
                mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(username) &&
                                (password.equals(dataSnapshot.child(username).child("password").getValue()))) {
                            //Log.i("Your log", "You successfully login");
                            Toast.makeText(getBaseContext(), "You successfully login", Toast.LENGTH_SHORT).show();
                            // when login, jump to main activity page
                            Intent myIntent = new Intent(LoginActivity.this, EventActivity.class);
                            Utils.username = username;
                            startActivity(myIntent);
                            //finish(); // click go back can't see login page
                        } else {
                            Toast.makeText(getBaseContext(), "Please login again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
