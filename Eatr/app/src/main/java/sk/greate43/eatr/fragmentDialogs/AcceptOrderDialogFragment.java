package sk.greate43.eatr.fragmentDialogs;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.entities.Notification;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.utils.Constants;


public class AcceptOrderDialogFragment extends DialogFragment {

    public final String TAG_FRAGMENT = "AcceptOrderDialogFragment";
    Button yes;
    Button no;
    ImageView imgDishPhoto;
    Food food;
    private TextView tvDishName;
    private TextView tvNoOfServings;
    private TextView tvQuestion;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private Notification notification;
    private float avgRating;
    private Profile profile;
    private TextView tvBuyerName;
    private RatingBar ratingBar;
    private CircleImageView imgUserPhoto;

    public AcceptOrderDialogFragment() {
        // Required empty public constructor
    }

    public static AcceptOrderDialogFragment newInstance(Food food, Notification notification, Profile profile, float ratingAvg) {
        AcceptOrderDialogFragment fragment = new AcceptOrderDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARGS_FOOD, food);
        args.putSerializable(Constants.ARGS_PROFILE, profile);
        args.putSerializable(Constants.ARGS_NOTIFICATION, notification);
        args.putFloat(Constants.ARGS_AVG_RATING, ratingAvg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogThemeCustom);

        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable(Constants.ARGS_FOOD);
            notification = (Notification) getArguments().getSerializable(Constants.ARGS_NOTIFICATION);
            profile = (Profile) getArguments().getSerializable(Constants.ARGS_PROFILE);
            avgRating = getArguments().getFloat(Constants.ARGS_AVG_RATING);
        }
    }

    Notification notificationReply = null;
    private static final String TAG = "AcceptOrderDialogFragme";
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_accept_order, container, false);
        tvBuyerName = view.findViewById(R.id.fragment_accept_order_text_view_buyer_name);
        ratingBar = view.findViewById(R.id.fragment_accept_order_ratingBar);

        tvDishName = view.findViewById(R.id.fragment_accept_order_text_view_dish_name);
        tvQuestion = view.findViewById(R.id.fragment_accept_order_text_view_question);
        tvNoOfServings = view.findViewById(R.id.fragment_accept_order_text_view_no_of_servings);
        yes = view.findViewById(R.id.fragment_accept_order_button_yes);
        no = view.findViewById(R.id.fragment_accept_order_button_no);
        imgDishPhoto = view.findViewById(R.id.fragment_accept_order_image_view_food_pic);
        imgUserPhoto = view.findViewById(R.id.fragment_accept_order_image_view_user_pic);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        tvBuyerName.setText(profile.getFullname());
        if (food != null && profile != null && notification != null) {
            tvDishName.setText(String.valueOf(food.getDishName()));
            tvNoOfServings.setText(String.valueOf(food.getNumberOfServingsPurchased()));

            ratingBar.setRating(avgRating);

            if (food.getImageUri() != null && !food.getImageUri().isEmpty()) {
                Picasso.with(getActivity())
                        .load(food.getImageUri())
                        .fit()
                        .centerCrop()
                        .into(imgDishPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }


            if (profile.getProfilePhotoUri() != null && !profile.getProfilePhotoUri().isEmpty()) {
                Picasso.with(getActivity())
                        .load(profile.getProfilePhotoUri())
                        .fit()
                        .centerCrop()
                        .into(imgUserPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }

            
            final String notificationId = mDatabaseReference.push().getKey();

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDatabaseReference.child(Constants.NOTIFICATION)
                            .child(notification.getNotificationId())
                            .updateChildren(updateNotificationAlert(true, true));
                    notificationReply = new Notification();
                    notificationReply.setTitle(food.getDishName());
                    notificationReply.setMessage("Your Order Has Been Accepted and You you can get the Order from your Pick Up Place");
                    notificationReply.setSenderId(user.getUid());
                    notificationReply.setReceiverId(food.getPurchasedBy());
                    notificationReply.setOrderId(food.getPushId());
                    notificationReply.setCheckIfButtonShouldBeEnabled(false);
                    notificationReply.setCheckIfNotificationAlertShouldBeShown(true);
                    notificationReply.setCheckIfNotificationAlertShouldBeSent(true);
                    notificationReply.setNotificationId(notificationId);
                    notificationReply.setNotificationType(Constants.TYPE_NOTIFICATION_ORDER_REQUEST);
                    notificationReply.setTimeStamp(ServerValue.TIMESTAMP);

                    mDatabaseReference.child(Constants.FOOD)
                            .child(food.getPushId())
                            .updateChildren(updateUpdateProgress(true, false, false, false, false, false));
                    mDatabaseReference.child(Constants.NOTIFICATION).child(notificationId).setValue(notificationReply);

                    dismiss();

                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDatabaseReference.child(Constants.NOTIFICATION)
                            .child(notification.getNotificationId())
                            .updateChildren(updateNotificationAlert(true, false));
                    notificationReply = new Notification();
                    notificationReply.setTitle(food.getDishName());
                    notificationReply.setMessage("Your Order Has Been Rejected");
                    notificationReply.setSenderId(user.getUid());
                    notificationReply.setReceiverId(food.getPurchasedBy());
                    notificationReply.setOrderId(food.getPushId());
                    notificationReply.setCheckIfButtonShouldBeEnabled(false);
                    notificationReply.setCheckIfNotificationAlertShouldBeShown(true);
                    notificationReply.setCheckIfNotificationAlertShouldBeSent(true);
                    notificationReply.setNotificationId(notificationId);
                    notificationReply.setNotificationType(Constants.TYPE_NOTIFICATION_ORDER_REQUEST);
                    notificationReply.setTimeStamp(ServerValue.TIMESTAMP);

                    mDatabaseReference.child(Constants.FOOD)
                            .child(food.getPushId())
                            .updateChildren(updateUpdateProgress(false, false, false, false, false, false));

                    mDatabaseReference.child(Constants.NOTIFICATION).child(notificationId).setValue(notificationReply);
                    dismiss();

                }
            });
        }


        return view;
    }

    private Map<String, Object> updateNotificationAlert(boolean isShow, boolean isAccepted) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SHOWN, isShow);
        if (isAccepted) {
            result.put(Constants.MESSAGE, "You Have accepted this order");
        } else {
            result.put(Constants.MESSAGE, "You Have rejected this order");

        }
        result.put(Constants.CHECK_IF_BUTTON_SHOULD_BE_ENABLED, false);

        return result;
    }

    private Map<String, Object> updateUpdateProgress(boolean progress, boolean booked, boolean isActive, boolean isPurchase, boolean isCompeted, boolean isAccepted) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS, progress);
        result.put(Constants.CHECK_IF_ORDERED_IS_BOOKED, booked);
        result.put(Constants.CHECK_IF_ORDER_IS_ACTIVE, isActive);
        if (!progress && !isPurchase && !isCompeted) {
            result.put(Constants.PURCHASED_BY, "");
        }
        if (isAccepted) {
            result.put(Constants.CHECK_IF_ORDER_IS_ACCEPTED, isAccepted);

        }
        if (isPurchase) {
            result.put(Constants.CHECK_IF_ORDER_IS_PURCHASED, isPurchase);
        }
//        if (isPurchase && isCompeted) {
//          //  showReviewDialog();
//
//
//        }


        return result;
    }
}
