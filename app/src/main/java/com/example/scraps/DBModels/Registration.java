package com.example.scraps.DBModels;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.scraps.DBModels.Households;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
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
                            storeUserDetails(firebaseAuthId, userName, userEmail, houseID, false, callback);
                        }
                    } else {
                        callback.onFailure("Authentication failed: " + task.getException().getMessage());
                    }
                });
    }

    private void addPendingUserToHousehold(String houseID, String firebaseAuthId, final DatabaseOperationCallback callback) {
        DatabaseReference userRef = database.getReference("households").child(houseID).child("usersToAccept").child(firebaseAuthId);
        userRef.setValue(false)
                .addOnSuccessListener(aVoid -> callback.onSuccess("User added to household successfully."))
                .addOnFailureListener(e -> callback.onFailure("Failed to add user to household: " + e.getMessage()));
    }

    private void storeUserDetails(String firebaseAuthId, String userName, String userEmail, String houseID, boolean isMember, final DatabaseOperationCallback callback) {
        // Reference to the new user
        DatabaseReference userRef = database.getReference("Users").child(firebaseAuthId);

        // Prepare user details
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("username", userName);
        userDetails.put("email", userEmail);
        userDetails.put("firebaseID", firebaseAuthId);
        userDetails.put("houseID", houseID.isEmpty() ? firebaseAuthId : houseID);

        // Update user details
        userRef.setValue(userDetails).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // If a houseID is provided, also add this user under that household's userIDs
                if (!houseID.isEmpty()) {
                    DatabaseReference householdUserRef = database.getReference("households").child(houseID).child("userIDs").child(firebaseAuthId);
                    householdUserRef.setValue(isMember)
                            .addOnSuccessListener(aVoid -> callback.onSuccess("User details stored successfully and added to household."))
                            .addOnFailureListener(e -> callback.onFailure("Failed to add user to household: " + e.getMessage()));
                } else {
                    callback.onSuccess("User details stored successfully without a household.");
                }
            } else {
                if (task.getException() != null) {
                    callback.onFailure("Failed to store user details: " + task.getException().getMessage());
                } else {
                    callback.onFailure("Failed to store user details for an unknown reason.");
                }
            }
        });
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
        Query query = householdsRef.orderByChild("houseEmail").equalTo(email);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String householdId = snapshot.getKey();
                        addPendingUserToHousehold(householdId, firebaseAuthId, callback);
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

    public void checkHouseholdExists(final String email, final DatabaseOperationCallback callback) {
        DatabaseReference householdsRef = database.getReference("households");
        householdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean found = false;
                Log.d("RegistrationDebug", "Checking households. Total count: " + dataSnapshot.getChildrenCount());

                for (DataSnapshot householdSnapshot : dataSnapshot.getChildren()) {
                    String emailValue = householdSnapshot.child("houseEmail").getValue(String.class);
                    Log.d("RegistrationDebug", "Household " + householdSnapshot.getKey() + " email: '" + emailValue + "'");

                    if (emailValue != null && email.trim().equalsIgnoreCase(emailValue.trim())) {
                        found = true;
                        String householdId = householdSnapshot.getKey();
                        Log.d("RegistrationDebug", "Matching email found in household: " + householdId);
                        callback.onSuccess(householdId);
                        return;
                    }
                }

                if (!found) {
                    Log.d("RegistrationDebug", "No matching email found in any household.");
                    callback.onFailure("No household found with the provided email.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("RegistrationDebug", "DatabaseError: " + databaseError.getMessage());
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
