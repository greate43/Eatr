package sk.greate43.eatr.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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

import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.entities.Review;
import sk.greate43.eatr.interfaces.Search;
import sk.greate43.eatr.interfaces.UpdateProfile;
import sk.greate43.eatr.utils.AcceptAndCompleteOrderUtils;
import sk.greate43.eatr.utils.Constants;
import sk.greate43.eatr.utils.DrawerUtil;
import sk.greate43.eatr.utils.ReviewUtils;
import sk.greate43.eatr.utils.Util;

public class BuyerActivity extends AppCompatActivity {
    private static final String TAG = "BuyerActivity";
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase database;
    private FirebaseStorage mStorage;
    private StorageReference storageRef;
    private UpdateProfile updateProfile;
    private Search search;
    private ValueEventListener profileValueListener;
    private ValueEventListener reviewValueListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer);
        Toolbar toolbar = findViewById(R.id.activity_buyer_toolbar);
        setSupportActionBar(toolbar);

        Util.ScheduleNotification(this);


        ReviewUtils.getOurInstance().reviewTheUser(this, Constants.TYPE_BUYER);

        AcceptAndCompleteOrderUtils.getOurInstance().checkIfOrderIsCompletedAndShowOrderCompleteDialog(this, Constants.TYPE_BUYER);

        updateProfile = DrawerUtil.getInstance().getCallback();

        DrawerUtil.getInstance().getDrawer(this, toolbar);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();


        profileValueListener = mDatabaseReference.child(Constants.PROFILE).child(user.getUid()).addValueEventListener(new ValueEventListener() {
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

    }


    public void setCallbackListener(Search search) {
        this.search = search;
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
//
//
//        ReviewUtils.getOurInstance().removeListener();
//        AcceptAndCompleteOrderUtils.getOurInstance().removeListener();
//        updateProfile = null;
//        search = null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (user != null) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            // Get the SearchView and set the searchable configuration
            final SearchView searchView = (SearchView) menu.findItem(R.id.menu_item_search).getActionView();
            // perform set on query text listener event
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (search != null) {
                        search.onSearchCompleted(query);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                    if (search != null) {
                        search.onSearchCompleted(newText);
                    }
                    return false;
                }
            });


            return true;


        }
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_sign_out:
                if (user != null) {

                    mAuth.signOut();


                    Intent intent = new Intent(BuyerActivity.this, MainActivity.class);

                    startActivity(intent);
                    finish();


                }
                return true;

            case R.id.menu_item_map:
                Intent intent = new Intent(BuyerActivity.this, ListOfAllPostedFoodsContainerMapActivity.class);
                startActivity(intent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    String userId = "";

    private void getMyOverallReview(String userId) {
        this.userId = userId;
        if (userId != null) {
            reviewValueListener = mDatabaseReference.child(Constants.REVIEW).orderByChild(Constants.USER_ID).equalTo(userId).addValueEventListener(new ValueEventListener() {
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


        if (review.getReviewType() != null && review.getReviewType().equals(Constants.REVIEW_FROM_SELLER)) {
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

    private void collectProfile(Map<String, Object> value) {
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
