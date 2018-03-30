package sk.greate43.eatr.holders;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.fragments.MapFragment;
import sk.greate43.eatr.utils.Constants;

/**
 * Created by great on 3/30/2018.
 */

public class TrackUserViewHolder extends RecyclerView.ViewHolder {
    public TextView tvDishName;
    public TextView tvQuestion;
    public Button btnTrackNow;

    public TrackUserViewHolder(View itemView) {
        super(itemView);
        tvDishName = itemView.findViewById(R.id.track_user_list_text_view_dish_name);
        tvQuestion = itemView.findViewById(R.id.track_user_list_text_view_question);
        btnTrackNow = itemView.findViewById(R.id.track_user_list_button);


    }

    public void populate(final AppCompatActivity activity, final Food food) {
        tvDishName.setText(food.getDishName());
        if (activity instanceof BuyerActivity) {
            tvQuestion.setText("Go to Food pick Up point  ");

            btnTrackNow.setText("Pick Up ");
            btnTrackNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragment = activity.getSupportFragmentManager();
                    fragment.beginTransaction().replace(R.id.content_buyer_container, MapFragment.newInstance(food, Constants.TYPE_BUYER)).commit();
                }
            });

        } else if (activity instanceof SellerActivity) {

            btnTrackNow.setText("Track Buyer ");
            tvQuestion.setText("Track where the user is right now ");

            btnTrackNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragment = activity.getSupportFragmentManager();
                    fragment.beginTransaction().replace(R.id.content_seller_container, MapFragment.newInstance(food,Constants.TYPE_SELLER)).commit();
                }
            });
        }

    }

}
