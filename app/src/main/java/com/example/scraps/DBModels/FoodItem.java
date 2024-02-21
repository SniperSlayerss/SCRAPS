package com.example.scraps.DBModels;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private String name;
    private String expiry;

    public FoodItem(String foodName, String expiryDate) {
        this.name = foodName;
        this.expiry = expiryDate;
    }

    public FoodItem() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String description) {
        this.expiry = description;
    }

    // Constructor, getters, and setters
}

