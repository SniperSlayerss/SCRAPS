package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.scraps.DBModels.Registration;
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
                Registration registration = new Registration();

// Now, call registerUserAndCreateHousehold with a callback implementation
                registration.registerUserAndCreateHousehold("Jack", "jack123@gmail.com", "urmom", new Registration.DatabaseOperationCallback() {
                    @Override
                    public void onSuccess(String houseID) {
                        // This block is executed if the registration and household creation are successful
                        System.out.println("Registration and household creation successful. House ID: " + houseID);
                    }

                    @Override
                    public void onFailure(String message) {
                        // This block is executed if there was a failure during the registration or household creation
                        System.out.println("Registration or household creation failed: " + message);
                    }
                });
            }
        });
    }
}