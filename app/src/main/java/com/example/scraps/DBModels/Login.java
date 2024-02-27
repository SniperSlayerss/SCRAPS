package com.example.scraps.DBModels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login {

    public interface UserDetailsCallback {
        void onSuccess(Users user);
        void onFailure(String errorMessage);
    }

    private String username, password;

    public Login() {}

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public void loginUserWithEmail(String email, String password, UserDetailsCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful, fetch user details
                            fetchUserDetails(callback);
                        } else {
                            // Login failed, handle error
                            Log.e("Login", "Login failed", task.getException());
                            callback.onFailure("Login failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    private void fetchUserDetails(UserDetailsCallback callback) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            // Reference to the "households" node
            DatabaseReference householdsRef = FirebaseDatabase.getInstance().getReference("households");

            // Iterate over each household to find the user
            householdsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot householdSnapshot : dataSnapshot.getChildren()) {
                        // Check each household's "users" node for the current user
                        Users user = householdSnapshot.child("users").child(firebaseUser.getUid()).getValue(Users.class);
                        if (user != null) {
                            // Successfully fetched user details
                            callback.onSuccess(user);
                            return; // Stop the loop once we've found the user
                        }
                    }
                    // If we've iterated over all households and not found the user
                    callback.onFailure("User details not found.");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    callback.onFailure("Failed to fetch user details: " + databaseError.getMessage());
                }
            });
        } else {
            callback.onFailure("No authenticated user found.");
        }
    }
}
