package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.Login;
import com.example.scraps.DBModels.Registration;
import com.example.scraps.DBModels.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private Registration registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        registration = new Registration();

        Button registerButton = findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assuming you have an instance of the Registration class
                //Registration registration = new Registration();


//                Registration registration = new Registration();
//                registration.registerUserAndCreateHousehold("Jack", "jack1234@gmail.com", "urmom", new Registration.DatabaseOperationCallback() {
//                    @Override
//                    public void onSuccess(String houseID) {
//                        System.out.println("Registration and household creation successful. House ID: " + houseID);
//                    }
//
//                    @Override
//                    public void onFailure(String message) {
//                        System.err.println("Registration or household creation failed: " + message);
//                    }
//                });

                Login login = new Login();
                login.loginUserWithEmail("jack1234@gmail.com", "urmom", new Login.UserDetailsCallback() {
                    @Override
                    public void onSuccess(Users user) {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (firebaseUser != null) {
                            String firebaseUid = firebaseUser.getUid();
                            Log.d("LoginSuccess", "User name: " + user.getUsername());

                            // Assuming FoodItem class and user object are properly set up
                            FoodItem cheese = new FoodItem("cheese", "11/09/2003", "11/09/2003", 2.05f, "DAIRY", false, "Jack");

                            // Now add the food item to the user using the Firebase UID
                            user.addFoodItemToUser(cheese, firebaseUid);
                        }
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Log.e("LoginFailure", errorMessage);
                    }
                });




//                Login login = new Login();
//                login.loginUserWithEmail(firebaseUser.getEmail(), "urmom", new Login.UserDetailsCallback() {
//                    @Override
//                    public void onSuccess(Users user) {
//                        // Handle successful login and fetched user details
//                        Log.d("LoginSuccess", "User name: " + user.getUsername());
//                        user.addFoodItemToUser(new FoodItem("cheese", "11/09/2003", "11/09/2003", 2.05f, "DAIRY", false, "Jack"));
//                    }
//
//                    @Override
//                    public void onFailure(String errorMessage) {
//                        // Handle login failure or errors fetching user details
//                        Log.e("LoginFailure", errorMessage);
//                    }
//                });


                //Now, call registerUserAndCreateHousehold with a callback implementation
//                registration.registerUserAndCreateHousehold("Jack1", "jack1234@gmail.com", "urmomisgayhalol", new Registration.DatabaseOperationCallback() {
//                    @Override
//                    public void onSuccess(String houseID) {
//                        // This block is executed if the registration and household creation are successful
//                        System.out.println("Registration and household creation successful. House ID: " + houseID);
//                    }
//
//                    @Override
//                    public void onFailure(String message) {
//                        // This block is executed if there was a failure during the registration or household creation
//                        System.out.println("Registration or household creation failed: " + message);
//                   }
//                });
//
//
            }
        });
   }
}