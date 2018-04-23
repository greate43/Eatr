package sk.greate43.eatr.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.entities.Notification;
import sk.greate43.eatr.utils.Constants;


public class AcceptOrderFragment extends Fragment {

    Button yes;
    Button no;
    ImageView imgDishPhoto;
    Food food;
    private TextView tvDishName;
    private TextView tvNoOfServings;
    private TextView tvWhoWantsToOrder;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public AcceptOrderFragment() {
        // Required empty public constructor
    }

    public static AcceptOrderFragment newInstance(Food food) {
        AcceptOrderFragment fragment = new AcceptOrderFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARGS_FOOD, food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable(Constants.ARGS_FOOD);
        }
    }

    Notification notificationReply = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_accept_order, container, false);
        tvDishName = view.findViewById(R.id.fragment_accept_order_text_view_dish_name);
        tvWhoWantsToOrder = view.findViewById(R.id.fragment_accept_order_text_view_who_wants_to_order);
        tvNoOfServings = view.findViewById(R.id.fragment_accept_order_text_view_no_of_servings_lbl);
        yes = view.findViewById(R.id.fragment_accept_order_button_yes);
        no = view.findViewById(R.id.fragment_accept_order_button_no);
        imgDishPhoto = view.findViewById(R.id.fragment_accept_order_image_view_food_pic);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        if (food != null) {
            tvDishName.setText(String.valueOf(food.getDishName()));
            tvNoOfServings.setText(String.valueOf(food.getNumberOfServingsPurchased()));
            final String notificationId = mDatabaseReference.push().getKey();

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


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

                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
