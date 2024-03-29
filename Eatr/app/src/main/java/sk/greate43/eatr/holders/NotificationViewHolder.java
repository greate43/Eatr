package sk.greate43.eatr.holders;

import android.content.Context;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import sk.greate43.eatr.entities.Notification;
import sk.greate43.eatr.utils.Constants;

/**
 * Created by great on 3/22/2018.
 */

public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
    private static final String TAG = "NotificationViewHolder";
    public TextView tvTitle;
    public TextView tvMessage;
    public CircleImageView img;
    public Button yes;
    public Button no;
    private Notification notification;
    private View view;

    private DatabaseReference mDatabaseReference;
    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {


            switch (item.getItemId()) {
                case 1:
                    if (notification != null) {
                        mDatabaseReference.child(Constants.NOTIFICATION).child(notification.getNotificationId()).removeValue();


                        Snackbar.make(view, "Deleted " + notification.getNotificationId(), Snackbar.LENGTH_LONG).show();


                        break;
                    }
                    break;
            }
            return true;
        }
    };
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        view = itemView;
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();

        tvTitle = itemView.findViewById(R.id.notification_list_title);
        tvMessage = itemView.findViewById(R.id.notification_list_message);
        img = itemView.findViewById(R.id.notification_list_circleImageView);
        yes = itemView.findViewById(R.id.notification_list_button_yes);
        no = itemView.findViewById(R.id.notification_list_button_no);
        yes.setOnClickListener(v -> {
            if (notification != null) {
                mDatabaseReference.child(Constants.NOTIFICATION)
                        .child(notification.getNotificationId())
                        .updateChildren(updateNotificationAlert(true, true));


                sendNotification(true);
            }
        });
        no.setOnClickListener(v -> {
            if (notification != null) {
                mDatabaseReference.child(Constants.NOTIFICATION)
                        .child(notification.getNotificationId())
                        .updateChildren(updateNotificationAlert(true, false));

                sendNotification(false);


            }
        });
    }

    private void sendNotification(Boolean isOrderAccepted) {
        String notificationId = mDatabaseReference.push().getKey();
        Notification notificationReply = null;
        if (notification.getNotificationType().equals(Constants.TYPE_NOTIFICATION_ORDER_REQUEST)) {
            if (isOrderAccepted) {
                if (notification != null) {
                    notificationReply = new Notification();
                    notificationReply.setTitle(notification.getTitle());

                    notificationReply.setMessage("Your Order has Been accepted by seller, you can get the Order from Pick Up location");
                    notificationReply.setSenderId(user.getUid());
                    notificationReply.setReceiverId(notification.getSenderId());
                    notificationReply.setOrderId(notification.getOrderId());
                    notificationReply.setCheckIfButtonShouldBeEnabled(false);
                    notificationReply.setCheckIfNotificationAlertShouldBeShown(true);
                    notificationReply.setCheckIfNotificationAlertShouldBeSent(true);
                    notificationReply.setNotificationId(notificationId);
                    notificationReply.setNotificationType(Constants.TYPE_NOTIFICATION_ORDER_REQUEST);
                    notificationReply.setTimeStamp(ServerValue.TIMESTAMP);
                    notificationReply.setCheckIfDialogShouldBeShown(false);
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
                    notificationReply.setCheckIfDialogShouldBeShown(false);

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
                    notificationReply.setCheckIfDialogShouldBeShown(false);

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
                    notificationReply.setCheckIfDialogShouldBeShown(false);

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

    private Map<String, Object> updateNotificationAlert(boolean isShow, boolean isAccepted) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SHOWN, isShow);
        if (isAccepted) {
            result.put(Constants.MESSAGE, "You have accepted this order");
        } else {
            result.put(Constants.MESSAGE, "You have rejected this order");

        }
        result.put(Constants.CHECK_IF_BUTTON_SHOULD_BE_ENABLED, false);
        result.put(Constants.CHECK_IF_DIALOG_SHOULD_BE_SHOWN, false);

        return result;
    }

    public void populate(Notification notification, Context context) {
        this.notification = notification;
        if (notification.getNotificationImage() != null && !notification.getNotificationImage().isEmpty()) {
            Picasso.get()
                    .load(notification.getNotificationImage())
                    .fit()
                    .centerCrop()
                    .into(img);
        } else {
            Log.d(TAG, "populate: ");
            Picasso.get()
                    .load(R.drawable.logo)
                    .fit()
                    .centerCrop()
                    .into(img);
        }
        tvMessage.setText(notification.getMessage());
        tvTitle.setText(notification.getTitle());

        if (notification.getCheckIfButtonShouldBeEnabled()) {
            yes.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
        } else {
            yes.setVisibility(View.GONE);
            no.setVisibility(View.GONE);
            activateContextMenu();
        }
    }


    @Override
    public void onClick(View v) {

    }

    private void activateContextMenu() {
        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem Delete = menu.add(Menu.NONE, 1, 1, "Delete");
        Delete.setOnMenuItemClickListener(onEditMenu);
    }
}
