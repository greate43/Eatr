package sk.greate43.eatr.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.holders.ListOfAllPostedFoodViewHolder;

/**
 * Created by great on 2/19/2018.
 */

public class ListOfAllPostedFoodRecyclerViewAdaptor extends RecyclerView.Adapter<ListOfAllPostedFoodViewHolder> {
    private ArrayList<Food> foods;
    private LayoutInflater inflater;
    private BuyerActivity buyerActivity;

    public ListOfAllPostedFoodRecyclerViewAdaptor(BuyerActivity buyerActivity) {
        this.buyerActivity = buyerActivity;
        inflater = buyerActivity.getLayoutInflater();
        foods = new ArrayList<>();


    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    @Override
    public ListOfAllPostedFoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.posted_food_list, parent, false);


        return new ListOfAllPostedFoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListOfAllPostedFoodViewHolder holder, int position) {
        if (foods == null || foods.size() == 0) {
            // holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);

        } else {

            holder.populate(buyerActivity, foods.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (foods != null
                && !foods.isEmpty()

                ) {
            return foods.size();
        } else {
            return 0;
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