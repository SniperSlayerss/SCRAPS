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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private LinearLayout navigationMenuLayout;
    private Button settingsButton, addFoodButton, foodDatabaseButton;
    NavigationView navigationView;
    private RecyclerView expiry;
    private FoodItemAdapter adapter;
    private FirebaseAuth mAuth;
    private List<FoodItem> foodItemsList = new ArrayList<>();
    private RecyclerView recyclerView;

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
        }
        else if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        else if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    /**
     * Gets all food items attached to a user that are set to expire between the current day and a specified number of days in the future.
     * @param currentUser
     * @param numberOfDays
     * @return
     */
    private ArrayList<FoodItem> GetExpiringFoodItems(Users currentUser, Integer numberOfDays){
        if (currentUser.getFoodItems() == null){
            return new ArrayList<FoodItem>();
        }
        if (currentUser.getFoodItems().isEmpty()){
            return new ArrayList<FoodItem>();
        }
        else{
            SimpleDateFormat dateFormatter = new SimpleDateFormat("d-M-yyyy");
            ArrayList<FoodItem> foodItems = new ArrayList((currentUser.getFoodItems()).values());
            ArrayList<FoodItem> output = new ArrayList<>();
            Date desiredDate = new Date();
            int newDay = desiredDate.getDate() + numberOfDays.intValue();
            desiredDate.setDate(newDay);
            Date yesterday = new Date();
            yesterday.setDate(yesterday.getDate()-1);
            yesterday.setHours(23);
            yesterday.setMinutes(59);
            yesterday.setSeconds(59);
            Boolean exceptionCaught = false;
            for (FoodItem f : foodItems){
                try{
                    Date expiryDate = dateFormatter.parse(f.getExpiryDate());
                    if (expiryDate.compareTo(desiredDate) <= 0 && expiryDate.compareTo(yesterday) >= 0){
                        output.add(f);
                    }
                }
                catch (ParseException e){
                    exceptionCaught = true;
                    break;
                }
            }
            if (exceptionCaught){
                return new ArrayList<FoodItem>();
            }
            else{
                return output;
            }
        }

    }

    /**
     * Written weirdly for testing purposes, ideally the current user should be accessible from the activity but for now I'm using a test user defined in scope.
     * Currently picks a random FoodItem from the array until I decide how I want to sort the items.
     */
    private void UpdateExpiryList(){
        int numberOfDays = 2;
        TextView expiryReminder = findViewById(R.id.expiryReminder);
        TextView costText = findViewById(R.id.costText);
        Users currentUser = new Users();
        Context context = this;
        currentUser.fetchUserData(mAuth.getUid(), new Users.UserDataCallback(){
            @Override
            public void onUserDataReceived(Users user) {
                ArrayList<FoodItem> expiring = GetExpiringFoodItems(user, numberOfDays); // Number of days can be changed, potentially as a setting
                if (expiring.isEmpty()){
                    expiryReminder.setText("Nothing expiring soon");
                    costText.setText("");
                }
                else{
                    StringBuilder sb = new StringBuilder();
                    sb.append("Your food items expiring in ");
                    sb.append(numberOfDays);
                    sb.append(" days:");
                    expiryReminder.setText(sb.toString());

                    sb = new StringBuilder();
                    sb.append("Which is ");
                    double cost = 0.0;
                    for (FoodItem f : expiring){
                        cost = cost + f.getPrice();
                    }
                    NumberFormat toCurrency = NumberFormat.getCurrencyInstance(Locale.UK);
                    sb.append(toCurrency.format(cost));
                    sb.append(" of potential food waste!");
                    costText.setText(sb.toString());

                    readFoodItemsForHousehold(user.getHouseID(), user.getFirebaseID());
                    adapter = new FoodItemAdapter(context, expiring, new FoodItemAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(FoodItem foodItem) {
                            Intent detailIntent = new Intent(HomeActivity.this, FoodItemScreen.class);
                            detailIntent.putExtra("FoodItem", foodItem); // foodItem must be Serializable
                            startActivity(detailIntent);
                        }
                    });
                    expiry.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e("UserData", "Error retrieving user data: " + message);
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
}