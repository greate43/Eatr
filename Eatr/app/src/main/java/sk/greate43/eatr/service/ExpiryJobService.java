package sk.greate43.eatr.service;


import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.utils.Constants;

public class ExpiryJobService extends JobService {
    private static final String TAG = "ExpiryJobService";
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.d(TAG, "onCreate: ");

        database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();
    }

    @Override
    public boolean onStartJob(JobParameters job) {

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


        return false;
    }

    private void showData(@NotNull DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {
                collectFood((Map<String, Object>) ds.getValue());
            }
        }
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
        //  Log.d(TAG, "collectFood: " + value);
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

        if (value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED) != null) {
            food.setCheckIfOrderIsCompleted((boolean) value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED));
        }


        if (food.getCheckIfOrderIsActive()
                && !food.getCheckIfOrderIsPurchased()
                && !food.getCheckIfFoodIsInDraftMode()
                && !food.getCheckIfOrderIsBooked()
                && !food.getCheckIfOrderIsInProgress()
                && !food.getCheckIfOrderIsCompleted()

                ) {


            if (isExpiryNeeded(food.getTime(), food.getExpiryTime())) {
                Log.d(TAG, "collectFood: ");
                mDatabaseReference.child(Constants.FOOD).child(food.getPushId()).updateChildren(updateFood());
            }
        }


    }

    private boolean isExpiryNeeded(long added, long when) {
        long now = System.currentTimeMillis();
        if (now > when) {
            return false;
        } else {
            long difference90 = (long) (0.9 * (when - added));
            return now > (added + difference90);
        }

    }

    private Map<String, Object> updateFood() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.CHECK_IF_ORDER_IS_PURCHASED, false);
        result.put(Constants.CHECK_IF_MAP_SHOULD_BE_CLOSED, false);
        result.put(Constants.CHECK_IF_ORDER_IS_COMPLETED, false);

        result.put(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS, false);
        result.put(Constants.CHECK_IF_ORDER_IS_ACTIVE, false);
        result.put(Constants.CHECK_IF_ORDERED_IS_BOOKED, false);
        result.put(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE, false);


        return result;
    }


    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
