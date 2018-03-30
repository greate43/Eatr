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
import sk.greate43.eatr.holders.HistoryViewHolder;

/**
 * Created by great on 2/28/2018.
 */

public class HistoryRecyclerViewAdaptor extends RecyclerView.Adapter<HistoryViewHolder> {
    private static final String TAG = "HistoryRecyclerViewAdap";
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
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.history_list, parent, false);


        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        if (foods == null || foods.size() == 0) {
            // holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);
            holder.tvPurchasedDate.setText("Order History Not Avialable ");
            holder.tvOrderId.setVisibility(View.GONE);
            holder.tvSellerId.setVisibility(View.GONE);
            holder.tvBuyerId.setVisibility(View.GONE);
            holder.tvPrice.setVisibility(View.GONE);


            holder.tvOrderIdLbl.setVisibility(View.GONE);
            holder.tvBuyerIdLbl.setVisibility(View.GONE);
            holder.tvSellerIdLbl.setVisibility(View.GONE);
        } else {
            holder.tvPurchasedDate.setVisibility(View.VISIBLE);

            holder.tvOrderId.setVisibility(View.VISIBLE);
            holder.tvSellerId.setVisibility(View.VISIBLE);
            holder.tvBuyerId.setVisibility(View.VISIBLE);
            holder.tvPrice.setVisibility(View.VISIBLE);

            holder.tvOrderIdLbl.setVisibility(View.VISIBLE);
            holder.tvBuyerIdLbl.setVisibility(View.VISIBLE);
            holder.tvSellerIdLbl.setVisibility(View.VISIBLE);

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
}
