package com.example.scraps.DBModels;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration {

    private FirebaseDatabase database;

    public Registration() {
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://scraps-dbdd1-default-rtdb.europe-west1.firebasedatabase.app");
    }

    public void addHouseholdWithUser(String userName, String userEmail, String userPassword) {
        DatabaseReference myRef = database.getReference("households");

        // Create a new Household
        String houseID = myRef.push().getKey(); // Automatically generate a unique ID for the household
        Households newHousehold = new Households(houseID);

        // Save the household
        myRef.child(houseID).setValue(newHousehold);

        // Add a user to this household
        Users newUser = new Users(userName, userEmail, userPassword, houseID);
        String userID = myRef.child(houseID).child("users").push().getKey(); // Automatically generate a unique ID for the user
        myRef.child(houseID).child("users").child(userID).setValue(newUser);
    }
}
