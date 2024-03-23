package com.example.scraps.DBModels;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scraps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class HouseholdMemberAdapter extends RecyclerView.Adapter<HouseholdMemberAdapter.HouseholdViewHolder> {

    private static final String TAG = "HouseholdMemberAdapter";
    private List<Users> householdMembers;
    private LayoutInflater mInflater;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Users user);
    }

    public interface DatabaseOperationCallback {
        void onSuccess(String message);
        void onFailure(String errorMessage);
    }

    public HouseholdMemberAdapter(Context context, List<Users> usersList, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.householdMembers = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HouseholdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.house_member, parent, false);
        return new HouseholdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HouseholdViewHolder holder, int position) {
        Users current = householdMembers.get(position);
        holder.bind(current, listener);
    }

    @Override
    public int getItemCount() {
        return householdMembers.size();
    }

    public void updateData(List<Users> newHouseholdMembers) {
        this.householdMembers.clear();
        this.householdMembers.addAll(newHouseholdMembers);
        notifyDataSetChanged();
    }

    public class HouseholdViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvEmail;
        private Button btnAccept;
        private Button btnDecline;

        public HouseholdViewHolder(View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            btnAccept = itemView.findViewById(R.id.buttonAccept);
            btnDecline = itemView.findViewById(R.id.buttonDecline);
        }

        public void bind(final Users user, final OnItemClickListener listener) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());

            itemView.setOnClickListener(v -> listener.onItemClick(user));

            btnAccept.setOnClickListener(view -> joinHousehold(user, new DatabaseOperationCallback() {
                @Override
                public void onSuccess(String message) {
                    Log.d(TAG, "User joined household successfully");
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e(TAG, "Failed to join household: " + errorMessage);
                }
            }));

            btnDecline.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("households")
                            .child(user.getHouseID()).child("userIDs").child(user.getFirebaseID());
                    userRef.removeValue() // Remove the user from the database
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "User removed from household successfully");
                                removeUserFromAdapter(position); // Update UI after successful removal from database
                            })
                            .addOnFailureListener(e -> Log.e(TAG, "Failed to remove user from household: " + e.getMessage()));
                }
            });
        }

        private void joinHousehold(Users user, final DatabaseOperationCallback callback) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("households")
                    .child(user.getHouseID()).child("userIDs").child(user.getFirebaseID());
            userRef.setValue(true)
                    .addOnSuccessListener(aVoid -> callback.onSuccess("User added to household successfully."))
                    .addOnFailureListener(e -> callback.onFailure("Failed to add user to household: " + e.getMessage()));
        }

        private void removeUserFromAdapter(int position) {
            householdMembers.remove(position);
            notifyItemRemoved(position);
        }
    }
}
