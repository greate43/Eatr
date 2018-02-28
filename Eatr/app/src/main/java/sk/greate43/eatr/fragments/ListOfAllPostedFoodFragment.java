package sk.greate43.eatr.fragments;

import android.content.Intent;
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
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.DetailFoodActivity;
import sk.greate43.eatr.adaptors.ListOfAllPostedFoodRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.recyclerCustomItem.RecyclerItemClickListener;
import sk.greate43.eatr.utils.Constants;

public class ListOfAllPostedFoodFragment extends Fragment implements RecyclerItemClickListener.OnRecyclerClickListenier{
    private static final String TAG = "ListOfAllPostedFoodFrag";
    RecyclerView recyclerView;
    ArrayList<Food> foods;
    ListOfAllPostedFoodRecyclerViewAdaptor adaptor;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference storageReference;


    public ListOfAllPostedFoodFragment() {
        // Required empty public constructor
    }

    public static ListOfAllPostedFoodFragment newInstance() {
        ListOfAllPostedFoodFragment fragment = new ListOfAllPostedFoodFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_of_all_posted_food, container, false);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        recyclerView = view.findViewById(R.id.fragment_list_of_all_posted_food_recycler_view);
        adaptor = new ListOfAllPostedFoodRecyclerViewAdaptor((BuyerActivity) getActivity());

        foods = adaptor.getFoods();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);



        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(),recyclerView,this));

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


                collectFood((Map<String, Object>) ds.getValue());
            }
        }
        adaptor.notifyDataSetChanged();
    }

    private void collectFood(Map<String, Object> value) {

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


            if (
                    !food.getCheckIfFoodIsInDraftMode()
                    && !food.getCheckIfOrderIsPurchased()
                    && food.getCheckIfOrderIsActive()
                    && !food.getPostedBy().equals(user.getUid())
                    ) {

                foods.add(food);
            }

        }
    }


    @Override
    public void OnItemClick(View v, int position) {
        if (v.getTag() instanceof Food){
            Food food= (Food) v.getTag();
            Log.d(TAG, "OnItemClick: "+food.getPostedBy());

            Intent intent = new Intent(getActivity(), DetailFoodActivity.class);
            intent.putExtra(Constants.ARGS_FOOD,food);
            startActivity(intent);

        }

    }

    @Override
    public void OnItemLongClick(View v, int position) {

    }
}
