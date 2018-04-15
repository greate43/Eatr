package sk.greate43.eatr.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.adaptors.HistoryRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.recyclerCustomItem.EndlessRecyclerViewScrollListener;
import sk.greate43.eatr.utils.Constants;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";
    String userType;
    RecyclerView recyclerView;
    ArrayList<Food> foods;
    HistoryRecyclerViewAdaptor adaptor;
    SwipeRefreshLayout swipeRefreshLayout;

    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressBar progressBar;

    private static final int TOTAL_ITEMS_TO_LOAD = 15;
    private int mCurrentPage = 1;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(String userType) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_TYPE, userType);
        fragment.setArguments(args);
        Log.d(TAG, "newInstance: " + userType);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            getActivity().setTitle("History Fragment");
        if (getArguments() != null) {
            userType = getArguments().getString(Constants.USER_TYPE);
            Log.d(TAG, "onCreate: " + userType);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        setHasOptionsMenu(true);

        recyclerView = view.findViewById(R.id.fragment_history_recycler_view);
        progressBar = view.findViewById(R.id.loading_more_progress);
        swipeRefreshLayout = view.findViewById(R.id.fragment_history_swipe_refresh_layout);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        progressBar.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);
        if (getActivity() != null)
            adaptor = new HistoryRecyclerViewAdaptor(getActivity());
        foods = adaptor.getFoods();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);



        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adaptor);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        loadFirebaseData();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFirebaseData();
            }
        });
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore: page " + page + " totalItemsCounts " + totalItemsCount);

                mCurrentPage = page;
                progressBar.setVisibility(View.VISIBLE);
                loadFirebaseData();
            }
        });




        return view;
    }


    private void loadFirebaseData() {
        mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.PURCHASED_DATE).limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


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
        Collections.reverse(foods);
        progressBar.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);

    }

    private void collectHistory(Map<String, Object> value) {

        Food food = new Food();
        food.setPushId((String) value.get(Constants.PUSH_ID));
        food.setDishName((String) value.get(Constants.DISH_NAME));
        food.setCuisine((String) value.get(Constants.CUISINE));
        if (value.get(Constants.EXPIRY_TIME) != null) {
            food.setExpiryTime((long) value.get(Constants.EXPIRY_TIME));
        }
        food.setIngredientsTags(String.valueOf(value.get(Constants.INGREDIENTS_TAGS)));
        food.setImageUri((String) value.get(Constants.IMAGE_URI));
        food.setPrice(Long.parseLong(String.valueOf(value.get(Constants.PRICE))));
        food.setNumberOfServings((long) value.get(Constants.NO_OF_SERVINGS));
        food.setLatitude((double) value.get(Constants.LATITUDE));
        food.setLongitude((double) value.get(Constants.LONGITUDE));
        food.setPickUpLocation((String) value.get(Constants.PICK_UP_LOCATION));
        food.setCheckIfOrderIsActive((boolean) value.get(Constants.CHECK_IF_ORDER_IS_ACTIVE));
        food.setCheckIfFoodIsInDraftMode((boolean) value.get(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE));
        food.setCheckIfOrderIsPurchased((boolean) value.get(Constants.CHECK_IF_ORDER_IS_PURCHASED));
        food.setPurchasedDate((long) value.get(Constants.PURCHASED_DATE));

        if (value.get(Constants.CHECK_IF_ORDERED_IS_BOOKED) != null)
            food.setCheckIfOrderIsBooked((boolean) value.get(Constants.CHECK_IF_ORDERED_IS_BOOKED));

        if (value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS) != null)
            food.setCheckIfOrderIsInProgress((boolean) value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS));


        if (value.get(Constants.TIME_STAMP) != null) {
            food.setTime(Long.parseLong(String.valueOf(value.get(Constants.TIME_STAMP))));
        }
        if (value.get(Constants.PURCHASED_BY) != null) {
            food.setPurchasedBy((String) value.get(Constants.PURCHASED_BY));
        }


        if (value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS) != null) {
            food.setCheckIfOrderIsInProgress((boolean) value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS));
        }


        if (value.get(Constants.POSTED_BY) != null) {
            food.setPostedBy((String) value.get(Constants.POSTED_BY));
        }

        if (value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED) != null) {
            food.setCheckIfOrderIsCompleted((boolean) value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED));
        }
        if (value.get(Constants.NO_OF_SERVINGS_PURCHASED) != null) {
            food.setNumberOfServingsPurchased((long) value.get(Constants.NO_OF_SERVINGS_PURCHASED));
        }

        switch (userType) {
            case Constants.TYPE_BUYER:
                Log.d(TAG, "collectHistory: " + food.getPurchasedBy());
                Log.d(TAG, "collectHistory:current user " + user.getUid());

                if (
                        !food.getCheckIfOrderIsActive()
                                && food.getCheckIfOrderIsPurchased()
                                && !food.getCheckIfFoodIsInDraftMode()
                                && !food.getCheckIfOrderIsBooked()
                                && !food.getCheckIfOrderIsInProgress()
                                && food.getCheckIfOrderIsCompleted()
                                && food.getPurchasedBy().equals(user.getUid())
                        ) {

                    foods.add(food);

                }

                break;
            case Constants.TYPE_SELLER:
                if (
                        !food.getCheckIfOrderIsActive()
                                && food.getCheckIfOrderIsPurchased()
                                && !food.getCheckIfFoodIsInDraftMode()
                                && !food.getCheckIfOrderIsBooked()
                                && !food.getCheckIfOrderIsInProgress()
                                && food.getCheckIfOrderIsCompleted()
                                && food.getPostedBy().equals(user.getUid())
                        ) {

                    foods.add(food);

                }

                break;
        }

    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_item_search);
        if (search != null)
            search.setVisible(false);

        super.onPrepareOptionsMenu(menu);


    }


}



