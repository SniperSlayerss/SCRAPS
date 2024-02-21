package com.example.scraps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.scraps.DBModels.FoodItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodDatabaseScreenActivity extends AppCompatActivity {
    List<FoodItem> foodItems = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_database_screen);
        readFromDatabase();
        updateFoodList();
    }

    private void updateFoodList(){
        LinearLayout buttonContainer = findViewById(R.id.button_container);
        for (FoodItem foodItem : foodItems) {
            Button button = new Button(this);
            button.setText(foodItem.getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Intent to navigate to another activity based on the button clicked
                    Intent intent = new Intent(FoodDatabaseScreenActivity.this, FoodItemScreen.class);
                    // Pass any relevant data to the next activity if needed
                    intent.putExtra("foodItem", foodItem);
                    startActivity(intent);
                }
            });
            buttonContainer.addView(button);
        }
    }

    private void readFromDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference foodRef = database.getReference("Food");

        foodRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot foodSnapshot : dataSnapshot.getChildren()) {
                    // Loop through each child under "Food" node
                    for (DataSnapshot itemSnapshot : foodSnapshot.getChildren()) {
                        // Loop through each child under each food item node
                        String itemName = itemSnapshot.child("name").getValue(String.class);
                        String itemExpiry = itemSnapshot.child("expiry").getValue(String.class);

                        FoodItem foodItem = new FoodItem();
                        foodItem.setName(itemName);
                        foodItem.setExpiry(itemExpiry);

                        foodItems.add(foodItem);
                        updateFoodList();
                    }
                }

                // Do whatever you want with the list of food items here
                // For example, you can pass it to another method or display it in a RecyclerView
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}