package com.example.scraps.DBModels;

import androidx.annotation.NonNull;

import com.example.scraps.HouseholdActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

public class Households {
    private String houseID, houseEmail;
    private HashMap<String, Users> usersToAccept;
    private Map<String, Boolean> userIDs;

    public Households() {}

    public Households(String houseID, String houseEmail) {
        this.houseID = houseID;
        this.houseEmail = houseEmail;
        this.userIDs = new HashMap<>();
        this.usersToAccept = new HashMap<>();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("houseID", houseID);
        result.put("houseEmail", houseEmail);  // Ensure email is included in toMap for consistency
        return result;
    }

    public String getHouseID() {
        return houseID;
    }

    public String getHouseEmail() {
        return houseEmail;
    }

    public void setHouseID(String houseID) {
        this.houseID = houseID;
    }

    public void setEmail(String email) {
        this.houseEmail = email;
    }

    public void usersToAdd(String householdId, HouseholdActivity.UsersCallback callback) {
        DatabaseReference householdMembersRef = FirebaseDatabase.getInstance().getReference()
                .child("households").child(householdId).child("userIDs");

        householdMembersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Users> usersList = new ArrayList<>();
                // Track the count of users with isMember set to false
                final long[] falseCount = {dataSnapshot.getChildrenCount()};
                for (DataSnapshot userIdSnapshot : dataSnapshot.getChildren()) {
                    String userId = userIdSnapshot.getKey();
                    Boolean isMember = userIdSnapshot.getValue(Boolean.class);

                    if (Boolean.FALSE.equals(isMember)) {
                        // Fetch the user details from the Users node
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                .child("Users").child(userId);

                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                Users user = userSnapshot.getValue(Users.class);
                                if (user != null) {
                                    usersList.add(user);
                                }
                                // Decrement falseCount and check if all false isMember users have been processed
                                falseCount[0]--;
                                if (falseCount[0] <= 0) {
                                    callback.onCompleted(usersList); // Callback when all relevant users are fetched
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                callback.onError(databaseError); // Handle individual user fetch error
                            }
                        });
                    } else {
                        // Decrement falseCount for each true isMember, as they are not processed
                        falseCount[0]--;
                        if (falseCount[0] <= 0) {
                            callback.onCompleted(usersList); // Callback if there are no false isMember users to process
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError); // Handle household members fetch error
            }
        });
    }



    // Function to get user IDs for a given household
    public CompletableFuture<Map<String, Boolean>> getUserIdsForHousehold(String householdId) {
        CompletableFuture<Map<String, Boolean>> future = new CompletableFuture<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("households").child(householdId).child("userIDs");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This unchecked cast is okay here because we know the structure of our data
                @SuppressWarnings("unchecked")
                Map<String, Boolean> userIds = (Map<String, Boolean>) dataSnapshot.getValue();
                future.complete(userIds); // Complete the future with the result
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(new Exception("Database error: " + databaseError.getMessage()));
            }
        });

        return future;
    }

    public void addUserToAccept(String userId, Users user) {
        if (usersToAccept == null) {
            usersToAccept = new HashMap<>();
        }
        usersToAccept.put(userId, user);
    }

    // Reject a user: remove from pending
    public void rejectUser(String userId) {
        if (usersToAccept != null && usersToAccept.containsKey(userId)) {
            usersToAccept.remove(userId);
        }
    }

    public Map<String, Boolean> getUserIDs() {
        return this.userIDs ;
    }

    public void setUserIDs(Map<String, Boolean> userIDs) {
        this.userIDs = userIDs;
    }
}
