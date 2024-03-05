package com.example.scraps.DBModels;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FoodItem implements Serializable {
    private String foodName, expiryDate, purchaseDate, userID, foodID, username;
    private double price;
    private boolean isShareable;
    private FirebaseAuth mAuth;
    public FoodItem() {}

    public FoodItem(String foodName, String expiryDate, String purchaseDate, String userID, String username, double price, boolean isShareable) {
        this.foodName = foodName;
        this.expiryDate = expiryDate;
        this.price = price;
        this.isShareable = false;
        this.purchaseDate = purchaseDate;
        this.userID = userID;
        this.username = username;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("foodName", foodName);
        result.put("expiryDate", expiryDate);
        result.put("price", price);
        result.put("isShareable", isShareable);
        result.put("purchaseDate", purchaseDate);
        result.put("foodID", foodID);
        result.put("userID", userID);
        result.put("username", username);
        return result;
    }

    public void removeFoodItem() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference foodItemsRef = database.child("Users")
                .child(mAuth.getUid()) // assuming userId is the user's unique ID
                .child("foodItems")
                .child(foodName); // assuming foodName is the unique identifier of the food item

        foodItemsRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Food item removed successfully
                    System.out.println("Food item successfully removed from database.");
                } else {
                    // An error occurred while removing the food item
                    System.out.println("Failed to remove food item from database: " + databaseError.getMessage());
                }
            }
        });
    }


    public String getFoodName() {
        return foodName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public double getPrice() {
        return price;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isShareable() {
        return isShareable;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setShareable(boolean shareable) {
        isShareable = shareable;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

