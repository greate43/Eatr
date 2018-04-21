package sk.greate43.eatr.fragmentDialogs;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.AskForReview;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.entities.Review;
import sk.greate43.eatr.utils.Constants;


public class ReviewDialogFragment extends DialogFragment {

    private static final String TAG = "ReviewDialogFragment";
    TextView tv;
    RatingBar rbReview;
    CircleImageView imgUserPic;
    AskForReview askForReview;
    String userType;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Button btnSubmit;
    public String TAG_FRAGMENT = "ReviewDialogFragment";

    public ReviewDialogFragment() {
        // Required empty public constructor
    }

    public static ReviewDialogFragment newInstance(String userType, AskForReview askForReview) {
        ReviewDialogFragment fragment = new ReviewDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_TYPE, userType);
        args.putSerializable(Constants.ARGS_ASK_FOR_REVIEW, askForReview);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogThemeCustom);

        if (getArguments() != null) {
            askForReview = (AskForReview) getArguments().getSerializable(Constants.ARGS_ASK_FOR_REVIEW);
            userType = getArguments().getString(Constants.USER_TYPE);

        }
    }

    float ratingValue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_review, container, false);
        tv = view.findViewById(R.id.fragment_dialog_review_text_view);
        imgUserPic = view.findViewById(R.id.fragment_dialog_review_circleImageView_user_image);
        rbReview = view.findViewById(R.id.fragment_dialog_review_ratingBar);
        btnSubmit = view.findViewById(R.id.fragment_dialog_button_submit_review);

        btnSubmit.setEnabled(false);
        btnSubmit.setAlpha(0.3f);


        rbReview.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                btnSubmit.setEnabled(true);
                btnSubmit.setAlpha(1f);
                ratingValue = rating;

            }
        });


        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        if (askForReview != null && userType.equalsIgnoreCase(Constants.TYPE_BUYER)) {

            mDatabaseReference.child(Constants.PROFILE).orderByChild(Constants.USER_ID).equalTo(askForReview.getPostedBy()).addValueEventListener(new ValueEventListener() {
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

            tv.setText("How Much Would You Rate Order Overall Food Quality?");

        } else if (askForReview != null && userType.equalsIgnoreCase(Constants.TYPE_SELLER)) {
            Log.d(TAG, "onCreateView: "+askForReview.getPurchasedBy());
            mDatabaseReference.child(Constants.PROFILE).orderByChild(Constants.USER_ID).equalTo(askForReview.getPurchasedBy()).addValueEventListener(new ValueEventListener() {
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


            tv.setText("How Much Would You Rate Buyer ?");

        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reviewId = mDatabaseReference.push().getKey();

                Review review = new Review();
                if (userType.equalsIgnoreCase(Constants.TYPE_SELLER)) {
                    review.setReviewId(reviewId);
                    review.setOrderId(askForReview.getOrderId());
                    review.setOverAllFoodQuality(ratingValue);
                    review.setReviewGivenBy(askForReview.getPostedBy());
                    review.setUserId(askForReview.getPurchasedBy());
                    review.setReviewType(Constants.REVIEW_FROM_SELLER);
                    mDatabaseReference.child(Constants.SELLER_REVIEW).child(askForReview.getOrderId()).child(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN_FOR_SELLER).setValue(false);


                } else if (userType.equalsIgnoreCase(Constants.TYPE_BUYER)) {
                    review.setReviewId(reviewId);
                    review.setOrderId(askForReview.getOrderId());
                    review.setOverAllFoodQuality(ratingValue);
                    review.setReviewGivenBy(askForReview.getPurchasedBy());
                    review.setUserId(askForReview.getPostedBy());
                    review.setReviewType(Constants.REVIEW_FROM_BUYER);

                    mDatabaseReference.child(Constants.BUYER_REVIEW).child(askForReview.getOrderId()).child(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN_FOR_BUYER).setValue(false);
                }
                mDatabaseReference.child(Constants.REVIEW).child(reviewId).setValue(review);


                dismiss();
            }
        });
        return view;
    }

    private void showData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }
        if (dataSnapshot.getValue() != null) {
            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                collectProfile((Map<String, Object>) ds.getValue());
            }
        }


    }

    private void collectProfile(Map<String, Object> value) {
        Log.d(TAG, "collectProfile: " + value);
        Profile profile = new Profile();
        profile.setUserId(String.valueOf(value.get(Constants.USER_ID)));
        profile.setFirstName(String.valueOf(value.get(Constants.FIRST_NAME)));
        profile.setLastName(String.valueOf(value.get(Constants.LAST_NAME)));
        profile.setProfilePhotoUri(String.valueOf(value.get(Constants.PROFILE_PHOTO_URI)));
        if (String.valueOf(value.get(Constants.EMAIL)) != null) {
            profile.setEmail(String.valueOf(value.get(Constants.EMAIL)));
        }
        profile.setUserType(String.valueOf(value.get(Constants.USER_TYPE)));


        if (profile.getProfilePhotoUri() != null && !profile.getProfilePhotoUri().isEmpty()) {
            Picasso.with(getActivity())
                    .load(profile.getProfilePhotoUri())
                    .fit()
                    .centerCrop()
                    .into(imgUserPic, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: ");
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }


    }

}
