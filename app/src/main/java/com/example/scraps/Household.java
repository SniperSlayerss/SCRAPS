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

public class Household extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private TextView houseIdTextView;
    private TextView emailTextView;

    private DrawerLayout drawerLayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.household);

        houseIdTextView = findViewById(R.id.houseIDNum);
        emailTextView = findViewById(R.id.houseEmailVal);

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
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle errors
                }
            });

        }


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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
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
}
