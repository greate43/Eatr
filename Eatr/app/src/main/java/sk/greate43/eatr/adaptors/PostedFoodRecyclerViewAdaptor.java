package sk.greate43.eatr.adaptors;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.fragments.PostedFoodFragment;
import sk.greate43.eatr.holders.PostedFoodRecyclerViewHolder;

/**
 * Created by great on 11/12/2017.
 */

public class PostedFoodRecyclerViewAdaptor extends RecyclerView.Adapter<PostedFoodRecyclerViewHolder> {

    private static final String TAG = "SellFoodRecyclerViewAda";


    private ArrayList<Food> foods;
    private LayoutInflater inflater;
    private SellerActivity sellerActivity;
    private PostedFoodFragment postedFoodFragment;

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
    public PostedFoodRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.posted_food_list, parent, false);


        return new PostedFoodRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostedFoodRecyclerViewHolder holder, int position) {
        if (foods == null || foods.size() == 0) {
            // holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);

        } else {

            holder.populate(sellerActivity, foods.get(position), postedFoodFragment, position);
        }
    }

    @Override
    public int getItemCount() {
        if (foods != null && !foods.isEmpty()) {
            return foods.size();
        } else {
            return 0;
        }

    }


    public void clear() {
        int size = foods.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                foods.remove(i);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }


    public void removeItem(int position) {
        foods.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

}