package sk.greate43.eatr.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import sk.greate43.eatr.activities.BuyerActivity;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.fragmentDialogs.ReviewDialogFragment;

public class ReviewUtils {
    private static final String TAG = "ReviewUtils";
    private static final ReviewUtils ourInstance = new ReviewUtils();
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Contract(pure = true)
    public static ReviewUtils getInstance() {
        return ourInstance;
    }

    private ReviewUtils() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();
    }

    private String userType = "";

    public void reviewTheUser(final Activity activity, String typeOfUser) {
        //activityReview = activity;
        userType = typeOfUser;

        if (activity instanceof SellerActivity) {
            mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.POSTED_BY).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showData(dataSnapshot, activity);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        } else if (activity instanceof BuyerActivity) {
            mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.PURCHASED_BY).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    showData(dataSnapshot, activity);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }


    }

    private void showData(@NotNull DataSnapshot dataSnapshot, Activity activity) {


        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {

                checkIfReviewIsRequired((Map<String, Object>) ds.getValue(), activity);
            }
        }


    }

    private void checkIfReviewIsRequired(@NotNull Map<String, Object> value, Activity activity) {
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
        if (value.get(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN) != null) {
            food.setCheckIfReviewDialogShouldBeShown((boolean) value.get(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN));
        }

        if (!food.getCheckIfOrderIsActive()
                && food.getCheckIfOrderIsPurchased()
                && !food.getCheckIfFoodIsInDraftMode()
                && !food.getCheckIfOrderIsBooked()
                && !food.getCheckIfOrderIsInProgress()
                && food.getCheckIfOrderIsCompleted()
                && food.getCheckIfReviewDialogShouldBeShown()
                ) {

            if (activity != null && activity instanceof SellerActivity) {
                FragmentTransaction ft = ((SellerActivity) activity).getSupportFragmentManager().beginTransaction();
                Fragment prev = ((SellerActivity) activity).getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);


                ReviewDialogFragment reviewDialogFragment = ReviewDialogFragment.newInstance(userType, food);

                reviewDialogFragment.show(ft, "dialog");
            } else if (activity != null && activity instanceof BuyerActivity) {
                FragmentTransaction ft = ((BuyerActivity) activity).getSupportFragmentManager().beginTransaction();
                Fragment prev = ((BuyerActivity) activity).getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);


                ReviewDialogFragment reviewDialogFragment = ReviewDialogFragment.newInstance(userType, food);

                reviewDialogFragment.show(ft, "dialog");
            }
        }
    }
}
