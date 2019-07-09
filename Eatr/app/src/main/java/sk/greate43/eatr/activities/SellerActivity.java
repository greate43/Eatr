package sk.greate43.eatr.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.entities.Review;
import sk.greate43.eatr.interfaces.UpdateProfile;
import sk.greate43.eatr.utils.AcceptAndCompleteOrderUtils;
import sk.greate43.eatr.utils.Constants;
import sk.greate43.eatr.utils.DrawerUtil;
import sk.greate43.eatr.utils.ReviewUtils;
import sk.greate43.eatr.utils.Util;

public class SellerActivity extends AppCompatActivity {
    private static final String TAG = "SellerActivity";
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    UpdateProfile updateProfile;
//    private ValueEventListener profileValueListener;
//    private ValueEventListener reviewValueListener;
    //
//    TextView tvFullName;
//    TextView tvUserType;
//    ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        Toolbar toolbar = findViewById(R.id.activity_seller_toolbar);
        setSupportActionBar(toolbar);

        Util.ScheduleNotification(this);
    //    Util.ScheduleExpireOrder(this);

        ReviewUtils.getOurInstance().reviewTheUser(this, Constants.TYPE_SELLER);


        updateProfile = DrawerUtil.getInstance().getCallback();

        DrawerUtil.getInstance().getDrawer(this, toolbar);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();


        mDatabaseReference.child(Constants.PROFILE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.d(TAG, "onDataChange: " + dataSnapshot);
                showData(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        getMyOverallReview(user.getUid());


        AcceptAndCompleteOrderUtils.getOurInstance().checkIfOrderIsBookedAndShowOrderAcceptDialog(this, Constants.TYPE_SELLER);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        if (user != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);

            return true;
        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_sign_out:
                if (user != null) {
                    mAuth.signOut();
                    finish();
                    Intent intent = new Intent(SellerActivity.this, MainActivity.class);

                    startActivity(intent);
                }


                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem search = menu.findItem(R.id.menu_item_search);
        if (search != null)
            search.setVisible(false);

        MenuItem map = menu.findItem(R.id.menu_item_map);
        if (map != null)
            map.setVisible(false);


        super.onPrepareOptionsMenu(menu);

        return true;
    }

    private String userId;

    private void getMyOverallReview(String userId) {
        this.userId = userId;
        if (userId != null) {
            mDatabaseReference.child(Constants.REVIEW).orderByChild(Constants.USER_ID).equalTo(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    showReviewData(dataSnapshot);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (profileValueListener != null) {
//            mDatabaseReference.child(Constants.PROFILE).child(user.getUid()).removeEventListener(profileValueListener);
//        }
//        if (reviewValueListener != null) {
//            mDatabaseReference.child(Constants.REVIEW).orderByChild(Constants.USER_ID).equalTo(userId).removeEventListener(reviewValueListener);
//        }


//        ReviewUtils.getOurInstance().removeListener();
//        AcceptAndCompleteOrderUtils.getOurInstance().removeListener();
        updateProfile = null;

    }

    private void showReviewData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        ;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            collectReview((Map<String, Object>) ds.getValue());
        }
        itemCount *= 3;
        ratingAvg = ratingAvg / itemCount;
        Log.d(TAG, "showReviewData: " + ratingAvg);
        if (updateProfile != null) {
            updateProfile.myOverAllRating(ratingAvg);
        }

        ratingAvg = 0;
        itemCount = 0;

    }

    private long itemCount = 0;
    private float ratingAvg = 0;

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


        if (review.getReviewType() != null && review.getReviewType().equals(Constants.REVIEW_FROM_BUYER)) {
            // reviews.add(review);
            Log.d(TAG, "collectReview: rating " + review.getQuestionOneAnswer());
            ratingAvg += (float) (review.getQuestionOneAnswer() + review.getQuestionTwoAnswer() + review.getQuestionThreeAnswer());
            Log.d(TAG, "collectReview: total " + ratingAvg);
            itemCount++;
        }


    }


    private void showData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        collectProfile((Map<String, Object>) dataSnapshot.getValue());


    }

    private void collectProfile(@NotNull Map<String, Object> value) {
        Profile profile = new Profile();
        profile.setUserId(String.valueOf(value.get(Constants.USER_ID)));
        profile.setFirstName(String.valueOf(value.get(Constants.FIRST_NAME)));
        profile.setLastName(String.valueOf(value.get(Constants.LAST_NAME)));
        profile.setProfilePhotoUri(String.valueOf(value.get(Constants.PROFILE_PHOTO_URI)));
        if (value.get(Constants.EMAIL) != null) {
            profile.setEmail(String.valueOf(value.get(Constants.EMAIL)));
        }
        profile.setUserType(String.valueOf(value.get(Constants.USER_TYPE)));

        if (updateProfile != null) {
            updateProfile.onNavDrawerDataUpdated(profile);
        }


    }


}
