package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.scraps.DBModels.Registration;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private Button registerButton, loginButton;
    private CheckBox checkBoxCreateOrJoin;
    private EditText emailInput, passwordInput, usernameInput, householdIdInput;
    private FirebaseAuth mAuth;

    public interface HouseholdOperationCallback {
        void onHouseholdSuccess(String message);

        void onHouseholdFailure(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        registerButton = findViewById(R.id.register_button);
        loginButton = findViewById(R.id.login_button);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        usernameInput = findViewById(R.id.username_input);
        householdIdInput = findViewById(R.id.householdIdInput);
        checkBoxCreateOrJoin = findViewById(R.id.checkbox_create_or_join);

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
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String username = usernameInput.getText().toString().trim();
        String householdEmail = householdIdInput.getText().toString().trim();
        boolean isCreate = checkBoxCreateOrJoin.isChecked();


        if (!email.isEmpty() && !password.isEmpty() && !username.isEmpty()) {
            Registration registration = new Registration();
            if (isCreate) {
                // Register the new user with a new household
                registration.registerUser(email, password, username, "",  new Registration.DatabaseOperationCallback() {
                    @Override
                    public void onSuccess(String message) {
                        Toast.makeText(RegistrationActivity.this, "Registration successful.", Toast.LENGTH_SHORT).show();
                        registration.createHousehold(mAuth.getCurrentUser().getUid(), email, new Registration.DatabaseOperationCallback() {
                            @Override
                            public void onSuccess(String householdMessage) {
                                // Household creation successful
                                Toast.makeText(RegistrationActivity.this, "Registration and household creation successful: " + householdMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String householdErrorMessage) {
                                // Household creation failed
                                Toast.makeText(RegistrationActivity.this, "Household creation failed: " + householdErrorMessage, Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(RegistrationActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Check if a household exists before proceeding with registration
                registration.checkHouseholdExists(householdEmail, new Registration.DatabaseOperationCallback() {
                     @Override
                    public void onSuccess(String householdId) {
                        // Household found, proceed with registration
                        registration.registerUser(email, password, username, householdId, new Registration.DatabaseOperationCallback() {
                            @Override
                            public void onSuccess(String message) {
                                // Associate the user with the householdId in your database
                                Toast.makeText(RegistrationActivity.this, "Registration successful with household association.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                Toast.makeText(RegistrationActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        // No household associated, abort registration
                        Toast.makeText(RegistrationActivity.this, "No existing household associated with this email.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        } else {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }



        private void handleHouseholdOperation (HouseholdOperationCallback householdOperationCallback)
        {
            String householdId = householdIdInput.getText().toString().trim();
            boolean shouldCreateHousehold = checkBoxCreateOrJoin.isChecked();

            Registration registration = new Registration();
            registration.createOrJoinHousehold(householdId, shouldCreateHousehold, new Registration.DatabaseOperationCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();
                    // Redirect to another activity or update UI as needed
                }

                @Override
                public void onFailure(String errorMessage) {
                    Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void openLoginScreenActivity () {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
