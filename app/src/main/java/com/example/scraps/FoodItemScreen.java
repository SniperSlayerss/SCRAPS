package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.scraps.DBModels.FoodItem;


public class FoodItemScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_screen);

        Intent intent = getIntent();
        FoodItem foodItem = (FoodItem) intent.getSerializableExtra("foodItem");

        //ADD IMAGE OF FOOD

        //ADD PROFILE IMAGE

        //Loads correct text for each section of UI
        TextView Item = findViewById(R.id.item);
        String ItemName = "NO NAME";
        if (foodItem != null) {
            ItemName = foodItem.getFoodName();
        }
        Item.setText(String.format("Item: %s", ItemName));

        TextView User = findViewById(R.id.user);
        String UserName = "user name";
        User.setText(String.format("User: %s", UserName));

        TextView Purchased = findViewById(R.id.purchased);
        String PurchaseDate = foodItem.getPurchaseDate();
        Purchased.setText(String.format("Purchased: %s", PurchaseDate));

        TextView UseBy = findViewById(R.id.useBy);
        String UseByDate = foodItem.getExpiryDate();
        UseBy.setText(String.format("Use By: %s", UseByDate));

        Button removeButton = findViewById(R.id.remove);
        removeButton.setOnClickListener(new View.OnClickListener() {

            /*
            public void onClick(View view) {

                foodItem.removeFoodItem();

                Intent intent = new Intent(view.getContext(), FoodDatabaseScreenActivity.class);
                startActivity(intent);
            }
            */
            @Override
            public void onClick(View view) {
                //Alert dialog to confirm the user wants to remove
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Confirmation")
                    .setMessage("Are you sure you want to remove this item?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //foodItem.removeFoodItem();

                            Intent intent = new Intent(view.getContext(), FoodDatabaseScreenActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }



}