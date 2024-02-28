package com.example.scraps.DBModels;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.scraps.R;

import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodItemViewHolder> {

    private static final String TAG = "FoodItemAdapter";
    private List<FoodItem> foodItemList;
    private LayoutInflater mInflater;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FoodItem foodItem);
    }

    public FoodItemAdapter(Context context, List<FoodItem> foodItemList, OnItemClickListener listener) {
        this.mInflater = LayoutInflater.from(context);
        this.foodItemList = foodItemList;
        this.listener = listener;
        Log.d(TAG, "Adapter initialized with item count: " + getItemCount());
    }

    @NonNull
    @Override
    public FoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = mInflater.inflate(R.layout.food_item, parent, false);
        return new FoodItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodItemViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position " + position);
        FoodItem current = foodItemList.get(position);
        holder.bind(current, listener);
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount called: " + foodItemList.size());
        return foodItemList.size();
    }

    public static class FoodItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFoodName;
        private TextView tvExpiryDate;

        public FoodItemViewHolder(View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
        }

        public void bind(final FoodItem foodItem, final OnItemClickListener listener) {
            Log.d(TAG, "bind called for: " + foodItem.getFoodName());
            tvFoodName.setText(foodItem.getFoodName());
            tvExpiryDate.setText(foodItem.getExpiryDate());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(foodItem);
                }
            });
        }
    }
}
