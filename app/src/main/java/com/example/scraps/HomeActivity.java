package com.example.scraps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private LinearLayout navigationMenuLayout;
    private Button settingsButton, addFoodButton, foodDatabaseButton;
    NavigationView navigationView;
    private RecyclerView expiry;
    private FoodItemAdapter adapter;
    private FirebaseAuth mAuth;
    private List<FoodItem> foodItemsList = new ArrayList<>();
    private Map<String, FoodItem> foodItemMap;
    private RecyclerView recyclerView;

    public interface FoodItemsCallback {
        void onCallback(Map<String, FoodItem> foodItems);
    }

    public interface ExpiringFoodItemsCallback {
        void onCallback(ArrayList<FoodItem> expiringFoodItems);
        void onError(String error);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        TextView title = findViewById(R.id.title);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addFoodButton = findViewById(R.id.foodAdd);
        foodDatabaseButton = findViewById(R.id.foodItem);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.menu_home);

        addFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FoodInputActivity.class); //v.context() lets you access current class
                startActivity(intent);
            }
        });

        foodDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FoodDatabaseScreenActivity.class); //v.context() lets you access current class
                startActivity(intent);
            }
        });


        expiry = findViewById(R.id.expiry);
        expiry.setLayoutManager(new LinearLayoutManager(this));
        expiry.setHasFixedSize(true);

        mAuth = FirebaseAuth.getInstance();
        UpdateExpiryList();

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class); //v.context() lets you access current class
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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

    public void openFoodItemDatabase(View view) {
        Intent intent = new Intent(this, FoodDatabaseScreenActivity.class);
        startActivity(intent);
    }

    public void openAddFoodItem(View view) {
        Intent intent = new Intent(this, FoodItemScreen.class);
        startActivity(intent);
    }

    //Copy below into java files for toolbar functionality
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_home) {
            //since we are already on home page, do nothing and let window close
        } else if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class); //v.context() lets you access current class
            startActivity(intent);
        } else if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    private void GetExpiringFoodItems(String householdId, String currentUserId, Integer numberOfDays, ExpiringFoodItemsCallback callback) {
        getFoodItemsForHousehold(householdId, currentUserId, new FoodItemsCallback() {
            @Override
            public void onCallback(Map<String, FoodItem> foodItems) {
                SimpleDateFormat dateFormatter = new SimpleDateFormat("d-M-yyyy", Locale.getDefault());
                ArrayList<FoodItem> output = new ArrayList<>();
                Calendar calendar = Calendar.getInstance();

                // Set desiredDate to the end of the day numberOfDays ahead
                calendar.add(Calendar.DATE, numberOfDays);
                setEndOfDay(calendar);
                Date desiredDate = calendar.getTime();

                // Set yesterday to the end of the previous day
                Calendar yesterdayCalendar = Calendar.getInstance();
                setStartOfDay(yesterdayCalendar);
                yesterdayCalendar.add(Calendar.DATE, -1);
                Date yesterday = yesterdayCalendar.getTime();

                for (FoodItem f : foodItems.values()) {
                    if (f.getExpiryDate() == null || f.getExpiryDate().isEmpty()) {
                        continue; // Skip if the date is null or empty
                    }
                    try {
                        Date expiryDate = dateFormatter.parse(f.getExpiryDate());
                        // Assuming you've set 'yesterday' and 'desiredDate' correctly as before
                        if (expiryDate != null && expiryDate.after(yesterday) && expiryDate.before(desiredDate)) {
                            output.add(f);
                        }
                    } catch (ParseException e) {
                        Log.e("DateParseError", "Error parsing the expiry date: " + f.getExpiryDate(), e);
                    }
                }
                callback.onCallback(output);
            }
        });
    }

    private void setEndOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    private void setStartOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    private void UpdateExpiryList() {
        final int numberOfDays = 5;
        recyclerView = findViewById(R.id.expiry);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        mAuth = FirebaseAuth.getInstance();

        Users user = new Users();
        user.fetchUserData(mAuth.getUid(), new Users.UserDataCallback() {
            @Override
            public void onUserDataReceived(Users user) {
                fetchExpiringFoodItems(user.getHouseID(), user.getFirebaseID(), numberOfDays);
            }

            @Override
            public void onFailure(String message) {
                showToast("Error retrieving user data: " + message);
            }
        });
    }

    private void fetchExpiringFoodItems(String householdId, String currentUserId, int numberOfDays) {
        GetExpiringFoodItems(householdId, currentUserId, numberOfDays, new ExpiringFoodItemsCallback() {
            @Override
            public void onCallback(ArrayList<FoodItem> expiringFoodItems) {
                updateUIWithExpiringItems(expiringFoodItems, numberOfDays);
            }

            @Override
            public void onError(String error) {
                showToast("Error loading expiring items: " + error);
            }
        });
    }

    private void updateUIWithExpiringItems(ArrayList<FoodItem> expiringFoodItems, int numberOfDays) {
        TextView expiryReminder = findViewById(R.id.expiryReminder);
        TextView costText = findViewById(R.id.costText);

        if (expiringFoodItems.isEmpty()) {
            expiryReminder.setText("Nothing expiring soon");
            costText.setText("");
        } else {
            String reminderText = String.format(Locale.UK, "Your food items expiring in %d days:", numberOfDays);
            expiryReminder.setText(reminderText);

            double cost = calculateTotalCost(expiringFoodItems);
            String costTextString = String.format(Locale.UK, "Which is %s of potential food waste!", NumberFormat.getCurrencyInstance(Locale.UK).format(cost));
            costText.setText(costTextString);

            FoodItemAdapter adapter = new FoodItemAdapter(this, expiringFoodItems, foodItem -> {
                Intent detailIntent = new Intent(this, FoodItemScreen.class);
                detailIntent.putExtra("FoodItem", foodItem);
                startActivity(detailIntent);
            });
            recyclerView.setAdapter(adapter);
        }
    }

    private double calculateTotalCost(ArrayList<FoodItem> expiringFoodItems) {
        double cost = 0.0;
        for (FoodItem f : expiringFoodItems) {
            cost += f.getPrice();
        }
        return cost;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }



    private void updateFoodList(Map<String, FoodItem> foodItems) {
        foodItemsList.clear();
        foodItemsList.addAll(foodItems.values());

        if (adapter == null) {
            // Initialize the adapter if it hasn't been initialized yet
            adapter = new FoodItemAdapter(this, foodItemsList, new FoodItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(FoodItem foodItem) {
                    Intent detailIntent = new Intent(HomeActivity.this, FoodItemScreen.class);
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

    private void readFoodItemsForHousehold(String householdId, String currentUserId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference householdRef = database.getReference("households").child(householdId).child("userIDs");

        householdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This map will hold all the relevant food items from users in the household
                Map<String, FoodItem> householdFoodItems = new HashMap<>();

                for (DataSnapshot userIdSnapshot : dataSnapshot.getChildren()) {
                    // For each user ID, fetch the food items
                    String userId = userIdSnapshot.getKey();
                    DatabaseReference userFoodItemsRef = database.getReference("Users").child(userId).child("foodItems");

                    userFoodItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot foodItemSnapshot : dataSnapshot.getChildren()) {
                                FoodItem foodItem = foodItemSnapshot.getValue(FoodItem.class);

                                // Check if the item is shareable or belongs to the current user
                                if (userId.equals(currentUserId) || (foodItem != null && foodItem.isShareable())) {
                                    householdFoodItems.put(foodItemSnapshot.getKey(), foodItem);
                                }
                            }
                            // Update UI method called outside the loop, ensuring aggregation of all items
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

    private void getFoodItemsForHousehold(String householdId, String currentUserId, FoodItemsCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference householdRef = database.getReference("households").child(householdId).child("userIDs");

        householdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This map will hold all the relevant food items from users in the household
                Map<String, FoodItem> householdFoodItems = new HashMap<>();

                AtomicInteger pendingRequests = new AtomicInteger((int) dataSnapshot.getChildrenCount());

                for (DataSnapshot userIdSnapshot : dataSnapshot.getChildren()) {
                    // For each user ID, fetch the food items
                    String userId = userIdSnapshot.getKey();
                    DatabaseReference userFoodItemsRef = database.getReference("Users").child(userId).child("foodItems");

                    userFoodItemsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot foodItemSnapshot : dataSnapshot.getChildren()) {
                                FoodItem foodItem = foodItemSnapshot.getValue(FoodItem.class);

                                // Check if the item is shareable or belongs to the current user
                                if (userId.equals(currentUserId) || (foodItem != null && foodItem.isShareable())) {
                                    householdFoodItems.put(foodItemSnapshot.getKey(), foodItem);
                                }
                            }
                            if (pendingRequests.decrementAndGet() == 0) {
                                // Once all requests are completed, execute the callback
                                callback.onCallback(householdFoodItems);
                            }
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