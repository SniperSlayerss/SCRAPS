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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scraps.DBModels.Households;
import com.example.scraps.DBModels.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Household extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView houseIdTextView;
    private TextView emailTextView;
    private TextView housematesTextView;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.household);

        initializeViews();
        fetchUserData();
        setupNavigationDrawer();
    }

    private void initializeViews() {
        houseIdTextView = findViewById(R.id.houseIDNum);
        emailTextView = findViewById(R.id.houseEmailVal);
        housematesTextView = findViewById(R.id.housemate);
    }

    private void fetchUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(currentUserId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Users user = dataSnapshot.getValue(Users.class);
                        if (user != null) {
                            String householdId = user.getHouseID();
                            houseIdTextView.setText(householdId);
                            String emailID = user.getEmail();
                            emailTextView.setText(emailID);

                            fetchHouseholdData(householdId, emailID);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });
        }
    }

    private void fetchHouseholdData(String householdId, String emailID) {
        DatabaseReference householdRef = FirebaseDatabase.getInstance().getReference()
                .child("Households").child(householdId);

        householdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Households household = snapshot.getValue(Households.class);
                    if (household != null) {
                        String emailOfHousehold = household.getHouseEmail();
                        emailTextView.setText(emailOfHousehold);
                        List<Users> usersWithEmail = household.getUsersByEmail(emailID);

                        StringBuilder stringBuilder = new StringBuilder();
                        for (Users user : usersWithEmail) {
                            stringBuilder.append(user.getUsername()).append("\n");
                        }
                        String housematesString = stringBuilder.toString();
                        housematesTextView.setText(housematesString);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }

    private void setupNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.menu_home);

        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        TextView title = findViewById(R.id.title);

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
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }
}
