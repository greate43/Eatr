package sk.greate43.eatr.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sk.greate43.eatr.R;


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
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AddFoodItemFragment addFoodItemFragment=new AddFoodItemFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.activity_seller_fragment_container, addFoodItemFragment)
                        .commit();

            }
        });

        return view;
    }


}
