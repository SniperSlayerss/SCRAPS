package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    private Button registerButton, loginButton;
    private EditText emailInput, passwordInput, usernameInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        registration = new Registration();

        mAuth = FirebaseAuth.getInstance();
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        usernameInput = findViewById(R.id.username_input);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLoginScreenActivity();
            }
        });
    }
    private void registerUser() {
        String username = usernameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Password, Email, and Username can't be empty
        if(username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, "Username, email, and password must not be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        registration.registerUserAndCreateHousehold(username, email, password, new Registration.DatabaseOperationCallback() {
            @Override
            public void onSuccess(String houseID) {
                // On success let user know
                Toast.makeText(RegistrationActivity.this, "Registration successful!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String message) {
                // Show error, invalid registration
                Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void openLoginScreenActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}