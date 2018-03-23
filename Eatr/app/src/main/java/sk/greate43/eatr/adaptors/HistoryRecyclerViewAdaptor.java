package sk.greate43.eatr.adaptors;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.holders.HistoryRecyclerViewHolder;

/**
 * Created by great on 2/28/2018.
 */

public class HistoryRecyclerViewAdaptor extends RecyclerView.Adapter<HistoryRecyclerViewHolder> {
    LayoutInflater layoutInflater;
    ArrayList<Food> foods;

    public HistoryRecyclerViewAdaptor(Activity activity) {

        layoutInflater = activity.getLayoutInflater();
        foods = new ArrayList<>();
    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    @NonNull
    @Override
    public HistoryRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.history_list, parent, false);


        return new HistoryRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecyclerViewHolder holder, int position) {
        if (foods == null || foods.size() == 0) {
            // holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);

        } else {

            holder.populate(foods.get(position));
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
}
