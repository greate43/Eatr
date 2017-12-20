package sk.greate43.eatr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import sk.greate43.eatr.activities.FoodItemContainerActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.adaptors.SellFoodRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.recyclerCustomItem.SimpleTouchCallback;
import sk.greate43.eatr.utils.Constants;


public class SellFoodFragment extends Fragment {

    public static final String TAG = "SellFoodFragment";
    RecyclerView recyclerView;
    ArrayList<Food> sellers;
    SellFoodRecyclerViewAdaptor adaptor;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public static SellFoodFragment newInstance(){
        SellFoodFragment fragment=new SellFoodFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_food, container, false);
        recyclerView = view.findViewById(R.id.fragment_sell_food_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        FloatingActionButton addFoodItem = view.findViewById(R.id.fragment_sell_food_add_food_item_btn);
        addFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FoodItemContainerActivity.class);
                startActivity(intent);

            }
        });

        adaptor = new SellFoodRecyclerViewAdaptor((SellerActivity) getActivity(), mDatabaseReference);
        adaptor.setHasStableIds(true);

        sellers = adaptor.getFoods();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        SimpleTouchCallback simpleTouchCallback = new SimpleTouchCallback(adaptor);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);


// Attach a listener to read the data at our posts reference
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
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
            // Food post = ds.getValue(Food.class);
            if (ds.child(user.getUid()).getValue() != null) {
                collectSeller((Map<String, Object>) ds.child(user.getUid()).getValue());
            }
        }
        adaptor.notifyDataSetChanged();


    }

    @Override
    public void onStop() {
        super.onStop();


    }

    private void collectSeller(Map<String, Object> value) {
        Log.d(TAG, "collectSeller: " + value);


        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : value.entrySet()) {
            //Get food map
            Map singleUser = (Map) entry.getValue();
            //Get food field and append to list

//                   ,


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
            food.setPickUpLocation((String) singleUser.get(Constants.PICK_UP_LOCATION));
            food.setCheckIfOrderIsActive((Boolean) singleUser.get(Constants.CHECK_IF_ORDER_IS_ACTIVE));
            food.setCheckIfFoodIsInDraftMode((Boolean)singleUser.get(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE));
            if (singleUser.get(Constants.TIME_STAMP) != null) {
                food.setTime(singleUser.get(Constants.TIME_STAMP).toString());
            }
            sellers.add(food);

        }

    }
}



