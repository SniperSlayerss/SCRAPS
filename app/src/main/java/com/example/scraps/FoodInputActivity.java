package com.example.scraps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scraps.DBModels.FoodItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class FoodInputActivity extends AppCompatActivity {

    private EditText foodNameEditText, expiryDateEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_input);

        Button scanButton = findViewById(R.id.scan_button);
        Button submitButton = findViewById(R.id.submit_button);
        foodNameEditText = findViewById(R.id.food_name_editText);
        expiryDateEditText = findViewById(R.id.expiry_date_editText);

        // Set OnClickListener for the scan button
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Initialize ZXing integrator
                IntentIntegrator integrator = new IntentIntegrator(FoodInputActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0); // Use the device's default camera
                integrator.setBeepEnabled(true); // Disable beep sound
                integrator.setBarcodeImageEnabled(true); // Save the captured barcode image
                integrator.initiateScan();
            }
        });

        // Set OnClickListener for the submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the input data
                String foodName = foodNameEditText.getText().toString().trim();
                String expiryDate = expiryDateEditText.getText().toString().trim();

                // Validate the input data
                if (foodName.isEmpty() || expiryDate.isEmpty()) {
                    // Show error message if any field is empty
                    // Handle validation as per your requirement
                    return;
                }

                // Write to Firebase Database
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference foodRef = database.getReference("Food");

                // Push new food item to the database
                String itemId = foodRef.push().getKey();
                FoodItem foodItem = new FoodItem(foodName, expiryDate);
                foodRef.child(itemId).setValue(foodItem);

                // Optionally, you can show a success message or navigate to another activity
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of the barcode scanning
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Barcode scanned successfully
                // Set the scanned barcode value to the food name EditText
                foodNameEditText.setText(result.getContents());
            } else {
                // Barcode scanning cancelled or failed
                // Handle as per your requirement
            }
        }
    }
}
