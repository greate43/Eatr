package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.utils.Constants;


public class DetailFoodFragment extends Fragment {
    private static final String TAG = "DetailFoodFragment";
    private Food food;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog mProgressDialog;

    public DetailFoodFragment() {
        // Required empty public constructor
    }


    public static DetailFoodFragment newInstance(Food food) {
        DetailFoodFragment fragment = new DetailFoodFragment();
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
        View view = inflater.inflate(R.layout.fragment_detail_food, container, false);
        ImageView imgDishPic = view.findViewById(R.id.fragment_detail_food_image_view_food_item);
        Button btnOrder = view.findViewById(R.id.fragment_detail_food_button_order);
        TextView tvPrice = view.findViewById(R.id.fragment_detail_food_price);
        TextView tvCuisine = view.findViewById(R.id.fragment_detail_food_cuisine);
        TextView tvNoOfServings = view.findViewById(R.id.fragment_detail_food_no_of_servings);
        TextView tvPickUpLocation = view.findViewById(R.id.fragment_detail_food_no_of_pick_up_location);
        TextView tvTags = view.findViewById(R.id.fragment_detail_food_tags);
        TextView tvDishName = view.findViewById(R.id.fragment_detail_food_dish_name);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();


        if (food != null) {
            if (food.getImageUri() != null && !food.getImageUri().isEmpty()) {
                Picasso.with(getActivity())
                        .load(food.getImageUri())
                        .fit()
                        .centerCrop()
                        .into(imgDishPic, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
            tvDishName.setText(String.valueOf(food.getDishName()));
            tvPrice.setText(String.valueOf("Rs : " + food.getPrice()));
            tvNoOfServings.setText(String.valueOf(food.getNumberOfServings()));
            tvCuisine.setText(String.valueOf(food.getCuisine()));
            tvPickUpLocation.setText(String.valueOf(food.getPickUpLocation()));
            tvTags.setText(String.valueOf(food.getIngredientsTags()));

            btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userId = "";
                    String pushId = "";

                    if (food.getPostedBy() != null) {
                        userId = food.getPostedBy();
                    }

                    if (food.getPushId() != null) {
                        pushId = food.getPushId();
                    }

                    writeData(userId, pushId);

                }
            });

        }


        return view;
    }

    private void writeData(String userId, final String pushId) {
        showProgressDialog();
        mDatabaseReference.child(Constants.FOOD).child(userId).child(pushId).updateChildren(toMap(user.getUid()));
        if (getActivity() != null) {
            getActivity().finish();
        }
        hideProgressDialog();
    }

    public Map<String, Object> toMap(String purchasedBy) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.CHECK_IF_ORDER_Is_PURCHASED, true);
        result.put(Constants.CHECK_IF_ORDER_IS_ACTIVE, false);
        result.put(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE, false);
        result.put(Constants.PURCHASED_BY, purchasedBy);
        result.put(Constants.PURCHASED_DATE, ServerValue.TIMESTAMP);
        return result;

    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
