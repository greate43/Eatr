package sk.greate43.eatr.holders;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.entities.Review;
import sk.greate43.eatr.fragments.PostedFoodFragment;
import sk.greate43.eatr.utils.Constants;

/**
 * Created by great on 11/12/2017.
 */

public class PostedFoodViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    private static final String TAG = "PostedFoodViewHolder";
    public ImageView imgFoodItem;
    public TextView tvStatus;
    public TextView tvLocation;
    public TextView tvDishName;
    public TextView tvTimeStamp;
    public TextView tvPrice;
    public TextView tvPostedbyName;
    public TextView tvPostedByLbl;
    public TextView tvRatingBarLbl;
    private TextView tvNoReviewGiven;


    public RatingBar ratingBar;
    public ProgressBar progressBar;
    Food food;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference storageReference;
    //ADD AN ONMENUITEM LISTENER TO EXECUTE COMMANDS ONCLICK OF CONTEXT MENU TASK
    private EditPostedFood editPostedFood;
    private int position;
    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {


            switch (item.getItemId()) {
                case 1:
                    if (editPostedFood != null) {
                        editPostedFood.onEdit(food, position);
                    }
                    break;

                case 2:
                    if (editPostedFood != null) {
                        editPostedFood.onDelete(food, position);
                    }

                    break;
            }
            return true;
        }
    };

    public PostedFoodViewHolder(View itemView) {
        super(itemView);
        tvStatus = itemView.findViewById(R.id.posted_food_list_status_text_view);
        imgFoodItem = itemView.findViewById(R.id.posted_food_list_food_item_image_view);
        tvLocation = itemView.findViewById(R.id.posted_food_list_location_text_view);
        tvDishName = itemView.findViewById(R.id.posted_food_list_food_item_dish_name);
        tvTimeStamp = itemView.findViewById(R.id.posted_food_list_food_item_timeStamp);
        tvPrice = itemView.findViewById(R.id.posted_food_list_item_price_text_view);
        tvPostedbyName = itemView.findViewById(R.id.posted_food_list_posted_by_name);
        ratingBar = itemView.findViewById(R.id.posted_food_list_ratingBar);
        tvNoReviewGiven = itemView.findViewById(R.id.posted_food_list_no_review_given);
        //ratingBar.setEnabled(false);

        tvPostedByLbl = itemView.findViewById(R.id.posted_food_list_posted_by_lbl);
        tvRatingBarLbl = itemView.findViewById(R.id.posted_food_list_rating_bar_lbl);
        progressBar = itemView.findViewById(R.id.posted_food_list_progress_bar);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

    }

    public void populate(Context context, Food food, PostedFoodFragment postedFoodFragment, int position) {
        itemView.setTag(food);
        this.food = food;
        this.position = position;
        editPostedFood = postedFoodFragment;
        if (food.getImageUri() != null && !food.getImageUri().isEmpty()) {
            Picasso.get()
                    .load(food.getImageUri())
                    .fit()
                    .centerCrop()
                    .into(imgFoodItem);
        }

        if (food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsActive() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getCheckIfOrderIsCompleted()
                ) {
            activateContextMenu();
            tvStatus.setTextColor(Color.GRAY);
            tvStatus.setText("Draft");
        } else if (food.getCheckIfOrderIsActive() && !food.getCheckIfFoodIsInDraftMode() && !food.getCheckIfOrderIsPurchased() && !food.getCheckIfOrderIsBooked() && !food.getCheckIfOrderIsInProgress() && !food.getCheckIfOrderIsCompleted()
                ) {
            activateContextMenu();
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
        tvTimeStamp.setText(DateUtils
                .getRelativeTimeSpanString(food.getTime(),
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        0));

        tvDishName.setText(food.getDishName());

        getSellerDetailsAndReview(food.getPostedBy(), food.getPushId());
    }

    private void activateContextMenu() {
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem Edit = menu.add(Menu.NONE, 1, 1, "Edit");
        MenuItem Delete = menu.add(Menu.NONE, 2, 2, "Delete");
        Edit.setOnMenuItemClickListener(onEditMenu);
        Delete.setOnMenuItemClickListener(onEditMenu);
    }

    private void getSellerDetailsAndReview(String postedBy, String orderId) {
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
            mDatabaseReference.child(Constants.REVIEW).orderByChild(Constants.ORDER_ID).equalTo(orderId).addValueEventListener(new ValueEventListener() {
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

    private void showReviewData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            collectReview((Map<String, Object>) ds.getValue());
        }

    }

    float avgReview = 0;

    private void collectReview(Map<String, Object> value) {
        Review review = new Review();
        review.setReviewId((String) value.get(Constants.REVIEW_ID));
        review.setOrderId((String) value.get(Constants.ORDER_ID));
        if (value.get(Constants.QUESTION_ONE_ANSWER) != null)
            review.setQuestionOneAnswer(Double.parseDouble(String.valueOf(value.get(Constants.QUESTION_ONE_ANSWER))));

        if (value.get(Constants.QUESTION_TWO_ANSWER) != null)
            review.setQuestionTwoAnswer(Double.parseDouble(String.valueOf(value.get(Constants.QUESTION_TWO_ANSWER))));

        if (value.get(Constants.QUESTION_THREE_ANSWER) != null)
            review.setQuestionThreeAnswer(Double.parseDouble(String.valueOf(value.get(Constants.QUESTION_THREE_ANSWER))));

        review.setReviewGivenBy((String) value.get(Constants.REVIEW_GIVEN_BY));
        review.setUserId((String) value.get(Constants.USER_ID));
        review.setReviewType((String) value.get(Constants.REVIEW_TYPE));
//        ratingBar.setVisibility(View.VISIBLE);
//        tvNoReviewGiven.setVisibility(View.GONE);


        if (review.getReviewType() != null
                && review.getReviewType().equals(Constants.REVIEW_FROM_BUYER)
                && !food.getCheckIfOrderIsActive()
                && food.getCheckIfOrderIsPurchased()
                && !food.getCheckIfFoodIsInDraftMode()
                && !food.getCheckIfOrderIsBooked()
                && !food.getCheckIfOrderIsInProgress()
                && food.getCheckIfOrderIsCompleted()
                ) {
            avgReview = (float)( (review.getQuestionOneAnswer() + review.getQuestionTwoAnswer() + review.getQuestionThreeAnswer())/3);
            Log.d(TAG, "collectReview: "+avgReview);
            ratingBar.setRating(avgReview);

        }

        avgReview = 0;


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

    public interface EditPostedFood {
        void onEdit(Food food, int position);

        void onDelete(Food food, int position);
    }

}



