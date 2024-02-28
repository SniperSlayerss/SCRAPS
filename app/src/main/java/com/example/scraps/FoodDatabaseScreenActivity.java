package com.example.scraps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.scraps.DBModels.FoodItemAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodDatabaseScreenActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    NavigationView navigationView;
    List<FoodItem> foodItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private FoodItemAdapter adapter;
    private List<FoodItem> foodItemsList = new ArrayList<>();
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_database_screen);

        recyclerView = findViewById(R.id.rvFoodList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAuth = FirebaseAuth.getInstance();


        readFoodItemsForHousehold(mAuth.getUid());

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

    private void updateFoodList(Map<String, FoodItem> foodItems) {
        foodItemsList.clear();
        foodItemsList.addAll(foodItems.values());

        if (adapter == null) {
            // Initialize the adapter if it hasn't been initialized yet
            adapter = new FoodItemAdapter(this, foodItemsList, new FoodItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(FoodItem foodItem) {
                    Intent detailIntent = new Intent(FoodDatabaseScreenActivity.this, FoodItemScreen.class);
                    detailIntent.putExtra("FoodItem", foodItem); // foodItem must be Serializable
                    startActivity(detailIntent);
                }
            });
            recyclerView.setAdapter(adapter);
        } else {
            // Notify the adapter if it's already initialized
            adapter.notifyDataSetChanged();
        }
    }

    private void readFoodItemsForHousehold(String householdId) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference householdRef = database.getReference("households").child(householdId).child("userIDs");

        householdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This map will hold all the food items from all users in the household
                Map<String, FoodItem> householdFoodItems = new HashMap<>();

                for (DataSnapshot userIdSnapshot : dataSnapshot.getChildren()) {
                    // For each user ID, fetch the food items
                    String userId = userIdSnapshot.getKey();
                    DatabaseReference userFoodItemsRef = database.getReference("Users").child(userId).child("foodItems");

                    userFoodItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot foodItemSnapshot : dataSnapshot.getChildren()) {
                                // Assuming FoodItem has a constructor that accepts a DataSnapshot
                                FoodItem foodItem = foodItemSnapshot.getValue(FoodItem.class);
                                householdFoodItems.put(foodItemSnapshot.getKey(), foodItem);
                            }
                            // Call a method to update the UI with household food items
                            updateFoodList(householdFoodItems);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("DatabaseError", "Error reading user food items: " + databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("DatabaseError", "Error reading household users: " + databaseError.getMessage());
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