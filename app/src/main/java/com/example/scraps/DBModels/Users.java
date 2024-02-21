package com.example.scraps.DBModels;
import java.util.HashMap;

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
