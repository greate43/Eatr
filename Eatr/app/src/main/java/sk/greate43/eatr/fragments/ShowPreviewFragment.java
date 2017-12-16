package sk.greate43.eatr.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.Constants;


public class ShowPreviewFragment extends Fragment {

    TextView tvFoodName;
    TextView tvCuisine;
    TextView tvPickLocation;
    TextView tvExpiryTime;
    TextView tvNoOfServings;
    TextView tvPrice;

    Button btnBack;

    ImageView imgFoodImage;

    Food food;
    private ReplaceFragment replaceFragment;

    public ShowPreviewFragment() {
        // Required empty public constructor
    }

    public static ShowPreviewFragment newInstance(Food food) {
        ShowPreviewFragment fragment = new ShowPreviewFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_preview, container, false);

        tvCuisine = view.findViewById(R.id.fragment_show_preview_text_view_cusine);
        tvExpiryTime = view.findViewById(R.id.fragment_show_preview_text_view_expiry_time);
        tvFoodName = view.findViewById(R.id.fragment_show_preview_text_view_food_name);
        tvPickLocation = view.findViewById(R.id.fragment_show_preview_text_view_pick_up_location);
        tvNoOfServings = view.findViewById(R.id.fragment_show_preview_text_view_no_of_servings);
        tvPrice = view.findViewById(R.id.fragment_show_preview_text_view_price);
        btnBack = view.findViewById(R.id.fragment_show_preview_button_return);
        imgFoodImage = view.findViewById(R.id.fragment_show_preview_image_view_food_image);
        if (food != null) {

            tvPrice.setText(String.valueOf(food.getPrice()));
            tvNoOfServings.setText(String.valueOf(food.getNumberOfServings()));
            tvPickLocation.setText(String.valueOf(food.getPickUpLocation()));
            tvFoodName.setText(String.valueOf(food.getDishName()));
            tvExpiryTime.setText(String.valueOf(showExpiryTime(food.getExpiryTime())));
            tvCuisine.setText(String.valueOf(food.getCuisine()));
            imgFoodImage.setImageURI(food.getImage());
            imgFoodImage.setScaleType(ImageView.ScaleType.CENTER_CROP);



        }
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (replaceFragment != null){
                 replaceFragment.onFragmentReplaced(FoodItemExpiryTimeAndPriceFragment.newInstance(food));

             }
            }
        });
        return view;
    }

    private CharSequence showExpiryTime(long time) {
       return DateUtils
                .getRelativeTimeSpanString(time,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS,
                        0);
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof ReplaceFragment) {
            replaceFragment = (ReplaceFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReplaceFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        replaceFragment = null;

    }

}
