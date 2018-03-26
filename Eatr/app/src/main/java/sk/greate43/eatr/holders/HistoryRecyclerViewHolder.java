package sk.greate43.eatr.holders;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;

/**
 * Created by great on 2/28/2018.
 */

public class HistoryRecyclerViewHolder extends RecyclerView.ViewHolder {
    private TextView tvPurchasedDate;
    private TextView tvOrderId;
    private TextView tvSellerId;
    private TextView tvBuyerId;
    private TextView tvPrice;

    public HistoryRecyclerViewHolder(View itemView) {
        super(itemView);
        tvOrderId = itemView.findViewById(R.id.history_list_order_id);
        tvPurchasedDate = itemView.findViewById(R.id.history_list_text_view_purchased_date);
        tvPrice = itemView.findViewById(R.id.history_list_price);
        tvSellerId = itemView.findViewById(R.id.history_list_seller_id);
        tvBuyerId = itemView.findViewById(R.id.history_list_buyer_id);

    }

    public void populate(Food food) {
        tvPrice.setText(String.valueOf("PKR " + food.getPrice()));
        tvOrderId.setText(String.valueOf(food.getPushId()));
        tvSellerId.setText(String.valueOf(food.getPostedBy()));
        tvBuyerId.setText(String.valueOf(food.getPurchasedBy()));
        tvPurchasedDate.setText(DateUtils
                .getRelativeTimeSpanString(food.getPurchasedDate(),
                        System.currentTimeMillis(),
                        DateUtils.FORMAT_ABBREV_ALL,
                        0));
    }
}
