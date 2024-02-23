package com.example.scraps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.AlertDialog;
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
            navigationView.setCheckedItem(R.id.menu_food_item);
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

        Intent intent = getIntent();
        FoodItem foodItem = (FoodItem) intent.getSerializableExtra("foodItem");

        //ADD IMAGE OF FOOD

        //ADD PROFILE IMAGE

        //ADD FLAG AS COMMUNAL AND SEND NOTIFICATIONS

        //Loads correct text for each section of UI
        TextView Item = findViewById(R.id.item);
        String ItemName = "NO NAME";
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


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_food_item) {
            //since we are already on home page, do nothing and let window close
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