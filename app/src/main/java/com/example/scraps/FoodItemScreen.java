package com.example.scraps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.scraps.DBModels.FoodItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.Toast;

import com.squareup.picasso.Transformation;

public class FoodItemScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FoodItem foodItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_item_screen);

        ImageView leftIcon = findViewById(R.id.left_icon);
        ImageView rightIcon = findViewById(R.id.right_icon);
        ImageView foodImageView = findViewById(R.id.imageView3);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

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

        Intent intent = getIntent();
        foodItem = (FoodItem) intent.getSerializableExtra("FoodItem");

        // Update UI elements with food item details
        TextView itemNameTextView = findViewById(R.id.item);
        TextView userNameTextView = findViewById(R.id.user);
        TextView purchaseDateTextView = findViewById(R.id.purchased);
        TextView useByDateTextView = findViewById(R.id.useBy);

        if (foodItem != null) {
            itemNameTextView.setText(String.format("Item: %s", foodItem.getFoodName()));
            purchaseDateTextView.setText(String.format("Purchased: %s", foodItem.getPurchaseDate()));
            useByDateTextView.setText(String.format("Use By: %s", foodItem.getExpiryDate()));
            userNameTextView.setText(String.format("Name: %s", foodItem.getUsername()));
        }

        String imageUrl = foodItem.getImageURL();
        Picasso.get()
                .load(imageUrl)
                .transform(new RotateTransformation(90)) // Adjust the rotation degrees as needed
                .into(foodImageView);

        Button removeButton = findViewById(R.id.remove);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog before removing the item
                AlertDialog.Builder builder = new AlertDialog.Builder(FoodItemScreen.this);
                builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            removeFoodItem();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        Button shareAndNotifyButton = findViewById(R.id.shareableToggle);
        shareAndNotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleShareable(foodItem);
            }
        });
    }


    public void toggleShareable(FoodItem foodItem) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference foodItemRef = database.child("Users").child(foodItem.getUserID())
                .child("foodItems")
                .child(foodItem.getFoodID())
                .child("shareable");

        // Read the current value of isShareable
        foodItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the current value and toggle it
                Boolean isShareable = dataSnapshot.getValue(Boolean.class);
                if (isShareable != null) {
                    foodItemRef.setValue(!isShareable, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                foodItem.toggleShareable();
                                if(foodItem.isShareable())
                                {
                                    Toast.makeText(FoodItemScreen.this, "Food is now being shared!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(FoodItemScreen.this, "Food is now not shared!", Toast.LENGTH_SHORT).show();
                                }

                                System.out.println("Food item shareable status successfully toggled.");
                            } else {
                                System.out.println("Failed to toggle food item shareable status: " + databaseError.getMessage());
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("Failed to read isShareable: " + databaseError.getMessage());
            }
        });
    }

    public void removeFoodItem() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference foodItemsRef = database.child("Users")
                .child(foodItem.getUserID())
                .child("foodItems")
                .child(foodItem.getFoodID());

        foodItemsRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    // Food item removed successfully
                    System.out.println("Food item successfully removed from database.");
                    // Return to the food database screen after removal
                    Intent intent = new Intent(FoodItemScreen.this, FoodDatabaseScreenActivity.class);
                    startActivity(intent);
                } else {
                    // An error occurred while removing the food item
                    System.out.println("Failed to remove food item from database: " + databaseError.getMessage());
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.menu_food_item) {
            Intent intent = new Intent(this, FoodDatabaseScreenActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.menu_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else if (itemID == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.END);
        return true;
    }

    public static class RotateTransformation implements Transformation {

        private final float rotationDegrees;

        public RotateTransformation(float rotationDegrees) {
            this.rotationDegrees = rotationDegrees;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);

            Bitmap rotatedBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

            if (rotatedBitmap != source) {
                source.recycle();
            }

            return rotatedBitmap;
        }

        @Override
        public String key() {
            return "rotate" + rotationDegrees;
        }
    }
}
