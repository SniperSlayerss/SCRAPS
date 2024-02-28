package com.example.scraps.DBModels;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private String username, password, email, houseID, firebaseID;
    private HashMap<String, FoodItem> foodItems;

    public Users() {}

    public Users(String username, String email, String houseID, String firebaseID) {
        this.username = username;
        this.email = email;
        this.houseID = houseID;
        this.foodItems = new HashMap<>();
        this.firebaseID = firebaseID;
    }

    public void fetchUserData(String firebaseId, final UserDataCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user data exists
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null) {
                        // Pass the user data to the callback
                        callback.onUserDataReceived(user);
                    } else {
                        // Handle the case where user data could not be converted to Users class
                        callback.onFailure("Could not convert data to Users class.");
                    }
                } else {
                    // Handle the case where user data does not exist
                    callback.onFailure("User data does not exist.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible database errors
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    // Interface for callback after user data is retrieved or failed
    public interface UserDataCallback {
        void onUserDataReceived(Users user);
        void onFailure(String message);
    }

    public void addFoodItemToUser(FoodItem foodItem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String key = databaseReference.child("Users").child(firebaseID).child("foodItems").push().getKey();

        if (key == null) {
            throw new NullPointerException("Couldn't generate a key for the food item.");
        }

        Map<String, Object> foodItemValues = foodItem.toMap();

        databaseReference.child("Users").child(firebaseID).child("foodItems").child(key).setValue(foodItemValues)
                .addOnSuccessListener(aVoid -> {
                    // Successfully added food item to user's list
                    System.out.println("Food item added successfully.");
                })
                .addOnFailureListener(e -> {
                    // Failed to add food item to user's list
                    System.out.println("Failed to add food item: " + e.getMessage());
                });
    }


    public void removeFoodItemFromDatabase(String foodItemId) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference foodItemsRef = database.child("Users").child(this.firebaseID)
                .child("foodItems")
                .child(foodItemId);
        foodItemsRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    System.out.println("Food item successfully removed from database.");
                } else {
                    System.out.println("Failed to remove food item from database: " + databaseError.getMessage());
                }
            }
        });
    }

    public static String getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        return toHexString(md.digest(input.getBytes(StandardCharsets.UTF_8)));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("houseID", houseID);
        // Add other properties to the map if needed
        return result;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getHouseID() {return houseID;}
    public void setHouseID(String houseID) {this.houseID = houseID;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, FoodItem> getFoodItems() {
        return foodItems;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }
}
