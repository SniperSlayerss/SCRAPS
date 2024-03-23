package com.example.scraps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scraps.DBModels.HouseholdMemberAdapter;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;



public class HouseholdActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView houseIdTextView;
    private TextView emailTextView;
    private RecyclerView rvHousemates;
    private HouseholdMemberAdapter householdMemberAdapter;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public interface UsersCallback {
        void onCompleted(List<Users> usersList);
        void onError(DatabaseError databaseError);
    }


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
        rvHousemates = findViewById(R.id.rvHousemembers);
        rvHousemates.setLayoutManager(new LinearLayoutManager(this));

        // Initialize with an empty list
        householdMemberAdapter = new HouseholdMemberAdapter(this, new ArrayList<>(), user -> {
            // Handle user item clicks here
        });
        rvHousemates.setAdapter(householdMemberAdapter);
    }


    private void fetchUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Users user = dataSnapshot.getValue(Users.class);
                        if (user != null) {
                            String householdId = user.getHouseID();
                            houseIdTextView.setText(user.getHouseID());
                            fetchHouseholdData(householdId);
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

    private void fetchHouseholdData(String householdId) {
        DatabaseReference householdRef = FirebaseDatabase.getInstance().getReference().child("households").child(householdId);
        householdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Households household = snapshot.getValue(Households.class);
                    if (household != null) {
                        emailTextView.setText(household.getHouseEmail());
                    }
                    household.usersToAdd(householdId, new UsersCallback() {
                        @Override
                        public void onCompleted(List<Users> usersList) {
                            Log.d("HouseholdActivity", "Fetched users: " + usersList.size());
                            runOnUiThread(() -> {
                                householdMemberAdapter = new HouseholdMemberAdapter(HouseholdActivity.this, usersList, user -> {
                                    // Implement on item click if needed.
                                });
                                rvHousemates.setAdapter(householdMemberAdapter);
                                householdMemberAdapter.notifyDataSetChanged(); // Notify the adapter of the dataset change
                            });
                        }
                        @Override
                        public void onError(DatabaseError databaseError) {
                            Log.e("DATABASE", databaseError.toString());
                            // Handle errors, possibly by showing an error message to the user
                        }
                    });
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

        leftIcon.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), HomeActivity.class);
            startActivity(intent);
        });

        rightIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.END));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();
        if (itemID == R.id.menu_home) {
            startActivity(new Intent(this, HomeActivity.class));
        } else if (itemID == R.id.menu_food_item) {
            startActivity(new Intent(this, FoodDatabaseScreenActivity.class));
        } else if (itemID == R.id.menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }
}
