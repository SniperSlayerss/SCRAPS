package com.example.scraps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.scraps.DBModels.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private LinearLayout navigationMenuLayout;
    NavigationView navigationView;

    private TextView usernameTextView;

    private TextView editUsernameMoreInfo;

    private EditText editUserField;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ToggleButton editUsernameButton = findViewById(R.id.editUsernameButton);
        usernameTextView = findViewById(R.id.usernameView_settings);
        editUsernameMoreInfo = findViewById(R.id.editUserMoreInfoText);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String currentUserID = currentUser.getUid();


            Users currentUserData = new Users();
            currentUserData.fetchUserData(currentUserID, new Users.UserDataCallback() {
                @Override
                public void onUserDataReceived(Users user) {
                    // Set the retrieved username to the TextView
                    usernameTextView.setText(user.getUsername());
                }

                @Override
                public void onFailure(String message) {
                    usernameTextView.setText("CANNOT RETRIEVE USER INFO");

                }
            });

        }
        else {
            usernameTextView.setText("USER NOT LOGGED IN!");


        }
        //setSupportActionBar(findViewById(R.id.my_toolbar));

        //COPY LINES 72-118 AND PASTE INTO onCreate FUNCTION TO USE TOOLBAR
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        editUserField = findViewById(R.id.newUserText);

        editUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUsernameButton.getText().equals("edit")) {
                    //Toast.makeText(SettingsActivity.this, "edit is the text showing rn", Toast.LENGTH_SHORT).show();
                    editUserField.setVisibility(View.GONE);
                    editUsernameMoreInfo.setText("Edit Username");
                    String new_username = editUserField.getText().toString().trim();
                    if (!new_username.isEmpty()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String currentUserID = currentUser.getUid();
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                                    .child("Users").child(currentUserID).child("username");
                            userRef.setValue(new_username)
                                    .addOnSuccessListener(aVoid -> {
                                        // Username updated successfully
                                        usernameTextView.setText(new_username);
                                        editUserField.setVisibility(View.GONE);
                                        editUsernameMoreInfo.setText("");
                                        editUsernameButton.setText("edit");
                                        Toast.makeText(SettingsActivity.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Failed to update username
                                        Toast.makeText(SettingsActivity.this, "Failed to update username: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }





                } else if (editUsernameButton.getText().equals("save")) {
                    //change username to that using EditText.getText()
                    //Toast.makeText(SettingsActivity.this, "save is the text showing  rn", Toast.LENGTH_SHORT).show();
                    editUserField.setVisibility(View.VISIBLE);
                    editUsernameMoreInfo.setText("Save?");
                }
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

    public void logoutUser(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
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
