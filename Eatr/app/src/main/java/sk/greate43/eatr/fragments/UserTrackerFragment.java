package sk.greate43.eatr.fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.adaptors.TrackUserRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.utils.Constants;


public class UserTrackerFragment extends Fragment {
    private static final String TAG = "UserTrackerFragment";
    RecyclerView recyclerView;

    ArrayList<Food> foods;
    // ArrayList<Profile> profiles;
    TrackUserRecyclerViewAdaptor adaptor;
    String userType;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference storageReference;

    public UserTrackerFragment() {
        // Required empty public constructor
    }


    public static UserTrackerFragment newInstance(String userType) {
        UserTrackerFragment fragment = new UserTrackerFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_TYPE, userType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() != null) {
            if (getActivity() instanceof SellerActivity) {
                getActivity().setTitle("Track Buyer Location ");
            } else if (getActivity() instanceof BuyerActivity) {
                getActivity().setTitle("Pickup Location");

            }
        }
        if (getArguments() != null) {
            userType = getArguments().getString(Constants.USER_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_tracker, container, false);
        recyclerView = view.findViewById(R.id.fragment_user_tracker_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();
        if (getActivity() != null)
            adaptor = new TrackUserRecyclerViewAdaptor((AppCompatActivity) getActivity());

        foods = adaptor.getFoods();


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        switch (userType) {
            case Constants.TYPE_SELLER:
              mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.POSTED_BY).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        showData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                break;
            case Constants.TYPE_BUYER:
           mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.PURCHASED_BY).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        showData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });
                break;


        }


        return view;
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


    }

    private void collectFood(@NotNull Map<String, Object> value) {


//        //iterate through each user, ignoring their UID
//        for (Map.Entry<String, Object> entry : value.entrySet()) {
//            //Get food map
//            Map singleUser = (Map) entry.getValue();
//            //Get food field and append to list
//
////                   ,
//
//
//            Log.d(TAG, "collectFood: " + singleUser);
//
        Log.d(TAG, "collectFood: " + value);
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
            food.setCheckIfOrderIsInProgress((Boolean) value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS));
        }


        if (value.get(Constants.POSTED_BY) != null) {
            food.setPostedBy((String) value.get(Constants.POSTED_BY));
        }
        if (value.get(Constants.NO_OF_SERVINGS_PURCHASED) != null) {
            food.setNumberOfServingsPurchased((long) value.get(Constants.NO_OF_SERVINGS_PURCHASED));
        }

        if (!food.getCheckIfOrderIsActive()
                && !food.getCheckIfOrderIsPurchased()
                && !food.getCheckIfFoodIsInDraftMode()
                && !food.getCheckIfOrderIsBooked()
                && food.getCheckIfOrderIsInProgress()


                ) {
            foods.add(food);
        }


//
//        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
//        if (foodValueListener != null){
//            mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.PURCHASED_BY).equalTo(user.getUid()).removeEventListener(foodValueListener);
//        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_item_search);
        if (search != null)
            search.setVisible(false);
        super.onPrepareOptionsMenu(menu);


    }
}
