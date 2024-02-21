package com.example.scraps.DBModels;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseError;


public class Registration {

    public interface DatabaseOperationCallback {
        void onSuccess();
        void onFailure();
    }

    private FirebaseDatabase database;

    public Registration() {
        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://scraps-dbdd1-default-rtdb.europe-west1.firebasedatabase.app");
    }

    public void createHousehold(String userName, String userEmail, String userPassword) {
        DatabaseReference myRef = database.getReference("households");

        // Create a new Household
        String houseID = myRef.push().getKey(); // Automatically generate a unique ID for the household
        Households newHousehold = new Households(houseID);

        // Save the household
        myRef.child(houseID).setValue(newHousehold);

        // Add a user to this household
        Users newUser = new Users(userName, userEmail, userPassword, houseID);
        newHousehold.setAdminUser(newUser);
        String userID = myRef.child(houseID).child("users").push().getKey(); // Automatically generate a unique ID for the user
        myRef.child(houseID).child("users").child(userID).setValue(newUser);
    }

    public void addUserToHousehold(String userName, String userEmail, String userPassword, String houseID, DatabaseOperationCallback callback)
    {
        DatabaseReference usersRef = database.getReference("households").child(houseID).child("users");
        Users newUser = new Users(userName, userEmail, userPassword, houseID);

        // Attempt to add a new user to the household
        usersRef.push().setValue(newUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    // If there's an error, invoke onFailure on the callback
                    callback.onFailure();
                } else {
                    // If the data was saved successfully, invoke onSuccess on the callback
                    callback.onSuccess();
                }
            }
        });
    }
}


// Registration registration = new Registration();
//registration.addUserToHousehold("Jane Doe", "janedoe@example.com", "securePassword", "houseID456", new Registration.DatabaseOperationCallback() {
//    @Override
//    public void onSuccess() {
//        // Code to execute on successful addition of the user
//        System.out.println("User successfully added.");
//    }
//
//    @Override
//    public void onFailure() {
//        // Code to execute if adding the user fails
//        System.out.println("Failed to add user.");
//    }
//});