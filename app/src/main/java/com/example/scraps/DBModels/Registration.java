package com.example.scraps.DBModels;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Registration {

    private FirebaseDatabase database;

    public Registration() {
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://scraps-dbdd1-default-rtdb.europe-west1.firebasedatabase.app");
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
                    // Username does not exist, proceed with user and household creation
                    createUserAndHousehold(userName, userEmail, userPassword, callback);
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
        DatabaseReference myRef = database.getReference("households");
        String houseID = myRef.push().getKey(); // Automatically generate a unique ID for the household

        // Assuming Households and Users classes are correctly defined elsewhere
        Households newHousehold = new Households(houseID);
        Users newUser = new Users(userName, userEmail, userPassword, houseID);

        // Save the new household
        myRef.child(houseID).setValue(newHousehold, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Household creation was successful, now add the user
                    DatabaseReference usersRef = database.getReference("households").child(houseID).child("users");
                    usersRef.child(userName).setValue(newUser, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                // User addition was successful, also update the usernames reference
                                DatabaseReference usernamesRef = database.getReference("usernames");
                                usernamesRef.child(userName).setValue(true, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            callback.onSuccess(houseID);
                                        } else {
                                            callback.onFailure("Failed to register username.");
                                        }
                                    }
                                });
                            } else {
                                callback.onFailure("Failed to add user to household.");
                            }
                        }
                    });
                } else {
                    callback.onFailure("Failed to create household.");
                }
            }
        });
    }
}
