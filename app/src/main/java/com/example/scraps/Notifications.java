package com.example.scraps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
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
    private SwitchCompat foodSharing;
    private TextView foodSharing_1;
    private TextView foodSharing_2;
    private SwitchCompat thirdOption;
    private TextView thirdOption_1;
    private TextView thirdOption_2;
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
        foodSharing = findViewById(R.id.FoodSharing_Switch);
        foodSharing_1 = findViewById(R.id.FoodSharing_1);
        foodSharing_2 = findViewById(R.id.FoodSharing_2);
        thirdOption = findViewById(R.id.ThirdOptionSwitch);
        thirdOption_1 = findViewById(R.id.ThirdOption_1);
        thirdOption_2 = findViewById(R.id.ThirdOption_2);
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        notifsToggle.setChecked(preferences.getBoolean("notificationsSwitch", false));
        foodExpiry_Switch.setChecked(preferences.getBoolean("foodExpirySwitch", false));
        foodSharing.setChecked(preferences.getBoolean("foodSharingSwitch", false));
        thirdOption.setChecked(preferences.getBoolean("thirdOptionSwitch", false));

        if (notifsToggle.isChecked()) {
            notification2.setText("ON");
        }
        if (foodExpiry_Switch.isChecked()) {
            foodExpiry2.setText("ON");
        }
        if (foodSharing.isChecked()) {
            foodSharing_2.setText("ON");
        }
        if (thirdOption.isChecked()) {
            thirdOption_2.setText("ON");
        }



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

        foodSharing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    foodSharing_2.setText("ON");

                }
                else {
                    foodSharing_2.setText("OFF");

                }
            }
        });

        foodExpiry_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    foodExpiry2.setText("ON");

                }
                else {
                    foodExpiry2.setText("OFF");

                }
            }
        });

        thirdOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    thirdOption_2.setText("ON");

                }
                else {
                    thirdOption_2.setText("OFF");

                }
            }
        });







    }

    private void hideOptions (boolean bool1) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (bool1) {
            foodExpiry1.setVisibility(View.GONE);
            foodExpiry2.setVisibility(View.GONE);
            foodSharing_1.setVisibility(View.GONE);
            foodSharing_2.setVisibility((View.GONE));
            thirdOption_1.setVisibility(View.GONE);
            thirdOption_2.setVisibility(View.GONE);

            //foodExpiry_Switch.setChecked(false);
            foodExpiry_Switch.setVisibility(View.GONE);
            //foodSharing.setChecked(false);
            foodSharing.setVisibility(View.GONE);
            //thirdOption.setChecked(false);
            thirdOption.setVisibility(View.GONE);

            editor.putBoolean("NotificationSwitch", notifsToggle.isChecked());
            editor.putBoolean("foodExpirySwitch", foodExpiry_Switch.isChecked());
            editor.putBoolean("foodSharingSwitch", foodSharing.isChecked());
            editor.putBoolean("ThirdOption", thirdOption.isChecked());

        }
        else {
            foodExpiry1.setVisibility(View.VISIBLE);
            foodExpiry2.setVisibility(View.VISIBLE);
            foodSharing_1.setVisibility(View.VISIBLE);
            foodSharing_2.setVisibility((View.VISIBLE));
            thirdOption_1.setVisibility(View.VISIBLE);
            thirdOption_2.setVisibility(View.VISIBLE);

            foodExpiry_Switch.setVisibility(View.VISIBLE);
            foodSharing.setVisibility(View.VISIBLE);
            thirdOption.setVisibility(View.VISIBLE);
        }
        editor.apply();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
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
