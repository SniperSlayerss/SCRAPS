package com.example.scraps;

import android.app.Dialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class FoodInputActivity extends AppCompatActivity {

    private EditText foodNameEditText, expiryDateEditText, purchaseDateEditText, priceEditText, typeEditText;
    private Button submitButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_input);

        foodNameEditText = findViewById(R.id.food_name_editText);
        expiryDateEditText = findViewById(R.id.expiry_date_editText);
        purchaseDateEditText = findViewById(R.id.purchase_date_editText);
        priceEditText = findViewById(R.id.price_editText);
        typeEditText = findViewById(R.id.type_editText);
        submitButton = findViewById(R.id.submit_button);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.pickDate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "datePicker");
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get input values
                String foodName = foodNameEditText.getText().toString().trim();
                String expiryDate = expiryDateEditText.getText().toString().trim();
                String purchaseDate = purchaseDateEditText.getText().toString().trim();
                double price = Double.parseDouble(priceEditText.getText().toString().trim());
                String type = typeEditText.getText().toString().trim();
                String username = "";

                String firebaseId = mAuth.getUid();

                // Create FoodItem object
                FoodItem foodItem = new FoodItem(foodName, expiryDate, purchaseDate, price, type, false, username);

                // Add food to user
                // Create an instance of Users
                Users users = new Users();

                // Call fetchUserData with the Firebase ID and a new callback
                users.fetchUserData(firebaseId, new Users.UserDataCallback() {
                    @Override
                    public void onUserDataReceived(Users user) {
                        user.addFoodItemToUser(foodItem);
                    }

                    @Override
                    public void onFailure(String message) {
                        // This is called when there is an error retrieving the user data.
                        // Here you can update the UI to show an error message.
                        Log.e("UserData", "Error retrieving user data: " + message);
                        // Handle the error
                    }
                });


                // Clear input fields
                foodNameEditText.setText("");
                expiryDateEditText.setText("");
                purchaseDateEditText.setText("");
                priceEditText.setText("");
                typeEditText.setText("");

                // Show success message or handle as per requirement
            }
        });
    }
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default values for the picker.
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it.
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // Handle the date set event
            // Update the relevant EditText or perform any other actions
            String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            // Update the EditText with the selected date
            // For example: (assuming expiryDateEditText is the corresponding EditText)
            // expiryDateEditText.setText(selectedDate);
        }
    }
}
