package com.example.scraps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

public class Notifications extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private LinearLayout navigationMenuLayout;
    NavigationView navigationView;

    private TextView notification1;
    private SwitchCompat notifsToggle;
    private TextView notification2;

    private SwitchCompat foodExpiry_Switch;
    private TextView foodExpiry1;
    private TextView foodExpiry2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications);

        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        TextView title = findViewById(R.id.title);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        //navigationView.setCheckedItem(R.id.menu_settings);

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


        notifsToggle = findViewById(R.id.notifications_switch);
        notification1 = findViewById(R.id.notification_1);
        notification2 = findViewById(R.id.notification_2);
        foodExpiry_Switch = findViewById(R.id.FoodExpiry_switch);
        foodExpiry1 = findViewById(R.id.FoodExpiry1);
        foodExpiry2 = findViewById(R.id.FoodExpiry2);


        notifsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notification2.setText("ON");
                    hideOptions(false);
                }
                else {
                    notification2.setText("OFF");
                    hideOptions(true);
                }
            }
        });







    }

    private void hideOptions (boolean bool1) {
        if (bool1) {
            foodExpiry1.setVisibility(View.GONE);
            foodExpiry2.setVisibility(View.GONE);
            foodExpiry_Switch.setChecked(false);
            foodExpiry_Switch.setVisibility(View.GONE);
        }
        else {
            foodExpiry1.setVisibility(View.VISIBLE);
            foodExpiry2.setVisibility(View.VISIBLE);

            foodExpiry_Switch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
        } else if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodItemScreen.class); //v.context() lets you access current class
            startActivity(intent);
        } else if (itemID == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class); //v.context() lets you access current class
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }
}