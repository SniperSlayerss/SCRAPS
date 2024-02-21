package com.example.scraps.DBModels;
import java.security.MessageDigest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
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
