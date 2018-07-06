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
import sk.greate43.eatr.entities.AskForReview;
import sk.greate43.eatr.fragmentDialogs.ReviewDialogFragment;

public class ReviewUtils {
    private static final String TAG = "ReviewUtils";
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String userType = "";

    public static final ReviewUtils ourInstance = new ReviewUtils();

    @Contract(pure = true)
    public static ReviewUtils getOurInstance() {
        return ourInstance;
    }

    private ReviewUtils() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();
    }


    public void reviewTheUser(final Activity activity, String typeOfUser) {
        //activityReview = activity;
        userType = typeOfUser;

        if (activity instanceof SellerActivity) {
            mDatabaseReference.child(Constants.SELLER_REVIEW).orderByChild(Constants.POSTED_BY).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
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
           mDatabaseReference.child(Constants.BUYER_REVIEW).orderByChild(Constants.PURCHASED_BY).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
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
        AskForReview askForReview = new AskForReview();


        if (value.get(Constants.ORDER_ID) != null) {
            askForReview.setOrderId((String) value.get(Constants.ORDER_ID));
        }


        if (value.get(Constants.POSTED_BY) != null) {
            askForReview.setPostedBy((String) value.get(Constants.POSTED_BY));
        }
        if (value.get(Constants.PURCHASED_BY) != null) {
            askForReview.setPurchasedBy((String) value.get(Constants.PURCHASED_BY));
        }


        if (value.get(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN_FOR_BUYER) != null) {
            askForReview.setCheckIfReviewDialogShouldBeShownForBuyer((boolean) value.get(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN_FOR_BUYER));
        }

        if (value.get(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN_FOR_SELLER) != null) {
            askForReview.setCheckIfReviewDialogShouldBeShownForSeller((boolean) value.get(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN_FOR_SELLER));
        }


        if (activity != null && activity instanceof SellerActivity && askForReview.getCheckIfReviewDialogShouldBeShownForSeller()) {
            ReviewDialogFragment reviewDialogFragment = ReviewDialogFragment.newInstance(userType, askForReview);

            FragmentTransaction ft = ((SellerActivity) activity).getSupportFragmentManager().beginTransaction();

            Fragment prev = ((SellerActivity) activity).getSupportFragmentManager().findFragmentByTag(reviewDialogFragment.TAG_FRAGMENT);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);


            ft.add(reviewDialogFragment, reviewDialogFragment.TAG_FRAGMENT).commitAllowingStateLoss();

        } else if (activity != null && activity instanceof BuyerActivity && askForReview.getCheckIfReviewDialogShouldBeShownForBuyer()) {
            ReviewDialogFragment reviewDialogFragment = ReviewDialogFragment.newInstance(userType, askForReview);

            FragmentTransaction ft = ((BuyerActivity) activity).getSupportFragmentManager().beginTransaction();

            Fragment prev = ((BuyerActivity) activity).getSupportFragmentManager().findFragmentByTag(reviewDialogFragment.TAG_FRAGMENT);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);


            ft.add(reviewDialogFragment, reviewDialogFragment.TAG_FRAGMENT).commitAllowingStateLoss();


        }
    }

//    public void removeListener() {
////        if (buyerReviewListener != null) {
////            mDatabaseReference.child(Constants.BUYER_REVIEW).orderByChild(Constants.PURCHASED_BY).equalTo(user.getUid()).removeEventListener(buyerReviewListener);
////        }
////        if (sellerReviewListener != null) {
////            mDatabaseReference.child(Constants.SELLER_REVIEW).orderByChild(Constants.POSTED_BY).equalTo(user.getUid()).removeEventListener(sellerReviewListener);
////        }
//    }
}

