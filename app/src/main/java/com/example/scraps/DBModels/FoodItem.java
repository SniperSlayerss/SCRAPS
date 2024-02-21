package com.example.scraps.DBModels;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class FoodItem implements Serializable {
    public String foodName;
    public String expiryDate;
    public double price;
    public String type;

    public FoodItem() {}

    public FoodItem(String foodName, String expiryDate, double price, String type) {
        this.foodName = foodName;
        this.expiryDate = expiryDate;
        this.price = price;
        this.type = type;
    }

    private void addFoodItemToUser(String houseID, String userID, FoodItem foodItem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String key = databaseReference.child("households").child(houseID).child("users").child(userID).child("foodItems").push().getKey();
        Map<String, Object> foodItemValues = foodItem.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/households/" + houseID + "/users/" + userID + "/foodItems/" + key, foodItemValues);

        databaseReference.updateChildren(childUpdates);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("foodName", foodName);
        result.put("expiryDate", expiryDate);
        result.put("price", price);
        result.put("type", type);
        return result;
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
}

