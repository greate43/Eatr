package sk.greate43.eatr.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.adaptors.HistoryRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.utils.Constants;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    String userType;
    RecyclerView recyclerView;
    ArrayList<Food> foods;
    HistoryRecyclerViewAdaptor adaptor;

    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String userType) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_TYPE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userType = getArguments().getString(Constants.USER_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        recyclerView = view.findViewById(R.id.fragment_history_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        adaptor = new HistoryRecyclerViewAdaptor(getActivity());
        foods = adaptor.getFoods();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adaptor);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mDatabaseReference.child(Constants.FOOD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return view;
    }

    private void showData(DataSnapshot dataSnapshot) {

        if (dataSnapshot.getValue() == null) {
            return;
        }

        if (adaptor != null) {
            adaptor.clear();
        }

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {


                collectHistory((Map<String, Object>) ds.getValue());
            }
        }
        adaptor.notifyDataSetChanged();


    }

    private void collectHistory(Map<String, Object> value) {
        for (Map.Entry<String, Object> entry : value.entrySet()) {
//            //Get food map
            Map singleUser = (Map) entry.getValue();

            Log.d(TAG, "collectSeller: " + singleUser);
            Food food = new Food();
            food.setPushId((String) singleUser.get(Constants.PUSH_ID));
            food.setDishName((String) singleUser.get(Constants.DISH_NAME));
            food.setCuisine((String) singleUser.get(Constants.CUISINE));
            if (singleUser.get(Constants.EXPIRY_TIME) != null) {
                food.setExpiryTime((long) singleUser.get(Constants.EXPIRY_TIME));
            }
            food.setIngredientsTags(String.valueOf(singleUser.get(Constants.INCIDENT_TAGS)));
            food.setImageUri((String) singleUser.get(Constants.IMAGE_URI));
            food.setPrice((long) singleUser.get(Constants.PRICE));
            food.setNumberOfServings((long) singleUser.get(Constants.NO_OF_SERVINGS));
            food.setLatitude((double) singleUser.get(Constants.LATITUDE));
            food.setLongitude((double) singleUser.get(Constants.LONGITUDE));
            food.setPickUpLocation((String) singleUser.get(Constants.PICK_UP_LOCATION));
            food.setCheckIfOrderIsActive((Boolean) singleUser.get(Constants.CHECK_IF_ORDER_IS_ACTIVE));
            food.setCheckIfFoodIsInDraftMode((Boolean) singleUser.get(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE));
            food.setCheckIfOrderIsPurchased((Boolean) singleUser.get(Constants.CHECK_IF_ORDER_Is_PURCHASED));
            if (singleUser.get(Constants.POSTED_BY) != null) {
                food.setPostedBy((String) singleUser.get(Constants.POSTED_BY));
            }

            if (singleUser.get(Constants.TIME_STAMP) != null) {
                food.setTime(singleUser.get(Constants.TIME_STAMP).toString());
            }
            if (singleUser.get(Constants.PURCHASED_BY) != null) {
                food.setPurchasedBy((String) singleUser.get(Constants.PURCHASED_BY));
            }

            if (singleUser.get(Constants.PURCHASED_DATE) != null) {
                food.setPurchasedDate((long) singleUser.get(Constants.PURCHASED_DATE));
            }

            if (userType.equals(Constants.TYPE_BUYER)) {
                if (
                        !food.getCheckIfFoodIsInDraftMode()
                                && food.getCheckIfOrderIsPurchased()
                                && !food.getCheckIfOrderIsActive()
                                && food.getPurchasedBy().equals(user.getUid())
                        ) {

                    foods.add(food);
                }

            } else if (userType.equals(Constants.TYPE_SELLER)) {
                if (
                        !food.getCheckIfFoodIsInDraftMode()
                                && food.getCheckIfOrderIsPurchased()
                                && !food.getCheckIfOrderIsActive()
                                && food.getPostedBy().equals(user.getUid())
                        ) {

                    foods.add(food);
                }

            }

        }

    }


}
