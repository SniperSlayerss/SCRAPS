package com.example.scraps.DBModels;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.security.MessageDigest;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.squareup.picasso.Picasso;

public class Users {
    private String username, password, email, houseID, firebaseID;
    private HashMap<String, FoodItem> foodItems;

    public Users() {}

    public Users(String username, String email, String houseID, String firebaseID) {
        this.username = username;
        this.email = email;
        this.houseID = houseID;
        this.foodItems = new HashMap<>();
        this.firebaseID = firebaseID;
    }

    public void fetchUserData(String firebaseId, final UserDataCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(firebaseId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The user data exists
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null) {
                        // Pass the user data to the callback
                        callback.onUserDataReceived(user);
                    } else {
                        // Handle the case where user data could not be converted to Users class
                        callback.onFailure("Could not convert data to Users class.");
                    }
                } else {
                    // Handle the case where user data does not exist
                    callback.onFailure("User data does not exist.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible database errors
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    // Interface for callback after user data is retrieved or failed
    public interface UserDataCallback {
        void onUserDataReceived(Users user);
        void onFailure(String message);
    }

    public void addFoodItemToUser(FoodItem foodItem, Uri filePath) {
        if (filePath != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference foodImagesRef = storageReference.child("food_images/" + UUID.randomUUID().toString());

            // Upload the file to Firebase Storage
            foodImagesRef.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Get a URL to the uploaded content
                        foodImagesRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            // Update the foodItem object with the image URL before saving
                            foodItem.setImageURL(downloadUrl.toString());
                            saveFoodItemToDatabase(foodItem);
                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        System.out.println("Failed to upload image: " + exception.getMessage());
                    });
        } else {
            // Proceed to save the food item if there is no image to upload
            saveFoodItemToDatabase(foodItem);
        }
    }
    public void fetchAndDisplayFoodItemImage(String foodItemId, ImageView imageView) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference foodItemRef = databaseRef.child("foodItems").child(foodItemId);

        foodItemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Use Picasso or any other library to load the image
                    Picasso.get().load(imageUrl).into(imageView);
                } else {
                    Log.e("fetchImage", "Image URL is null or empty for food item ID: " + foodItemId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("fetchImage", "Database error while fetching image: " + databaseError.getMessage());
            }
        });
    }
    private void saveFoodItemToDatabase(FoodItem foodItem) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        String key = databaseReference.child("Users").child(foodItem.getUserID()).child("foodItems").push().getKey();

        if (key == null) {
            throw new NullPointerException("Couldn't generate a key for the food item.");
        }

        // Set the key to the food item
        foodItem.setFoodID(key);

        Map<String, Object> foodItemValues = foodItem.toMap();

        // Save the food item, now including the image URL, to Firebase Database
        databaseReference.child("Users").child(foodItem.getUserID()).child("foodItems").child(key).setValue(foodItemValues)
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Food item added successfully with image.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Failed to add food item: " + e.getMessage());
                });
    }



    public void removeFoodItemFromDatabase(String foodItemId) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference foodItemsRef = database.child("Users").child(this.firebaseID)
                .child("foodItems")
                .child(foodItemId);
        foodItemsRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    System.out.println("Food item successfully removed from database.");
                } else {
                    System.out.println("Failed to remove food item from database: " + databaseError.getMessage());
                }
            }
        });
    }

    public static String getSHA(String input) throws NoSuchAlgorithmException
    {
        // Static getInstance method is called with hashing SHA
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // digest() method called
        return toHexString(md.digest(input.getBytes(StandardCharsets.UTF_8)));
    }

    public static String toHexString(byte[] hash)
    {
        // Convert byte array into signum representation
        BigInteger number = new BigInteger(1, hash);

        // Convert message digest into hex value
        StringBuilder hexString = new StringBuilder(number.toString(16));

        // Pad with leading zeros
        while (hexString.length() < 64)
        {
            hexString.insert(0, '0');
        }

        return hexString.toString();
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("houseID", houseID);
        // Add other properties to the map if needed
        return result;
    }

    public String getUsername() {
        return username;
    }

    public void setName(String username) {
        this.username = username;
    }

    public String getHouseID() {return houseID;}
    public void setHouseID(String houseID) {this.houseID = houseID;}

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashMap<String, FoodItem> getFoodItems() {
        return foodItems;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirebaseID() {
        return firebaseID;
    }

    public void setFirebaseID(String firebaseID) {
        this.firebaseID = firebaseID;
    }
}
