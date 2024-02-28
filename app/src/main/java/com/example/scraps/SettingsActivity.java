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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private LinearLayout navigationMenuLayout;
    NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //setSupportActionBar(findViewById(R.id.my_toolbar));

        //COPY LINES 23-40 AND PASTE INTO onCreate FUNCTION TO USE TOOLBAR
        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        TextView title = findViewById(R.id.title);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.menu_settings);

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), HomeActivity.class); //v.context() lets you access current class
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

    public void openNotificationsActivity(View view) {
        Intent intent = new Intent(this, Notifications.class);
        startActivity(intent);
    }

    public void openHouseholdActivity(View view) {
        Intent intent = new Intent(this, Household.class);
        startActivity(intent);
    }

    public void openPrivacyActivity(View view) {
        Intent intent = new Intent(this, Privacy.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_settings) {
            //since we are already on home page, do nothing and let window close
        } else if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class); //v.context() lets you access current class
            startActivity(intent);
        } else if (itemID == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }
}
