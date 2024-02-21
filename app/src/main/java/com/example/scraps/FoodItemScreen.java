package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.scraps.DBModels.FoodItem;


public class FoodItemScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_screen);

        /*
        Intent intent = getIntent();
        FoodItem foodItem = (FoodItem) intent.getSerializableExtra("foodItem");
        // e.g.
        String foodName = foodItem.getName();
        */

        TextView Item = findViewById(R.id.item);
        String ItemName = "item name";
        Item.setText(String.format("Item: %s", ItemName));

        TextView User = findViewById(R.id.user);
        String UserName = "user name";
        User.setText(String.format("User: %s", UserName));

        TextView Purchased = findViewById(R.id.purchased);
        String PurchaseDate = "date";
        Purchased.setText(String.format("Purchased: %s", PurchaseDate));

        TextView UseBy = findViewById(R.id.useBy);
        String UseByDate = "date";
        UseBy.setText(String.format("Use By: %s", UseByDate));
    }



}