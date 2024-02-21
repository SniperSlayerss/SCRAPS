package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.scraps.DBModels.FoodItem;


public class FoodItemScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_screen);
        Intent intent = getIntent();
        FoodItem foodItem = (FoodItem) intent.getSerializableExtra("foodItem");
        // e.g.
        String foodName = foodItem.getName();
    }

}