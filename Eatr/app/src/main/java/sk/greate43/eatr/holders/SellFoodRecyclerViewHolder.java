package sk.greate43.eatr.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Seller;

/**
 * Created by great on 11/12/2017.
 */

public class SellFoodRecyclerViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "SellFoodRecyclerView";
    public ImageView imgFoodItem;
    private TextView tvStatus;
    private TextView tvLocation;
    private TextView tvDishName;
    private TextView tvTimeStamp;

    public SellFoodRecyclerViewHolder(View itemView) {
        super(itemView);
        tvStatus = itemView.findViewById(R.id.posted_food_list_status_text_view);
        imgFoodItem = itemView.findViewById(R.id.posted_food_list_food_item_image_view);
        tvLocation = itemView.findViewById(R.id.posted_food_list_location_text_view);
        tvDishName = itemView.findViewById(R.id.posted_food_list_food_item_dish_name);
        tvTimeStamp = itemView.findViewById(R.id.posted_food_list_food_item_timeStamp);

    }

    public void populate(Context context, Seller seller) {
        itemView.setTag(seller);
        Log.d(TAG, "populate: " + seller.getImageUri());
        if (seller.getImageUri() != null && !seller.getImageUri().isEmpty()) {
            Picasso.with(context)
                    .load(seller.getImageUri())
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
        tvStatus.setText("Active");
        tvLocation.setText(seller.getPickUpLocation());
        tvTimeStamp.setText(DateUtils
                .getRelativeTimeSpanString(Long.parseLong(seller.getTime()),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        0));
        tvDishName.setText(seller.getDishName());
    }

}



