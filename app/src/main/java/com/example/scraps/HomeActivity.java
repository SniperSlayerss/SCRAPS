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
import java.util.Random;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setSupportActionBar(findViewById(R.id.my_toolbar));
        UpdateExpiryTextView();
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
        if (currentUser.getFoodItems().isEmpty()){
            return new ArrayList<FoodItem>();
        }
        else{
            ArrayList<FoodItem> foodItems = new ArrayList((currentUser.getFoodItems()).values());
            ArrayList<FoodItem> output = new ArrayList<>();
            IntDate desiredDate = new IntDate();
            desiredDate.AddDays(numberOfDays);
            for (FoodItem f : foodItems){
                IntDate expiryDate = new IntDate(f.getExpiryDate());
                if (IntDate.LessThanEqualTo(expiryDate, desiredDate) && IntDate.GreaterThanEqualTo(expiryDate, IntDate.CurrentDate())){
                    output.add(f);
                }
            }
            return output;
        }

    }

    /**
     * Written weirdly for testing purposes, ideally the current user should be accessible from the activity but for now I'm using a test user defined in scope.
     * Currently picks a random FoodItem from the array until I decide how I want to sort the items.
     * @param currentUser
     */
    private void UpdateExpiryTextView(){
        // TEST CODE
        Users testUser = new Users("Mr. Test", "someone@example.com", "p@ssW0rd123", "2394");
        testUser.TESTMETHOD(new FoodItem("Eggs", "27/02/2024", "23/02/2024", 3.50, "Testing food", false, "Mr. Test"));
        testUser.TESTMETHOD(new FoodItem("Fish", "28/02/2024", "23/02/2024", 3.50, "Testing food", false, "Mr. Test"));
        testUser.TESTMETHOD(new FoodItem("Milk", "29/02/2024", "23/02/2024", 3.50, "Testing food", false, "Mr. Test"));
        testUser.TESTMETHOD(new FoodItem("Bread", "01/03/2024", "23/02/2024", 3.50, "Testing food", false, "Mr. Test"));
        testUser.TESTMETHOD(new FoodItem("Canned Tuna", "02/03/2024", "23/02/2024", 3.50, "Testing food", false, "Mr. Test"));
        // TEST CODE END
        TextView expiryReminder = findViewById(R.id.expiryReminder);
        ArrayList<FoodItem> expiring = GetExpiringFoodItems(testUser, 3);
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