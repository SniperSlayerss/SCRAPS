package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.my_toolbar));
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
        Intent intent = new Intent(this, Household.class);
        startActivity(intent);
    }

}
