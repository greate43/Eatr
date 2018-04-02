package sk.greate43.eatr.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;

/**
 * Created by great on 2/28/2018.
 */

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "HistoryRecyclerViewHold";
    public TextView tvPurchasedDate;
    public TextView tvOrderId;
    public TextView tvSellerName;
    public TextView tvBuyerName;
    public TextView tvPrice;


    public TextView tvOrderIdLbl;
    public TextView tvSellerIdLbl;
    public TextView tvBuyerIdLbl;

    public HistoryViewHolder(View itemView) {
        super(itemView);
        tvOrderId = itemView.findViewById(R.id.history_list_order_id);
        tvPurchasedDate = itemView.findViewById(R.id.history_list_text_view_purchased_date);
        tvPrice = itemView.findViewById(R.id.history_list_price);
        tvSellerName = itemView.findViewById(R.id.history_list_seller_name);
        tvBuyerName = itemView.findViewById(R.id.history_list_buyer_name);

        tvBuyerIdLbl = itemView.findViewById(R.id.history_list_buyer_name_lbl);
        tvSellerIdLbl = itemView.findViewById(R.id.history_list_seller_name_lbl);
        tvOrderIdLbl = itemView.findViewById(R.id.history_list_order_id_lbl);

    }

    public void populate(Food food) {
        tvPrice.setText(String.valueOf("PKR " + food.getPrice() * food.getNumberOfServingsPurchased()));
        tvOrderId.setText(String.valueOf(food.getPushId()));

        tvSellerName.setText(String.valueOf(food.getPostedBy()));

        tvBuyerName.setText(String.valueOf(food.getPurchasedBy()));

        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        tvPurchasedDate.setText(sfd.format(new Date(food.getPurchasedDate())));

    }
}
