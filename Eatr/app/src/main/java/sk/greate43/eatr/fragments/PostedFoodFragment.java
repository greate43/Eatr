package sk.greate43.eatr.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.FoodItemContainerActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.adaptors.PostedFoodRecyclerViewAdaptor;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.holders.PostedFoodViewHolder;
import sk.greate43.eatr.utils.Constants;


public class PostedFoodFragment extends Fragment implements PostedFoodViewHolder.EditPostedFood {

    public static final String TAG = "PostedFoodFragment";
    RecyclerView recyclerView;
    ArrayList<Food> foods;
    // ArrayList<Profile> profiles;
    PostedFoodRecyclerViewAdaptor adaptor;
    String states = "";
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference storageReference;
    private String orderState;

    public static PostedFoodFragment newInstance(String orderState) {
        PostedFoodFragment fragment = new PostedFoodFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ORDER_STATE, orderState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            orderState = getArguments().getString(Constants.ORDER_STATE);
            if (orderState != null) {
                switch (orderState) {
                    case Constants.ALL_ORDERS:
                        states = "No Order Posted";


                        break;
                    case Constants.ORDER_ACTIVE:
                        states = "No Active Order Available";


                        break;
                    case Constants.ORDER_PURCHASED:

                        states = "No Ordered Purchased Yet";


                        break;
                    case Constants.ORDER_DRAFT:

                        states = "No Order is in Draft";

                        break;
                    case Constants.ORDERED_BOOKED:

                        states = "No Order Is Booked";

                        break;
                    default:
                        break;
                }
            }

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posted_food, container, false);
        recyclerView = view.findViewById(R.id.fragment_posted_food_recycler_view);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        FloatingActionButton addFoodItem = view.findViewById(R.id.fragment_posted_food_add_food_item_btn);
        addFoodItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FoodItemContainerActivity.class);
                startActivity(intent);
            }
        });

        adaptor = new PostedFoodRecyclerViewAdaptor((SellerActivity) getActivity(), this);
        adaptor.setStates(states);
        foods = adaptor.getFoods();


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

//        SimpleTouchCallback simpleTouchCallback = new SimpleTouchCallback(adaptor);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleTouchCallback);
//        itemTouchHelper.attachToRecyclerView(recyclerView);


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


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void collectFood(Map<String, Object> value) {


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
            food.setTime(value.get(Constants.TIME_STAMP).toString());
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

        if (value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED) != null) {
            food.setCheckIfOrderIsCompleted((boolean) value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED));
        }

        switch (orderState) {
            case Constants.ALL_ORDERS:
                foods.add(food);

                break;
            case Constants.ORDER_ACTIVE:

                if (food.getCheckIfOrderIsActive()
                        && !food.getCheckIfOrderIsPurchased()
                        && !food.getCheckIfFoodIsInDraftMode()
                        && !food.getCheckIfOrderIsBooked()
                        && !food.getCheckIfOrderIsInProgress()
                        && !food.getcheckIfOrderIsCompleted()

                        ) {
                    foods.add(food);
                }
                break;
            case Constants.ORDER_PURCHASED:

                if (!food.getCheckIfOrderIsActive()
                        && food.getCheckIfOrderIsPurchased()
                        && !food.getCheckIfFoodIsInDraftMode()
                        && !food.getCheckIfOrderIsBooked()
                        && !food.getCheckIfOrderIsInProgress()
                        && food.getcheckIfOrderIsCompleted()

                        ) {
                    foods.add(food);
                }

                break;
            case Constants.ORDER_DRAFT:

                if (!food.getCheckIfOrderIsActive()
                        && !food.getCheckIfOrderIsPurchased()
                        && food.getCheckIfFoodIsInDraftMode()
                        && !food.getCheckIfOrderIsBooked()
                        && !food.getCheckIfOrderIsInProgress()
                        && !food.getcheckIfOrderIsCompleted()


                        ) {
                    foods.add(food);
                }
                break;
            case Constants.ORDERED_BOOKED:

                if (!food.getCheckIfOrderIsActive()
                        && !food.getCheckIfOrderIsPurchased()
                        && !food.getCheckIfFoodIsInDraftMode()
                        && food.getCheckIfOrderIsBooked()
                        && !food.getCheckIfOrderIsInProgress()
                        && !food.getcheckIfOrderIsCompleted()

                        ) {
                    foods.add(food);
                }
                break;
            default:
                foods.add(food);
                break;
        }

//
//        }

    }


    @Override
    public void onEdit(Food food, int position) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), FoodItemContainerActivity.class);
            intent.putExtra(Constants.ARGS_FOOD, food);
            startActivity(intent);

        }
        Toast.makeText(getActivity(), "Edit ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDelete(Food food, int position) {
        if (food != null) {
            mDatabaseReference.child(Constants.FOOD).child(food.getPushId()).removeValue();
            if (food.getImageUri() != null && food.getImageUri().isEmpty()) {
                storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(food.getImageUri());

                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        Log.d(TAG, "onSuccess: deleted file");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        Log.d(TAG, "onFailure: did not delete file");
                    }
                });
            }
            Toast.makeText(getActivity(), "Deleted " + food.getPushId(), Toast.LENGTH_SHORT).show();

            adaptor.removeItem(position);
        }
    }
}



