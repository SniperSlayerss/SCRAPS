package com.example.scraps;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.scraps.DBModels.FoodItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FoodInputActivity extends AppCompatActivity {

    private EditText foodNameEditText, expiryDateEditText, purchaseDateEditText, priceEditText, typeEditText;
    private Button submitButton;

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

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get input values
                String foodName = foodNameEditText.getText().toString().trim();
                String expiryDate = expiryDateEditText.getText().toString().trim();
                String purchaseDate = purchaseDateEditText.getText().toString().trim();
                double price = Double.parseDouble(priceEditText.getText().toString().trim());
                String type = typeEditText.getText().toString().trim();

                // Create FoodItem object
                FoodItem foodItem = new FoodItem(foodName, expiryDate, purchaseDate, price, type, false);

                // TODO: CHANGE THIS CODE TO PUT DATA IN CORRECT PLACE IN DB
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                String key = databaseReference.child("").push().getKey();
                databaseReference.child("").child(key).setValue(foodItem);

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
}
