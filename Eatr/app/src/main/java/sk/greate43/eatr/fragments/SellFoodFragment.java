package sk.greate43.eatr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.AddFoodItemActivity;


public class SellFoodFragment extends Fragment {

    public static final String TAG = "SellFoodFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_food, container, false);
        FloatingActionButton addFoodItem = view.findViewById(R.id.fragment_sell_food_add_food_item_btn);

        addFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), AddFoodItemActivity.class);
                startActivity(intent);

            }
        });

        return view;
    }


}
