package sk.greate43.eatr.adaptors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.fragments.PostedFoodFragment;
import sk.greate43.eatr.holders.PostedFoodViewHolder;

/**
 * Created by great on 11/12/2017.
 */

public class PostedFoodRecyclerViewAdaptor extends RecyclerView.Adapter<PostedFoodViewHolder> {

    private static final String TAG = "SellFoodRecyclerViewAda";


    private ArrayList<Food> foods;
    private LayoutInflater inflater;
    private SellerActivity sellerActivity;
    private PostedFoodFragment postedFoodFragment;
    private String states = "";


    public PostedFoodRecyclerViewAdaptor(SellerActivity sellerActivity, PostedFoodFragment postedFoodFragment) {
        this.sellerActivity = sellerActivity;
        inflater = sellerActivity.getLayoutInflater();
        foods = new ArrayList<>();
        this.postedFoodFragment = postedFoodFragment;

    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    @NonNull
    @Override
    public PostedFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.posted_food_list, parent, false);


        return new PostedFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostedFoodViewHolder holder, int position) {
        if (foods == null || foods.size() == 0) {
            // holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);
            holder.imgFoodItem.setVisibility(View.GONE);
            holder.tvDishName.setVisibility(View.GONE);
            holder.tvPrice.setVisibility(View.GONE);
            holder.tvStatus.setVisibility(View.GONE);
            holder.tvTimeStamp.setVisibility(View.GONE);
            holder.tvLocation.setVisibility(View.GONE);
            holder.tvPostedByLbl.setText(states);
            holder.tvPostedbyName.setVisibility(View.GONE);
            holder.tvRatingBarLbl.setVisibility(View.GONE);
            holder.ratingBar.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
        } else {
            holder.progressBar.setVisibility(View.GONE);
            holder.tvPostedbyName.setVisibility(View.VISIBLE);
            holder.tvRatingBarLbl.setVisibility(View.VISIBLE);
            holder.tvPostedbyName.setVisibility(View.VISIBLE);
            holder.ratingBar.setVisibility(View.VISIBLE);
            holder.imgFoodItem.setVisibility(View.VISIBLE);
            holder.tvDishName.setVisibility(View.VISIBLE);
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvTimeStamp.setVisibility(View.VISIBLE);
            holder.tvLocation.setVisibility(View.VISIBLE);
            holder.tvPostedByLbl.setText("Posted By : ");

            holder.tvRatingBarLbl.setText("Order Rating");
            holder.populate(sellerActivity, foods.get(position), postedFoodFragment, position);
        }
    }

    public void setStates(String states) {
        this.states = states;
    }

    @Override
    public int getItemCount() {
        if (foods != null && !foods.isEmpty()) {
            return foods.size();
        } else {
            return 1;
        }

    }


    public void clear() {
        int size = foods.size();
        if (size > 0) {
            foods.clear();


            notifyItemRangeRemoved(0, size);
        }
    }


    public void removeItem(int position) {
        foods.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

}