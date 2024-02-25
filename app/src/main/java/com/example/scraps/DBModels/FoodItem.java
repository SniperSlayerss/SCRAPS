package com.example.scraps.DBModels;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FoodItem implements Serializable {
    private String foodName, expiryDate, type, purchaseDate, username;
    private double price;
    private boolean isShareable;

    public FoodItem() {}

    public FoodItem(String foodName, String expiryDate, String purchaseDate, double price, String type, boolean isShareable, String username) {
        this.foodName = foodName;
        this.expiryDate = expiryDate;
        this.price = price;
        this.type = type;
        this.isShareable = false;
        this.purchaseDate = purchaseDate;
        this.username = username;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("foodName", foodName);
        result.put("expiryDate", expiryDate);
        result.put("price", price);
        result.put("type", type);
        result.put("isShareable", isShareable);
        result.put("purchaseDate", purchaseDate);
        result.put("username", username);
        return result;
    }

    public void removeFoodItem() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference foodItemsRef = database.child("users").child(this.username)
                .child("foodItems")
                .child(foodName);
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

    public String getFoodName() {
        return foodName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type;
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

    public void setType(String type) {
        this.type = type;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }
}

