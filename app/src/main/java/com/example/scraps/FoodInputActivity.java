package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.Users;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FoodInputActivity extends AppCompatActivity {

    private FoodItem foodItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_input);

        Button submit_button = findViewById(R.id.add_item);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Example usage
                EditText name_box = findViewById(R.id.name);
                String name = name_box.getText().toString();

                EditText date_box = findViewById(R.id.expiry_date);
                String expiry_date = date_box.getText().toString();

                EditText price_box = findViewById(R.id.expiry_date);
                Integer price = Integer.parseInt(date_box.getText().toString());

                EditText food_box = findViewById(R.id.food_type);
                String food_type = food_box.getText().toString();

                foodItem = new FoodItem(name, expiry_date, price, food_type);

                // create function to add the food created to the user
            }
        });
    }
}