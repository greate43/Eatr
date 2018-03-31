package sk.greate43.eatr.holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.fragments.PostedFoodFragment;

/**
 * Created by great on 11/12/2017.
 */

public class PostedFoodViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    private static final String TAG = "SellFoodRecyclerView";
    public ImageView imgFoodItem;
    public TextView tvStatus;
    public TextView tvLocation;
    public TextView tvDishName;
    public TextView tvTimeStamp;
    public TextView tvPrice;
    Food food;
    //ADD AN ONMENUITEM LISTENER TO EXECUTE COMMANDS ONCLICK OF CONTEXT MENU TASK
    private EditPostedFood editPostedFood;
    private int position;
    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {


            switch (item.getItemId()) {
                case 1:
                    if (editPostedFood != null) {
                        editPostedFood.onEdit(food, position);
                    }
                    break;

                case 2:
                    if (editPostedFood != null) {
                        editPostedFood.onDelete(food, position);
                    }

                    break;
            }
            return true;
        }
    };

    public PostedFoodViewHolder(View itemView) {
        super(itemView);
        tvStatus = itemView.findViewById(R.id.posted_food_list_status_text_view);
        imgFoodItem = itemView.findViewById(R.id.posted_food_list_food_item_image_view);
        tvLocation = itemView.findViewById(R.id.posted_food_list_location_text_view);
        tvDishName = itemView.findViewById(R.id.posted_food_list_food_item_dish_name);
        tvTimeStamp = itemView.findViewById(R.id.posted_food_list_food_item_timeStamp);
        tvPrice = itemView.findViewById(R.id.posted_food_list_item_price_text_view);

    }

    @SuppressLint("SetTextI18n")
    public void populate(Context context, Food food, PostedFoodFragment postedFoodFragment, int position) {
        itemView.setTag(food);
        this.food = food;
        this.position = position;
        Log.d(TAG, "populate: " + food.getImageUri());
        editPostedFood = postedFoodFragment;
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
            activateContextMenu();
            tvStatus.setTextColor(Color.GRAY);
            tvStatus.setText("Draft");
        } else if (food.getCheckIfOrderIsActive() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getcheckIfOrderIsCompleted()
                ) {
            activateContextMenu();
            tvStatus.setTextColor(Color.GREEN);
            tvStatus.setText("Active");
        } else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getcheckIfOrderIsCompleted()
                ) {
            activateContextMenu();
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

    private void activateContextMenu() {
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem Edit = menu.add(Menu.NONE, 1, 1, "Edit");
        MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Delete");
        Edit.setOnMenuItemClickListener(onEditMenu);
        Delete.setOnMenuItemClickListener(onEditMenu);
    }

    public interface EditPostedFood {
        void onEdit(Food food, int position);

        void onDelete(Food food, int position);
    }
}



