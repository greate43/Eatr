package sk.greate43.eatr.holders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.entities.Review;
import sk.greate43.eatr.utils.Constants;

/**
 * Created by great on 2/19/2018.
 */

public class ListOfAllPostedFoodViewHolder extends RecyclerView.ViewHolder {

    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private static final String TAG = "ListOfAllPostedFoodView";
    public ImageView imgFoodItem;
    public TextView tvStatus;
    public TextView tvLocation;
    public TextView tvDishName;
    public TextView tvTimeStamp;
    public TextView tvPrice;
    public TextView tvPostedbyName;
    public RatingBar ratingBar;
    public TextView tvPostedByLbl;
    public TextView tvRatingBarLbl;
    public ProgressBar progressBar;

    public ListOfAllPostedFoodViewHolder(View itemView) {
        super(itemView);
        tvStatus = itemView.findViewById(R.id.posted_food_list_status_text_view);
        imgFoodItem = itemView.findViewById(R.id.posted_food_list_food_item_image_view);
        tvLocation = itemView.findViewById(R.id.posted_food_list_location_text_view);
        tvDishName = itemView.findViewById(R.id.posted_food_list_food_item_dish_name);
        tvTimeStamp = itemView.findViewById(R.id.posted_food_list_food_item_timeStamp);
        tvPrice = itemView.findViewById(R.id.posted_food_list_item_price_text_view);
        tvPostedbyName = itemView.findViewById(R.id.posted_food_list_posted_by_name);
        ratingBar = itemView.findViewById(R.id.posted_food_list_ratingBar);
        tvPostedByLbl = itemView.findViewById(R.id.posted_food_list_posted_by_lbl);
        tvRatingBarLbl = itemView.findViewById(R.id.posted_food_list_rating_bar_lbl);
        progressBar =  itemView.findViewById(R.id.posted_food_list_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();
    }


    @SuppressLint("SetTextI18n")
    public void populate(Context context, Food food) {
        itemView.setTag(food);

        if (food.getImageUri() != null && !food.getImageUri().isEmpty()) {
            Picasso.with(context)
                    .load(food.getImageUri())
                    .fit()
                    .centerCrop()
                    .into(imgFoodItem, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: ");
                            progressBar.setVisibility(View.GONE);

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        tvTimeStamp.setText(DateUtils
                .getRelativeTimeSpanString(food.getTime(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        0));


        if (food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getCheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.GRAY);
            tvStatus.setText("Draft");
        } else if (food.getCheckIfOrderIsActive() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getCheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.GREEN);
            tvStatus.setText("Active");
        } else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getCheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.RED);
            tvStatus.setText("Expired");

        } else if (!food.getCheckIfOrderIsActive() && food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && food.getCheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.BLACK);
            tvStatus.setText("Sold");

        } else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getCheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.YELLOW);
            tvStatus.setText("Reserved");

        } else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && food.getCheckIfOrderIsInProgress() && !food.getCheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.BLUE);
            tvStatus.setText("In Progress");

        } else if (!food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && food.getCheckIfOrderIsCompleted()
                ) {
            tvStatus.setTextColor(Color.BLACK);
            tvStatus.setText("Completed");

        }

        tvPrice.setText(String.valueOf("Rs : " + food.getPrice()));
        tvLocation.setText(food.getPickUpLocation());

        tvDishName.setText(food.getDishName());

        getSellerDetailsAndReview(food.getPostedBy());
    }


    private void getSellerDetailsAndReview(String postedBy) {
        if (postedBy != null) {
            mDatabaseReference.child(Constants.PROFILE).orderByChild(Constants.USER_ID).equalTo(postedBy).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    showProfileData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }

        if (postedBy != null) {
            DatabaseReference reviewRef = mDatabaseReference.child(Constants.REVIEW);
            Query query = reviewRef.orderByChild(Constants.USER_ID).equalTo(postedBy);

            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    showReviewData(dataSnapshot);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }


    }
   private long itemCount = 0;
    private void showReviewData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

      ;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            collectReview((Map<String, Object>) ds.getValue());
        }

        ratingAvg = ratingAvg / itemCount;
        Log.d(TAG, "showReviewData: itemcount "+ itemCount);
        Log.d(TAG, "showReviewData: avg "+ratingAvg);
        ratingBar.setRating(ratingAvg);
        ratingAvg = 0;
        itemCount = 0;

    }

    private float ratingAvg = 0;


    private void collectReview(Map<String, Object> value) {
        Review review = new Review();
        review.setReviewId((String) value.get(Constants.RECEIVER_ID));
        review.setOrderId((String) value.get(Constants.ORDER_ID));
        review.setOverAllFoodQuality(Double.parseDouble(String.valueOf(value.get(Constants.OVER_ALL_FOOD_QUALITY))));
        review.setReviewGivenBy((String) value.get(Constants.REVIEW_GIVEN_BY));
        review.setUserId((String) value.get(Constants.USER_ID));
        review.setReviewType((String) value.get(Constants.REVIEW_TYPE));


        if (review.getReviewType() != null && review.getReviewType().equals(Constants.REVIEW_FROM_BUYER)) {
            // reviews.add(review);
            Log.d(TAG, "collectReview: rating "+review.getOverAllFoodQuality());
            ratingAvg += (float) (review.getOverAllFoodQuality());
            Log.d(TAG, "collectReview: total "+ratingAvg);
             itemCount ++;
        }


    }

    private void showProfileData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            collectProfile((Map<String, Object>) ds.getValue());
        }
    }

    private void collectProfile(Map<String, Object> value) {
        Profile profile = new Profile();
        profile.setUserId(String.valueOf(value.get(Constants.USER_ID)));
        profile.setFirstName(String.valueOf(value.get(Constants.FIRST_NAME)));
        profile.setLastName(String.valueOf(value.get(Constants.LAST_NAME)));
        profile.setProfilePhotoUri(String.valueOf(value.get(Constants.PROFILE_PHOTO_URI)));
        if (String.valueOf(value.get(Constants.EMAIL)) != null) {
            profile.setEmail(String.valueOf(value.get(Constants.EMAIL)));
        }
        profile.setUserType(String.valueOf(value.get(Constants.USER_TYPE)));


        tvPostedbyName.setText(String.valueOf(profile.getFullname()));

    }


}
