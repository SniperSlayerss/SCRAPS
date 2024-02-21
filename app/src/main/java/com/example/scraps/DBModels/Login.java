package com.example.scraps.DBModels;

import android.util.Log;

import androidx.annotation.NonNull;

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

    private String username, password;

    public Login(String username, String password) {}

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Login successful, fetch user details
                            fetchUserDetails();
                        } else {
                            // Login failed, handle error
                            Log.e("Login", "Login failed", task.getException());
                        }
                    }
                });
    }
    private void fetchUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Assuming you have a User class to map the data
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null) {
                        // Use user details
                        Log.d("UserDetails", "User name: " + user.getUsername() + ", Email: " + user.getEmail());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w("FetchUserDetails", "loadUser:onCancelled", databaseError.toException());
                }
            });
        }
    }
}
