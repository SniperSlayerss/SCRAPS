package com.example.scraps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.FoodItemAdapter;
import com.example.scraps.DBModels.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private Button addFoodButton, foodDatabaseButton;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private FoodItemAdapter adapter;
    private FirebaseAuth mAuth;
    private List<FoodItem> foodItemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeUI();

        mAuth = FirebaseAuth.getInstance();
        UpdateExpiryList();
    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addFoodButton = findViewById(R.id.foodAdd);
        foodDatabaseButton = findViewById(R.id.foodItem);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.menu_home);

        addFoodButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FoodInputActivity.class);
            startActivity(intent);
        });

        foodDatabaseButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, FoodDatabaseScreenActivity.class);
            startActivity(intent);
        });

        recyclerView = findViewById(R.id.expiry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        adapter = new FoodItemAdapter(this, new ArrayList<>(), this::onFoodItemClick);
        recyclerView.setAdapter(adapter);
    }

    private void onFoodItemClick(FoodItem foodItem) {
        Intent detailIntent = new Intent(HomeActivity.this, FoodItemScreen.class);
        detailIntent.putExtra("FoodItem", foodItem);
        startActivity(detailIntent);
    }

    private void UpdateExpiryList() {
        // Placeholder for actual data fetching logic
        updateFoodList(new HashMap<>());
    }

    private void updateFoodList(Map<String, FoodItem> foodItems) {
        foodItemsList.clear();
        foodItemsList.addAll(foodItems.values());

        if (adapter != null) {
            adapter.updateData(foodItemsList); // Make sure this method exists in your adapter
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_home) {
            // Do nothing or refresh home data
        } else if (itemId == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class);
            startActivity(intent);
        } else if (itemId == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    private ArrayList<FoodItem> GetExpiringFoodItems(Users currentUser, Integer numberOfDays) {
        ArrayList<FoodItem> expiringFoodItems = new ArrayList<>();
        if (currentUser == null || currentUser.getFoodItems() == null || currentUser.getFoodItems().isEmpty()) {
            return expiringFoodItems;
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("d-M-yyyy");
        Date today = new Date();
        for (Map.Entry<String, FoodItem> entry : currentUser.getFoodItems().entrySet()) {
            FoodItem foodItem = entry.getValue();
            try {
                Date expiryDate = dateFormatter.parse(foodItem.getExpiryDate());
                if (expiryDate != null && !today.after(expiryDate)) {
                    long diff = expiryDate.getTime() - today.getTime();
                    long diffDays = diff / (24 * 60 * 60 * 1000);
                    if (diffDays <= numberOfDays) {
                        expiringFoodItems.add(foodItem);
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing date: " + foodItem.getExpiryDate(), e);
            }
        }

        return expiringFoodItems;
    }

    private void readFoodItemsForHousehold(String householdId, String currentUserId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference householdRef = database.getReference("households").child(householdId).child("userIDs");

        householdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, FoodItem> householdFoodItems = new HashMap<>();
                for (DataSnapshot userIdSnapshot : dataSnapshot.getChildren()) {
                    String userId = userIdSnapshot.getKey();
                    DatabaseReference userFoodItemsRef = database.getReference("Users").child(userId).child("foodItems");

                    userFoodItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot foodItemSnapshot : dataSnapshot.getChildren()) {
                                FoodItem foodItem = foodItemSnapshot.getValue(FoodItem.class);
                                if (userId.equals(currentUserId) || (foodItem != null && foodItem.isShareable())) {
                                    householdFoodItems.put(foodItemSnapshot.getKey(), foodItem);
                                }
                            }
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
}
