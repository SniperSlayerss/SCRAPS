package com.example.scraps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.HouseholdMemberAdapter;
import com.example.scraps.DBModels.Households;
import com.example.scraps.DBModels.Login;
import com.example.scraps.DBModels.Registration;
import com.example.scraps.DBModels.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    public interface BooleanCallback {
        void onCompleted(Boolean isMember);
        void onError(String databaseError);
    }


    private EditText emailInput, passwordInput;
    private Button loginButton, registerButton;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openRegisterScreenActivity();
            }
        });

    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Password and Email can't be empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email and password must not be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase login
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fetchUserDetails(new BooleanCallback() {
                    @Override
                    public void onCompleted(Boolean isMember) {
                        if (isMember) {
                            Toast.makeText(LoginActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();
                            // Navigate to Home Screen
                            openHomeScreenActivity();
                        }
                        else{
                            Toast.makeText(LoginActivity.this, "Not accepted into the household.", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }
                    }

                    @Override
                    public void onError(String databaseError) {
                        mAuth.signOut();
                        Toast.makeText(LoginActivity.this, "Not a member.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Sign in failed.", Toast.LENGTH_SHORT).show();
            }
        });
        // The misplaced else block is removed. Error handling should be done inside the if(task.isSuccessful()) check.
    }



    public void openRegisterScreenActivity() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void openHomeScreenActivity() {
        Intent intent = new Intent(this, Privacy.class);
        startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Do nothing to disable the back button
    }

    public void isUserMember(String householdId, String userID, BooleanCallback callback) {
        DatabaseReference householdMemberRef = FirebaseDatabase.getInstance().getReference()
                .child("households").child(householdId).child("userIDs").child(userID);

        householdMemberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Boolean isMember = dataSnapshot.getValue(Boolean.class);
                if (isMember != null) {
                    callback.onCompleted(isMember); // Return true or false based on isMember value
                } else {
                    callback.onCompleted(false); // Default to false if userID is not found
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError("Error!"); // Handle error
            }
        });
    }

    private void fetchUserDetails(BooleanCallback callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Reference to the "households" node
            DatabaseReference householdsRef = FirebaseDatabase.getInstance().getReference("households");

            householdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot householdSnapshot : dataSnapshot.getChildren()) {
                        // Navigate to the "userIDs" node for the household
                        DataSnapshot userIdsSnapshot = householdSnapshot.child("userIDs");

                        // Check if the user ID exists in this "userIDs" node
                        if (userIdsSnapshot.hasChild(mAuth.getCurrentUser().getUid())) {
                            // Retrieve the value associated with the user ID
                            Boolean isUserPartOfHousehold = userIdsSnapshot.child(mAuth.getCurrentUser().getUid()).getValue(Boolean.class);

                            // Now you have the value (true/false) associated with the user
                            // You can handle it as needed in your application logic
                            if (isUserPartOfHousehold != null) {
                                // User is part of the household, handle the success case
                                callback.onCompleted(isUserPartOfHousehold);
                            }
                            return; // Stop the loop since we found the user
                        }
                    }
                    // If we've iterated over all households and not found the user ID
                    callback.onError("User ID not found in any household.");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                    callback.onError(databaseError.toException().toString());
                }
            });

        } else {
            callback.onError("No authenticated user found.");
        }
    }

}