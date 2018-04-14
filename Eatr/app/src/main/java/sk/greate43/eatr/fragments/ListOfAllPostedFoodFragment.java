package sk.greate43.eatr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.DetailFoodActivity;
import sk.greate43.eatr.adaptors.ListOfAllPostedFoodRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.interfaces.Search;
import sk.greate43.eatr.recyclerCustomItem.EndlessRecyclerViewScrollListener;
import sk.greate43.eatr.recyclerCustomItem.RecyclerItemClickListener;
import sk.greate43.eatr.utils.Constants;

public class ListOfAllPostedFoodFragment extends Fragment implements RecyclerItemClickListener.OnRecyclerClickListenier, Search {
    private static final String TAG = "ListOfAllPostedFoodFrag";
    RecyclerView recyclerView;
    ArrayList<Food> foods;
    ListOfAllPostedFoodRecyclerViewAdaptor adaptor;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference storageReference;
    private static final int TOTAL_ITEMS_TO_LOAD = 15;
    private int mCurrentPage = 1;
    private ProgressBar progressBar;
    LinearLayoutManager layoutManager;

    public ListOfAllPostedFoodFragment() {
        // Required empty public constructor
    }

    @NonNull
    public static ListOfAllPostedFoodFragment newInstance() {

        return new ListOfAllPostedFoodFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null)
            getActivity().setTitle("Posted Food Fragment");
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_of_all_posted_food, container, false);

        initialize();
        recyclerView = view.findViewById(R.id.fragment_list_of_all_posted_food_recycler_view);
        progressBar = view.findViewById(R.id.loading_more_progress);
        if (getActivity() != null)
            adaptor = new ListOfAllPostedFoodRecyclerViewAdaptor((BuyerActivity) getActivity());

        foods = adaptor.getFoods();
        recyclerView.setHasFixedSize(true);
        progressBar.setVisibility(View.GONE);

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerView, this));

        recyclerView.setAdapter(adaptor);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        retrieveFirebaseData("");


//        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh(SwipyRefreshLayoutDirection direction) {
//                mCurrentPage++;
//                Log.d(TAG, "onRefresh: " + mCurrentPage);
//                retrieveFirebaseData("");
//            }
//        });

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore: page " + page + " totalItemsCounts " + totalItemsCount);
                mCurrentPage = page;
                progressBar.setVisibility(View.VISIBLE);
                retrieveFirebaseData("");

            }
        });


        //  implementScrollListener();

        return view;
    }



    private void initialize() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();
    }


    private void retrieveFirebaseData(String searchKeyword) {
        if (mDatabaseReference != null) {

            if (!searchKeyword.isEmpty()) {
                mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.DISH_NAME).startAt(searchKeyword).endAt(searchKeyword + Constants.MAX_UNI_CODE_LIMIT).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        showData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
            } else {
                DatabaseReference foodListRef = mDatabaseReference.child(Constants.FOOD);
                Query query = foodListRef.limitToFirst(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
                query.orderByChild(Constants.EXPIRY_TIME).addValueEventListener(new ValueEventListener() {
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

        } else {
            initialize();
        }
    }


    private void showData(@NotNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        if (adaptor != null) {
            adaptor.clear();
        }

        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {


                collectFood((Map<String, Object>) ds.getValue());
            }
        }
        adaptor.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);

    }

    private void collectFood(Map<String, Object> value) {


        Log.d(TAG, "collectSeller: " + value);
        Food food = new Food();
        food.setPushId((String) value.get(Constants.PUSH_ID));
        food.setDishName((String) value.get(Constants.DISH_NAME));
        food.setCuisine((String) value.get(Constants.CUISINE));
        if (value.get(Constants.EXPIRY_TIME) != null) {
            food.setExpiryTime((long) value.get(Constants.EXPIRY_TIME));
        }
        food.setIngredientsTags(String.valueOf(value.get(Constants.INGREDIENTS_TAGS)));
        food.setImageUri((String) value.get(Constants.IMAGE_URI));
        food.setPrice((long) value.get(Constants.PRICE));
        food.setNumberOfServings((long) value.get(Constants.NO_OF_SERVINGS));
        food.setLatitude((double) value.get(Constants.LATITUDE));
        food.setLongitude((double) value.get(Constants.LONGITUDE));
        food.setPickUpLocation((String) value.get(Constants.PICK_UP_LOCATION));
        food.setCheckIfOrderIsActive((Boolean) value.get(Constants.CHECK_IF_ORDER_IS_ACTIVE));
        food.setCheckIfFoodIsInDraftMode((Boolean) value.get(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE));
        food.setCheckIfOrderIsPurchased((Boolean) value.get(Constants.CHECK_IF_ORDER_IS_PURCHASED));


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


        if (value.get(Constants.POSTED_BY) != null) {
            food.setPostedBy((String) value.get(Constants.POSTED_BY));
        }
        if (value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED) != null) {
            food.setCheckIfOrderIsCompleted((boolean) value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED));
        }

        if (
                !food.getCheckIfFoodIsInDraftMode()
                        && !food.getCheckIfOrderIsPurchased()
                        && food.getCheckIfOrderIsActive()
                        && !food.getCheckIfOrderIsInProgress()
                        && !food.getCheckIfOrderIsBooked()
                        && !food.getCheckIfOrderIsCompleted()
                        && food.getNumberOfServings() > 0
                        && !food.getPostedBy().equals(user.getUid())
                ) {

            foods.add(food);

        }

    }


    @Override
    public void OnItemClick(View v, int position) {
        if (v.getTag() instanceof Food) {
            Food food = (Food) v.getTag();
            Log.d(TAG, "OnItemClick: " + food.getPostedBy());

            Intent intent = new Intent(getActivity(), DetailFoodActivity.class);
            intent.putExtra(Constants.ARGS_FOOD, food);
            startActivity(intent);

        }

    }

    @Override
    public void OnItemLongClick(View v, int position) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            ((BuyerActivity) getActivity()).setCallbackListener(this);
        }

    }

    @Override
    public void onSearchCompleted(String searchKeyword) {
        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            retrieveFirebaseData(searchKeyword.toLowerCase());
        } else {
            retrieveFirebaseData("");
        }
    }


}
