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
 * Created by great on 2/19/2018.
 */

public class ListOfAllPostedFoodViewHolder extends RecyclerView.ViewHolder {


    private static final String TAG = "ListOfAllPostedFoodView";
    public ImageView imgFoodItem;
    public TextView tvStatus;
    public TextView tvLocation;
    public TextView tvDishName;
    public TextView tvTimeStamp;
    public TextView tvPrice;

    public ListOfAllPostedFoodViewHolder(View itemView) {
        super(itemView);
        tvStatus = itemView.findViewById(R.id.posted_food_list_status_text_view);
        imgFoodItem = itemView.findViewById(R.id.posted_food_list_food_item_image_view);
        tvLocation = itemView.findViewById(R.id.posted_food_list_location_text_view);
        tvDishName = itemView.findViewById(R.id.posted_food_list_food_item_dish_name);
        tvTimeStamp = itemView.findViewById(R.id.posted_food_list_food_item_timeStamp);
        tvPrice = itemView.findViewById(R.id.posted_food_list_item_price_text_view);
    }


    @SuppressLint("SetTextI18n")
    public void populate(Context context, Food food) {
        itemView.setTag(food);

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

        if (food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getcheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.GRAY);
            tvStatus.setText("Draft");
        } else if (food.getCheckIfOrderIsActive() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getcheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.GREEN);
            tvStatus.setText("Active");
        } else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getcheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.RED);
            tvStatus.setText("Expired");

        } else if (!food.getCheckIfOrderIsActive() && food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && food.getcheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.BLACK);
            tvStatus.setText("Sold");

        } else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getcheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.YELLOW);
            tvStatus.setText("Reserved");

        } else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && food.getCheckIfOrderIsInProgress() && !food.getcheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.BLUE);
            tvStatus.setText("In Progress");

        }
        else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && food.getcheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.BLACK);
            tvStatus.setText("Completed");

        }

        tvPrice.setText(String.valueOf("Rs : " + food.getPrice()));
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
