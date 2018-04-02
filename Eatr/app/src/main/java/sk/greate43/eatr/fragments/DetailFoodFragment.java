package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.entities.Notification;
import sk.greate43.eatr.utils.Constants;


public class DetailFoodFragment extends Fragment implements AdapterView.OnItemSelectedListener {
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

    Spinner spNoOfServings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            getActivity().setTitle("Detail Food Fragment");
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable(Constants.ARGS_FOOD);
        }
    }

    TextView tvPrice;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_food, container, false);
        ImageView imgDishPic = view.findViewById(R.id.fragment_detail_food_image_view_food_item);
        Button btnOrder = view.findViewById(R.id.fragment_detail_food_button_order);
        tvPrice = view.findViewById(R.id.fragment_detail_food_price);
        TextView tvCuisine = view.findViewById(R.id.fragment_detail_food_cuisine);
        spNoOfServings = view.findViewById(R.id.fragment_detail_food_spinner_no_of_servings);
        TextView tvPickUpLocation = view.findViewById(R.id.fragment_detail_food_no_of_pick_up_location);
        TextView tvTags = view.findViewById(R.id.fragment_detail_food_tags);
        TextView tvDishName = view.findViewById(R.id.fragment_detail_food_dish_name);


        spNoOfServings.setOnItemSelectedListener(this);


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


// Spinner click listener

            // Spinner Drop down elements
            List<Integer> servingList = new ArrayList<>();
            for (int i = 1; i <= food.getNumberOfServings(); i++) {
                servingList.add(i);
            }


            if (getActivity() != null) {
                // Creating adapter for spinner
                ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, servingList);

                // Drop down layout style - list view with radio button
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                // attaching data adapter to spinner
                spNoOfServings.setAdapter(dataAdapter);


            }


            tvCuisine.setText(String.valueOf(food.getCuisine()));
            tvPickUpLocation.setText(String.valueOf(food.getPickUpLocation()));
            tvTags.setText(String.valueOf(food.getIngredientsTags()));
            tvPrice.setText(String.valueOf("Rs : " + food.getPrice() * Long.parseLong(spNoOfServings.getSelectedItem().toString())));

            btnOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pushId = "";


                    if (food.getPushId() != null) {
                        pushId = food.getPushId();
                    }

                    writeData(pushId);

                }
            });

        }


        return view;
    }

    private void writeData(final String pushId) {
        showProgressDialog();
        String notificationId = mDatabaseReference.push().getKey();

        Notification notification = new Notification();
        notification.setTitle(food.getDishName());
        notification.setMessage("Would you Like To Accept The Order ?");
        notification.setSenderId(user.getUid());
        notification.setReceiverId(food.getPostedBy());
        notification.setOrderId(food.getPushId());
        notification.setCheckIfButtonShouldBeEnabled(true);
        notification.setCheckIfNotificationAlertShouldBeShown(true);
        notification.setCheckIfNotificationAlertShouldBeSent(true);
        notification.setNotificationId(notificationId);
        notification.setNotificationType(Constants.TYPE_NOTIFICATION_ORDER_REQUEST);


        notification.setTimeStamp(ServerValue.TIMESTAMP);

        if (food.getNumberOfServings() - Long.parseLong(spNoOfServings.getSelectedItem().toString()) > 0) {
            String pushFoodId = mDatabaseReference.push().getKey();
            mDatabaseReference.child(Constants.FOOD).child(pushFoodId).updateChildren(createFood(pushFoodId));

        }
        mDatabaseReference.child(Constants.FOOD).child(pushId).updateChildren(updateFood(user.getUid()));
        mDatabaseReference.child(Constants.NOTIFICATION).child(notificationId).setValue(notification);


        if (getActivity() != null) {
            getActivity().finish();
        }
        hideProgressDialog();
    }

    private Map<String, Object> createFood(String pushFoodId) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.PUSH_ID, pushFoodId);
        result.put(Constants.DISH_NAME, food.getDishName());
        result.put(Constants.CUISINE, food.getCuisine());
        result.put(Constants.EXPIRY_TIME, food.getExpiryTime());
        result.put(Constants.INGREDIENTS_TAGS, food.getIngredientsTags());
        result.put(Constants.IMAGE_URI, food.getImageUri());
        result.put(Constants.PICK_UP_LOCATION, food.getPickUpLocation());
        result.put(Constants.CHECK_IF_ORDER_IS_ACTIVE, true);
        result.put(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE, false);
        result.put(Constants.PRICE, food.getPrice());
        result.put(Constants.NO_OF_SERVINGS, food.getNumberOfServings() - Long.parseLong(spNoOfServings.getSelectedItem().toString()));
        result.put(Constants.LONGITUDE, food.getLongitude());
        result.put(Constants.LATITUDE, food.getLatitude());
        result.put(Constants.CHECK_IF_ORDER_IS_PURCHASED, false);
        result.put(Constants.PURCHASED_BY, "");
        result.put(Constants.POSTED_BY, food.getPostedBy());
        result.put(Constants.PURCHASED_DATE, 0);
        result.put(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS, false);
        result.put(Constants.CHECK_IF_ORDER_IS_ACCEPTED, false);
        result.put(Constants.CHECK_IF_ORDERED_IS_BOOKED, false);
        result.put(Constants.CHECK_IF_MAP_SHOULD_BE_CLOSED, false);
        result.put(Constants.NO_OF_SERVINGS_PURCHASED, 0);
        result.put(Constants.CHECK_IF_ORDER_IS_COMPLETED, false);


        return result;
    }

    public Map<String, Object> updateFood(String purchasedBy) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.CHECK_IF_ORDER_IS_PURCHASED, false);
        result.put(Constants.CHECK_IF_ORDER_IS_ACTIVE, false);
        result.put(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE, false);
        result.put(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS, false);
        result.put(Constants.CHECK_IF_ORDERED_IS_BOOKED, true);
        result.put(Constants.PURCHASED_BY, purchasedBy);
        result.put(Constants.PURCHASED_DATE, ServerValue.TIMESTAMP);
        result.put(Constants.NO_OF_SERVINGS, 0);
        result.put(Constants.NO_OF_SERVINGS_PURCHASED, Long.parseLong(spNoOfServings.getSelectedItem().toString()));
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tvPrice.setText(String.valueOf("Rs : " + food.getPrice() * Long.parseLong(String.valueOf(parent.getItemAtPosition(position)))));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
