package sk.greate43.eatr.adaptors;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.holders.TrackUserViewHolder;

/**
 * Created by great on 3/30/2018.
 */

public class TrackUserRecyclerViewAdaptor extends RecyclerView.Adapter<TrackUserViewHolder> {
    private static final String TAG = "TrackUserRecyclerViewAd";
    private ArrayList<Food> foods;
    private LayoutInflater inflater;
    private AppCompatActivity activity;

    public TrackUserRecyclerViewAdaptor(AppCompatActivity activity) {
        this.activity = activity;
        inflater = activity.getLayoutInflater();
        foods = new ArrayList<>();

    }

    public ArrayList<Food> getFoods() {
        return foods;
    }

    @NonNull
    @Override
    public TrackUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.track_user_list, parent, false);


        return new TrackUserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackUserViewHolder holder, int position) {
        if (foods == null || foods.size() == 0) {
            // holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);
            holder.tvDishName.setText("No Order In Progress");
            holder.tvQuestion.setVisibility(View.GONE);
            holder.btnTrackNow.setVisibility(View.GONE);

        } else {
            holder.btnTrackNow.setVisibility(View.VISIBLE);
            holder.tvDishName.setVisibility(View.VISIBLE);
            holder.tvQuestion.setVisibility(View.VISIBLE);
            holder.populate(activity, foods.get(position));
        }
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
