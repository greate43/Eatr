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
import sk.greate43.eatr.entities.Seller;
import sk.greate43.eatr.recyclerCustomItem.SimpleTouchCallback;


public class SellFoodFragment extends Fragment {

    public static final String TAG = "SellFoodFragment";
    RecyclerView recyclerView;
    ArrayList<Seller> sellers;
    SellFoodRecyclerViewAdaptor adaptor;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell_food, container, false);
        recyclerView = view.findViewById(R.id.fragment_sell_food_recycler_view);
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();

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

        sellers = adaptor.getSellers();

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
            // Seller post = ds.getValue(Seller.class);
            if (ds.child("greate43").getValue() != null) {
                collectSeller((Map<String, Object>) ds.child("greate43").getValue());
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
            //Get seller map
            Map singleUser = (Map) entry.getValue();
            //Get seller field and append to list

//                   ,


            Log.d(TAG, "collectSeller: " + singleUser);

            Seller seller = new Seller();
            seller.setDishName((String) singleUser.get("dishName"));
            seller.setCuisine((String) singleUser.get("cuisine"));
            if (singleUser.get("expiryTime")!=null) {
                seller.setExpiryTime((long) singleUser.get("expiryTime"));
            }
            seller.setIngredientsTags(String.valueOf(singleUser.get("ingredientsTags")));
            seller.setImageUri((String) singleUser.get("imageUri"));
            seller.setPickUpLocation((String) singleUser.get("pickUpLocation"));
            seller.setCheckIfOrderIsActive((Boolean) singleUser.get("checkIfOrderIsActive"));

            if (singleUser.get("timeStamp") != null) {
                seller.setTime(singleUser.get("timeStamp").toString());
            }
            sellers.add(seller);

        }

    }
}



