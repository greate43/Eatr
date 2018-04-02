package sk.greate43.eatr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.utils.Constants;


public class AcceptOrderFragment extends Fragment {

    Button yes;
    Button no;
    ImageView imgDishPhoto;
    Food food;
    private TextView tvDishName;
    private TextView tvNoOfServings;
    private TextView tvWhoWantsToOrder;

    public AcceptOrderFragment() {
        // Required empty public constructor
    }

    public static AcceptOrderFragment newInstance(Food food) {
        AcceptOrderFragment fragment = new AcceptOrderFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARGS_FOOD, food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable(Constants.ARGS_FOOD);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accept_order, container, false);
        tvDishName = view.findViewById(R.id.fragment_accept_order_text_view_dish_name);
        tvWhoWantsToOrder = view.findViewById(R.id.fragment_accept_order_text_view_who_wants_to_order);
        tvNoOfServings = view.findViewById(R.id.fragment_accept_order_text_view_no_of_servings);
        yes = view.findViewById(R.id.fragment_accept_order_button_yes);
        no = view.findViewById(R.id.fragment_accept_order_button_no);
        imgDishPhoto = view.findViewById(R.id.fragment_accept_order_image_view_food_pic);


        if (food != null) {
            tvDishName.setText(String.valueOf(food.getDishName()));
            tvNoOfServings.setText(String.valueOf(food.getNumberOfServings()));
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }


        return view;
    }

}
