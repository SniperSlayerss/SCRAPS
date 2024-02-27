package com.example.scraps;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.scraps.DBModels.FoodItem;
import com.example.scraps.DBModels.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setSupportActionBar(findViewById(R.id.my_toolbar));
    }

    // Method to handle button click and open the settings activity
    public void openSettingsActivity(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openFoodItemScreen(View view) {
        Intent intent = new Intent(this, FoodItemScreen.class);
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

    /**
     * Gets all food items attached to a user that are set to expire between the current day and a specified number of days in the future.
     * NOTE: Encapsulate into Users?
     * @param currentUser
     * @param numberOfDays
     * @return
     */
    private ArrayList<FoodItem> GetExpiringFoodItems(Users currentUser, Integer numberOfDays){
        ArrayList<FoodItem> foodItems = new ArrayList((currentUser.getFoodItems()).values());
        IntDate desiredDate = new IntDate();
        desiredDate.AddDays(numberOfDays);
        for (FoodItem f : foodItems){
            IntDate expiryDate = new IntDate(f.getExpiryDate());
            if (IntDate.LessThanEqualTo(expiryDate, desiredDate) && IntDate.GreaterThanEqualTo(expiryDate, IntDate.CurrentDate())){
                foodItems.add(f);
            }
        }
        return foodItems;
    }




}