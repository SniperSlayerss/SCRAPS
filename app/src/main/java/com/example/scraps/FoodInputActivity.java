package com.example.scraps;

import android.app.Dialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class FoodInputActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    private EditText foodNameEditText, expiryDateEditText, purchaseDateEditText, priceEditText, typeEditText;
    private Button submitButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_input);

        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class); //v.context() lets you access current class
                startActivity(intent);
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            //button to open menu
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "MENU TOGGLE CLICKED", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

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
                FoodItem foodItem = new FoodItem(foodName, expiryDate, purchaseDate, firebaseId, username, price, type, false);

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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        else if (itemID == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        else if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }
}
