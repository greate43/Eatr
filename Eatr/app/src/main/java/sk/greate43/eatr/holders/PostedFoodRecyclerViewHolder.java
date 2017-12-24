package sk.greate43.eatr.holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;

/**
 * Created by great on 11/12/2017.
 */

public class PostedFoodRecyclerViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "SellFoodRecyclerView";
    private ImageView imgFoodItem;
    private TextView tvStatus;
    private TextView tvLocation;
    private TextView tvDishName;
    private TextView tvTimeStamp;

    public PostedFoodRecyclerViewHolder(View itemView) {
        super(itemView);
        tvStatus = itemView.findViewById(R.id.posted_food_list_status_text_view);
        imgFoodItem = itemView.findViewById(R.id.posted_food_list_food_item_image_view);
        tvLocation = itemView.findViewById(R.id.posted_food_list_location_text_view);
        tvDishName = itemView.findViewById(R.id.posted_food_list_food_item_dish_name);
        tvTimeStamp = itemView.findViewById(R.id.posted_food_list_food_item_timeStamp);

    }

    @SuppressLint("SetTextI18n")
    public void populate(Context context, Food food) {
        itemView.setTag(food);
        Log.d(TAG, "populate: " + food.getImageUri());
        if (food.getImageUri() != null && !food.getImageUri().isEmpty()) {
            Picasso.with(context)
                    .load(food.getImageUri())
                    .fit()
                    .centerCrop()
                    .into(imgFoodItem, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: ");
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        if (food.getCheckIfFoodIsInDraftMode()) {
            tvStatus.setTextColor(Color.GRAY);
            tvStatus.setText("Draft");
        } else if (food.getCheckIfOrderIsActive()) {
            tvStatus.setTextColor(Color.GREEN);
            tvStatus.setText("Active");
        } else if (!food.getCheckIfOrderIsActive()) {
            tvStatus.setTextColor(Color.RED);
            tvStatus.setText("Expired");

        }
        tvLocation.setText(food.getPickUpLocation());
        if (food.getTime() != null && !food.getTime().isEmpty()) {
            tvTimeStamp.setText(DateUtils
                    .getRelativeTimeSpanString(Long.parseLong(food.getTime()),
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS,
                            0));
        } else {
            tvTimeStamp.setText("");
        }
        tvDishName.setText(food.getDishName());
    }

}



