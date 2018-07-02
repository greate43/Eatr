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


public class AcceptAndOrderCompleteDialog extends DialogFragment {

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
    private String userType;

    public AcceptAndOrderCompleteDialog() {
        // Required empty public constructor
    }

    public static AcceptAndOrderCompleteDialog newInstance(Food food, Notification notification, Profile profile, float ratingAvg, String userType) {
        AcceptAndOrderCompleteDialog fragment = new AcceptAndOrderCompleteDialog();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARGS_FOOD, food);
        args.putSerializable(Constants.ARGS_PROFILE, profile);
        args.putSerializable(Constants.ARGS_NOTIFICATION, notification);
        args.putFloat(Constants.ARGS_AVG_RATING, ratingAvg);
        args.putString(Constants.USER_TYPE, userType);
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
            userType = getArguments().getString(Constants.USER_TYPE);
        }
    }

    Notification notificationReply = null;
    private static final String TAG = "AcceptOrderDialogFragme";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dialog_for_accept_and_complete_order, container, false);
        tvBuyerName = view.findViewById(R.id.fragment_accept_order_text_view_user_name);
        ratingBar = view.findViewById(R.id.fragment_accept_order_ratingBar);

        tvDishName = view.findViewById(R.id.fragment_accept_order_text_view_dish_name);
        tvQuestion = view.findViewById(R.id.fragment_accept_order_text_view_question);
        tvNoOfServings = view.findViewById(R.id.fragment_accept_order_text_view_no_of_servings);
        tvQuestion = view.findViewById(R.id.fragment_accept_order_text_view_question);
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
            if (userType.equalsIgnoreCase(Constants.TYPE_BUYER)){
                tvQuestion.setText("Have you received the food?");
            } else  if (userType.equalsIgnoreCase(Constants.TYPE_SELLER)){
                tvQuestion.setText("Do you want to accept this Order?");
            }

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


            yes.setOnClickListener(v -> {

                mDatabaseReference.child(Constants.NOTIFICATION)
                        .child(notification.getNotificationId())
                        .updateChildren(updateNotificationAlert(true, true));


                sendNotification(true);
                dismiss();

            });

            no.setOnClickListener(v -> {
                mDatabaseReference.child(Constants.NOTIFICATION)
                        .child(notification.getNotificationId())
                        .updateChildren(updateNotificationAlert(true, false));


                sendNotification(false);
                dismiss();

            });
        }


        return view;
    }

    private void sendNotification(Boolean isOrderAccepted) {
        String notificationId = mDatabaseReference.push().getKey();
        Notification notificationReply = null;
        if (notification.getNotificationType().equals(Constants.TYPE_NOTIFICATION_ORDER_REQUEST)) {
            if (isOrderAccepted) {
                if (notification != null) {
                    notificationReply = new Notification();
                    notificationReply.setTitle(notification.getTitle());
                    notificationReply.setMessage("Your Order has Been acceptedby seller, you can get the Order from Pick Up location");
                    notificationReply.setSenderId(user.getUid());
                    notificationReply.setReceiverId(notification.getSenderId());
                    notificationReply.setOrderId(notification.getOrderId());
                    notificationReply.setCheckIfButtonShouldBeEnabled(false);
                    notificationReply.setCheckIfNotificationAlertShouldBeShown(true);
                    notificationReply.setCheckIfNotificationAlertShouldBeSent(true);
                    notificationReply.setNotificationId(notificationId);
                    notificationReply.setNotificationType(Constants.TYPE_NOTIFICATION_ORDER_REQUEST);
                    notificationReply.setTimeStamp(ServerValue.TIMESTAMP);

                    mDatabaseReference.child(Constants.FOOD)
                            .child(notification.getOrderId())
                            .updateChildren(updateUpdateProgress(true, false, false, false, false, false));

                }
            } else {
                if (notification != null) {
                    notificationReply = new Notification();
                    notificationReply.setTitle(notification.getTitle());
                    notificationReply.setMessage("Your Order has been rejected");
                    notificationReply.setSenderId(user.getUid());
                    notificationReply.setReceiverId(notification.getSenderId());
                    notificationReply.setOrderId(notification.getOrderId());
                    notificationReply.setCheckIfButtonShouldBeEnabled(false);
                    notificationReply.setCheckIfNotificationAlertShouldBeShown(true);
                    notificationReply.setCheckIfNotificationAlertShouldBeSent(true);
                    notificationReply.setNotificationId(notificationId);
                    notificationReply.setNotificationType(Constants.TYPE_NOTIFICATION_ORDER_REQUEST);
                    notificationReply.setTimeStamp(ServerValue.TIMESTAMP);

                    mDatabaseReference.child(Constants.FOOD)
                            .child(notification.getOrderId())
                            .updateChildren(updateUpdateProgress(false, false, false, false, false, false));

                }
            }
        } else if (notification.getNotificationType().equals(Constants.TYEPE_NOTIFICATION_ORDER_COMPLETED)) {
            if (isOrderAccepted) {
                if (notification != null) {
                    notificationReply = new Notification();
                    notificationReply.setTitle(notification.getTitle());
                    notificationReply.setMessage("Buyer has also marked order as Complete ");
                    notificationReply.setSenderId(user.getUid());
                    notificationReply.setReceiverId(notification.getSenderId());
                    notificationReply.setOrderId(notification.getOrderId());
                    notificationReply.setCheckIfButtonShouldBeEnabled(false);
                    notificationReply.setCheckIfNotificationAlertShouldBeShown(true);
                    notificationReply.setCheckIfNotificationAlertShouldBeSent(true);
                    notificationReply.setNotificationId(notificationId);
                    notificationReply.setNotificationType(Constants.TYEPE_NOTIFICATION_ORDER_COMPLETED);

                    notificationReply.setTimeStamp(ServerValue.TIMESTAMP);

                    mDatabaseReference.child(Constants.FOOD)
                            .child(notification.getOrderId())
                            .updateChildren(updateUpdateProgress(false, false, false, true, true, true));
                }
            } else {
                if (notification != null) {
                    notificationReply = new Notification();
                    notificationReply.setTitle(notification.getTitle());
                    notificationReply.setMessage("Buyer has marked order as incomplete");
                    notificationReply.setSenderId(user.getUid());
                    notificationReply.setReceiverId(notification.getSenderId());
                    notificationReply.setOrderId(notification.getOrderId());
                    notificationReply.setCheckIfButtonShouldBeEnabled(false);
                    notificationReply.setCheckIfNotificationAlertShouldBeShown(true);
                    notificationReply.setCheckIfNotificationAlertShouldBeSent(true);
                    notificationReply.setNotificationId(notificationId);
                    notificationReply.setNotificationType(Constants.TYEPE_NOTIFICATION_ORDER_COMPLETED);
                    notificationReply.setTimeStamp(ServerValue.TIMESTAMP);

                    mDatabaseReference.child(Constants.FOOD)
                            .child(notification.getOrderId())
                            .updateChildren(updateUpdateProgress(false, false, false, false, true, true));

                }
            }
        }


        if (notificationId != null) {
            mDatabaseReference.child(Constants.NOTIFICATION).child(notificationId).setValue(notificationReply);
        }
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
        if (isPurchase && isCompeted) {
            showReviewDialog();


        }


        return result;
    }

    private void showReviewDialog() {
        mDatabaseReference.child(Constants.SELLER_REVIEW).child(notification.getOrderId()).updateChildren(reviewUpdate(true));
        mDatabaseReference.child(Constants.BUYER_REVIEW).child(notification.getOrderId()).updateChildren(reviewUpdate(false));

    }

    private Map<String, Object> reviewUpdate(boolean isSeller) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.ORDER_ID, notification.getOrderId());
        result.put(Constants.POSTED_BY,notification.getSenderId());
        result.put(Constants.PURCHASED_BY,notification.getReceiverId());
        if (isSeller) {

            result.put(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN_FOR_SELLER, true);
        } else {
            result.put(Constants.CHECK_IF_REVIEW_DIALOG_SHOULD_BE_SHOWN_FOR_BUYER, true);
        }
        return result;
    }
}
