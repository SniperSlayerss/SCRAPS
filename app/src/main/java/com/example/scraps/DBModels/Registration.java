package com.example.scraps.DBModels;

import com.example.scraps.DBModels.Households;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registration {

    private final FirebaseDatabase database;
    private final FirebaseAuth mAuth;

    public Registration() {
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    public interface DatabaseOperationCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public void registerUser(String userEmail, String userPassword, String userName, String houseID, final DatabaseOperationCallback callback) {
        mAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String firebaseAuthId = firebaseUser.getUid();
                            // After successful registration, store user details
                            storeUserDetails(firebaseAuthId, userName, userEmail, houseID, callback);
                        }
                    } else {
                        callback.onFailure("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private void storeUserDetails(String firebaseAuthId, String userName, String userEmail, String houseID, final DatabaseOperationCallback callback) {
        DatabaseReference userRef = database.getReference("Users").child(firebaseAuthId);
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", userName);
        userDetails.put("email", userEmail);
        userDetails.put("firebaseID", firebaseAuthId);
        if (Objects.equals(houseID, ""))
        {
            userDetails.put("houseID", firebaseAuthId);
        }
        else
        {
            userDetails.put("houseID", houseID);
        }


        userRef.setValue(userDetails)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User details stored successfully."))
                .addOnFailureListener(e -> callback.onFailure("Failed to store user details: " + e.getMessage()));
    }
    public void createOrJoinHousehold(String householdEmail, boolean createHousehold, final DatabaseOperationCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User is not authenticated.");
            return;
        }
        String firebaseAuthId = currentUser.getUid();
        String email = currentUser.getEmail(); // Get current user's email

        if (createHousehold) {
            // Use current user's email as the key to find the household
            createHousehold(firebaseAuthId, email, callback);
        } else {
            // Join existing household by email
            findHouseholdByEmail(householdEmail, firebaseAuthId, callback);
        }
    }

    public void createHousehold(String houseID, String email, DatabaseOperationCallback callback) {
        DatabaseReference householdsRef = database.getReference("households");
        Households newHousehold = new Households(houseID, email);

        householdsRef.child(houseID).setValue(newHousehold)
                .addOnSuccessListener(aVoid -> {
                    // Successfully created new household
                    callback.onSuccess("Household created successfully with ID: " + houseID);
                    // You may also want to update the current user's profile with the new household ID here
                })
                .addOnFailureListener(e -> callback.onFailure("Failed to create household: " + e.getMessage()));
        joinHousehold(houseID, houseID, callback);
    }


    private void findHouseholdByEmail(String email, String firebaseAuthId, final DatabaseOperationCallback callback) {
        DatabaseReference householdsRef = database.getReference("households");
        Query query = householdsRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String householdId = snapshot.getKey();
                        joinHousehold(householdId, firebaseAuthId, callback);
                        return;
                    }
                } else {
                    callback.onFailure("No household found with the provided email.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure("Failed to find household: " + databaseError.getMessage());
            }
        });
    }

    public void checkHouseholdExists(String email, final DatabaseOperationCallback callback) {
        DatabaseReference householdsRef = database.getReference("households");
        Query query = householdsRef.orderByChild("email").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String householdId = snapshot.getKey();
                        return;
                    }
                } else {
                    callback.onFailure("No household found with the provided email.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure("Failed to find household: " + databaseError.getMessage());
            }
        });
    }


    private void joinHousehold(String houseID, String firebaseAuthId, final DatabaseOperationCallback callback) {
        DatabaseReference userRef = database.getReference("households").child(houseID).child("userIDs").child(firebaseAuthId);
        userRef.setValue(true)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User added to household successfully."))
                .addOnFailureListener(e -> callback.onFailure("Failed to add user to household: " + e.getMessage()));
    }
}
