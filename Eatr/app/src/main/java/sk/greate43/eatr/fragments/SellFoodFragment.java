package sk.greate43.eatr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.AddFoodItemActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.adaptors.SellFoodRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Seller;


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
        adaptor = new SellFoodRecyclerViewAdaptor((SellerActivity) getActivity());

        FloatingActionButton addFoodItem = view.findViewById(R.id.fragment_sell_food_add_food_item_btn);
        addFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddFoodItemActivity.class);
                startActivity(intent);

            }
        });


        sellers = adaptor.getSellers();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);

        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();

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
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
           // Seller post = ds.getValue(Seller.class);
            Log.d(TAG, "showData: "+ds);
            Seller seller = new Seller(
                        ds.child("greate43").child("sasa").getValue(Seller.class).getDishName(),
                        ds.child("greate43").child("sasa").getValue(Seller.class).getCuisine(),
                        ds.child("greate43").child("sasa").getValue(Seller.class).getExpiryTime(),
                        ds.child("greate43").child("sasa").getValue(Seller.class).getPickUpLocation(),
                        ds.child("greate43").child("sasa").getValue(Seller.class).getImageUri()

                );

                sellers.add(seller);


                adaptor.notifyDataSetChanged();
        }


    }}



