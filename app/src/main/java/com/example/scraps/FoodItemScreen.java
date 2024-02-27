package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.app.AlertDialog;
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

        //ADD FLAG AS COMMUNAL AND SEND NOTIFICATIONS

        //Loads correct text for each section of UI
        TextView Item = findViewById(R.id.item);
        String ItemName = "NO NAME ERROR";
        if (foodItem != null) {
            ItemName = foodItem.getFoodName();
        }
        Item.setText(String.format("Item: %s", ItemName));

        TextView User = findViewById(R.id.user);
        String UserName = "USERNAME ERROR";
        if (foodItem != null) {
            UserName = foodItem.getUsername();
        }
        User.setText(String.format("User: %s", UserName));

        TextView Purchased = findViewById(R.id.purchased);
        String PurchaseDate = "PURCHASE DATE ERROR";
        if (foodItem != null) {
            PurchaseDate = foodItem.getPurchaseDate();
        }
        Purchased.setText(String.format("Purchased: %s", PurchaseDate));

        TextView UseBy = findViewById(R.id.useBy);
        String UseByDate = "USE BY DATE ERROR";
        if (foodItem != null) {
            UseByDate = foodItem.getExpiryDate();
        }
        UseBy.setText(String.format("Use By: %s", UseByDate));

        Button removeButton = findViewById(R.id.remove);
        removeButton.setOnClickListener(view -> {
            //Alert dialog to confirm the user wants to remove
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setTitle("Confirmation")
                .setMessage("Are you sure you want to remove this item?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {

                    if (foodItem != null) {
                        foodItem.removeFoodItem();
                    }

                    Intent intent1 = new Intent(view.getContext(), FoodDatabaseScreenActivity.class);
                    startActivity(intent1);
                })
                .setNegativeButton("No", (dialogInterface, i) -> {

                });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });
    }



}