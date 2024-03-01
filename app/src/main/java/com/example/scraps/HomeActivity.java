package com.example.scraps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import android.widget.TextView;


import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private LinearLayout navigationMenuLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        UpdateExpiryTextView();

        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        TextView title = findViewById(R.id.title);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.menu_home);

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

    // Method to handle button click and open the settings activity
    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openFoodItemDatabase(View view) {
        Intent intent = new Intent(this, FoodDatabaseScreenActivity.class);
        startActivity(intent);
    }

    public void openAddFoodItem(View view) {
        Intent intent = new Intent(this, FoodInputActivity.class);
        startActivity(intent);
    }


    // EXAMPLE METHODS TO INTERACT WITH FIREBASE REALTIME DATABASE
    private void readFromDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Food").child("item").child("name");
        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void writeToDatabase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users").child("user1").child("name");

        myRef.setValue("joe");
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
     * NOTE: Encapsulate into Users?
     * @param currentUser
     * @param numberOfDays
     * @return
     */
    private ArrayList<FoodItem> GetExpiringFoodItems(Users currentUser, Integer numberOfDays){
        if (currentUser.getFoodItems().isEmpty()){
            return new ArrayList<FoodItem>();
        }
        else{
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE);
            ArrayList<FoodItem> foodItems = new ArrayList((currentUser.getFoodItems()).values());
            ArrayList<FoodItem> output = new ArrayList<>();
            Date desiredDate = new Date();
            int newDay = desiredDate.getDate() + numberOfDays.intValue();
            desiredDate.setDate(newDay);
            Date currentDate = new Date();
            Boolean exceptionCaught = false;
            for (FoodItem f : foodItems){
                try{
                    Date expiryDate = dateFormatter.parse(f.getExpiryDate());
                    if (expiryDate.compareTo(desiredDate) <= 0 && expiryDate.compareTo(currentDate) >= 0){
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
    private void UpdateExpiryTextView(){
        // TEST CODE
        Users testUser = new Users("Mr. Test", "someone@example.com", "p@ssW0rd123", "2394");
        testUser.TESTMETHOD(new FoodItem("Eggs", "27/02/2024", "23/02/2024", 3.50, "Testing food", false));
        testUser.TESTMETHOD(new FoodItem("Fish", "28/02/2024", "23/02/2024", 3.50, "Testing food", false));
        testUser.TESTMETHOD(new FoodItem("Milk", "29/02/2024", "23/02/2024", 3.50, "Testing food", false));
        testUser.TESTMETHOD(new FoodItem("Bread", "01/03/2024", "23/02/2024", 3.50, "Testing food", false));
        testUser.TESTMETHOD(new FoodItem("Canned Tuna", "02/03/2024", "23/02/2024", 3.50, "Testing food", false));
        // TEST CODE END
        TextView expiryReminder = findViewById(R.id.expiryReminder);
        ArrayList<FoodItem> expiring = GetExpiringFoodItems(testUser, 2);
        if (expiring.isEmpty()){
            expiryReminder.setText("Nothing expiring soon");
        }
        else{
            Random rnd = new Random();
            StringBuilder sb = new StringBuilder();
            sb.append("Your '");
            sb.append(expiring.get(rnd.nextInt(expiring.size())).getFoodName());
            sb.append("' item is expiring on ");
            Date date = new Date();
            DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH);
            sb.append(dateFormatter.format(date));
            expiryReminder.setText(sb.toString());
        }

    }
}