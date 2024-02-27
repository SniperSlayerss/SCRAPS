package com.example.scraps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.scraps.DBModels.FoodItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FoodDatabaseScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    List<FoodItem> foodItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_database_screen);

        readFromDatabase();
        updateFoodList();

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

    }

    private void updateFoodList(){
        LinearLayout buttonContainer = findViewById(R.id.button_container);
        TextView noItemsTextView = findViewById(R.id.no_items_textView);
        // Check if any buttons are added to the LinearLayout
        if (buttonContainer.getChildCount() == 0) {
            // No buttons are added, show the "No items available" text
            noItemsTextView.setVisibility(View.VISIBLE);
        } else {
            // Buttons are added, hide the "No items available" text
            noItemsTextView.setVisibility(View.GONE);
        }

        for (FoodItem foodItem : foodItems) {
            Button button = new Button(this);
            button.setText(foodItem.getFoodName());
            button.setTextColor(getResources().getColor(R.color.text_green)); // Ensure you have defined text_green color in your resources
            button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button_green))); // Ensure you have defined button_green color in your resources
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
            if (buttonContainer.getChildCount() == 0) {
                // No buttons are added, show the "No items available" text
                noItemsTextView.setVisibility(View.VISIBLE);
            } else {
                // Buttons are added, hide the "No items available" text
                noItemsTextView.setVisibility(View.GONE);
            }
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
                        String itemName = itemSnapshot.child("foodName").getValue(String.class);
                        String itemExpiry = itemSnapshot.child("expiryDate").getValue(String.class);
                        String itemPrice = itemSnapshot.child("price").getValue(String.class);
                        String itemType = itemSnapshot.child("type").getValue(String.class);
                        String itemIsShareable = itemSnapshot.child("isShareable").getValue(String.class);
                        String itemPurchaseDate = itemSnapshot.child("purchaseDate").getValue(String.class);
                        String itemUsername = itemSnapshot.child("username").getValue(String.class);

                        FoodItem foodItem = new FoodItem();
                        foodItem.setFoodName(itemName);
                        foodItem.setExpiryDate(itemExpiry);

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