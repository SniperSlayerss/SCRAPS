package com.example.scraps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scraps.DBModels.FoodItem;
import com.google.android.material.navigation.NavigationView;

public class FoodItemScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_screen);

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
                Intent intent = new Intent(v.getContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        Intent intent = getIntent();
        FoodItem foodItem = (FoodItem) intent.getSerializableExtra("FoodItem");

        // Update UI elements with food item details
        TextView itemNameTextView = findViewById(R.id.item);
        TextView userNameTextView = findViewById(R.id.user);
        TextView purchaseDateTextView = findViewById(R.id.purchased);
        TextView useByDateTextView = findViewById(R.id.useBy);

        if (foodItem != null) {
            itemNameTextView.setText(String.format("Item: %s", foodItem.getFoodName()));
            purchaseDateTextView.setText(String.format("Purchased: %s", foodItem.getPurchaseDate()));
            useByDateTextView.setText(String.format("Use By: %s", foodItem.getExpiryDate()));
        }

        Button shareButton = findViewById((R.id.share));
        shareButton.setOnClickListener(view -> {

        });

        Button removeButton = findViewById(R.id.remove);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog before removing the item
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemScreen.this);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            // Perform removal action here
                            if (foodItem != null) {
                                foodItem.removeFoodItem();
                            }
                            // Return to the food database screen after removal
                            Intent intent = new Intent(FoodItemScreen.this, FoodDatabaseScreenActivity.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            // Do nothing if "No" is clicked
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class);
            startActivity(intent);
        }
        else if (itemID == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
        else if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }
}
