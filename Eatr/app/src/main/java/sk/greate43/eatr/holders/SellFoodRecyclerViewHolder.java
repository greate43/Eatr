package sk.greate43.eatr.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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
    public TextView tvStatus;
    public ImageView imgFoodItem;

    public SellFoodRecyclerViewHolder(View itemView) {
        super(itemView);
        tvStatus = itemView.findViewById(R.id.posted_food_list_status_text_view);
        imgFoodItem = itemView.findViewById(R.id.posted_food_list_food_item_image_view);
    }

    public void populate(Context context, Seller seller) {
        itemView.setTag(seller);
        Log.d(TAG, "populate: "+seller.getImageUri());
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

        tvStatus.setText("Active");

    }

}



