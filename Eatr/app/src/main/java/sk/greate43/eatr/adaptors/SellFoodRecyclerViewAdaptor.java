package sk.greate43.eatr.adaptors;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Seller;
import sk.greate43.eatr.holders.SellFoodRecyclerViewHolder;

/**
 * Created by great on 11/12/2017.
 */

public class SellFoodRecyclerViewAdaptor extends RecyclerView.Adapter<SellFoodRecyclerViewHolder> {

    private ArrayList<Seller> sellers;
    private LayoutInflater inflater;
    private SellerActivity sellerActivity;

    public SellFoodRecyclerViewAdaptor(SellerActivity sellerActivity) {
        this.sellerActivity = sellerActivity;
        inflater = sellerActivity.getLayoutInflater();
        sellers = new ArrayList<>();

    }

    public ArrayList<Seller> getSellers() {
        return sellers;
    }

    @Override
    public SellFoodRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.posted_food_list, parent, false);


        return new SellFoodRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SellFoodRecyclerViewHolder holder, int position) {
        if (sellers == null || sellers.size() == 0) {
            holder.imgFoodItem.setImageResource(R.drawable.ic_launcher_background);

        } else {
            holder.populate(sellerActivity, sellers.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return ((sellers != null && sellers.size() != 0) ? sellers.size() : 1);
    }
}
