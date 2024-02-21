package com.example.scraps.DBModels;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private String username, password, email, houseID;
    private HashMap<String, FoodItem> foodItems;

    public Users() {}

    public Users(String username, String email, String password, String houseID) {
        this.username = username;
        this.email = email;
        this.houseID = houseID;
        this.password = password;
        this.foodItems = new HashMap<>();
    }

    private void addFoodItemToUser(FoodItem foodItem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String key = databaseReference.child("households").child(houseID).child("users").child(username).child("foodItems").push().getKey();
        Map<String, Object> foodItemValues = foodItem.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/households/" + houseID + "/users/" + username + "/foodItems/" + key, foodItemValues);

        databaseReference.updateChildren(childUpdates);
    }

    public boolean removeFoodItem(String foodItemId) {
        if (this.foodItems.containsKey(foodItemId)) {
            this.foodItems.remove(foodItemId);
            removeFoodItemFromDatabase(foodItemId);
            return true; // Item was found and removed.
        }
        return false; // Item was not found.
    }
    public void removeFoodItemFromDatabase(String foodItemId) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference foodItemsRef = database.child("users").child(this.username)
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
}
