package com.example.scraps.DBModels;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Registration {

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    public Registration() {
        // Initialize Firebase Database and Auth
        database = FirebaseDatabase.getInstance("https://scraps-dbdd1-default-rtdb.europe-west1.firebasedatabase.app");
        mAuth = FirebaseAuth.getInstance();
    }

    public interface DatabaseOperationCallback {
        void onSuccess(String houseID);
        void onFailure(String message);
    }

    public void registerUserAndCreateHousehold(String userName, String userEmail, String userPassword, final DatabaseOperationCallback callback) {
        DatabaseReference usersRef = database.getReference("usernames");

        // Check if the username already exists
        usersRef.child(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username already exists
                    callback.onFailure("Username already exists.");
                } else {
                    // Username does not exist, proceed with Firebase Auth user registration
                    mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    // Authentication successful, proceed with user and household creation
                                    createUserAndHousehold(userName, userEmail, userPassword, callback);
                                } else {
                                    // Authentication failed, handle error
                                    callback.onFailure("Authentication failed: " + task.getException().getMessage());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible database errors
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    private void createUserAndHousehold(String userName, String userEmail, String userPassword, final DatabaseOperationCallback callback) {
        // Assume the user is successfully created in Firebase Auth, now create the household
        String houseID = database.getReference("households").push().getKey();
        Households newHousehold = new Households(houseID);
        Users newUser = new Users(userName, userEmail, userPassword, houseID);

        DatabaseReference myRef = database.getReference("households").child(houseID);
        myRef.setValue(newHousehold).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                myRef.child("users").child(mAuth.getCurrentUser().getUid()).setValue(newUser)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                // Update the usernames reference to prevent duplicate usernames
                                database.getReference("usernames").child(userName).setValue(mAuth.getCurrentUser().getUid())
                                        .addOnSuccessListener(aVoid -> callback.onSuccess(houseID))
                                        .addOnFailureListener(e -> callback.onFailure("Failed to register username."));
                            } else {
                                callback.onFailure("Failed to add user to household.");
                            }
                        });
            } else {
                callback.onFailure("Failed to create household.");
            }
        });
    }
}
