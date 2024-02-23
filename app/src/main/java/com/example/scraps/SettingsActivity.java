package com.example.scraps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //setSupportActionBar(findViewById(R.id.my_toolbar));

        //COPY LINES 23-40 AND PASTE INTO onCreate FUNCTION TO USE TOOLBAR
        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        TextView title = findViewById(R.id.title);

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
                Toast.makeText(SettingsActivity.this, "MENU TOGGLE CLICKED", Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(this, Household.class);
        startActivity(intent);
    }

}
