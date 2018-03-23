package sk.greate43.eatr.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "NotificationViewHolder";
    public TextView tvTitle;
    public TextView tvMessage;
    public CircleImageView img;
    public Button yes;
    public Button no;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public NotificationViewHolder(View itemView) {
        super(itemView);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();

        tvTitle = itemView.findViewById(R.id.notification_list_title);
        tvMessage = itemView.findViewById(R.id.notification_list_message);
        img = itemView.findViewById(R.id.notification_list_circleImageView);
        yes = itemView.findViewById(R.id.notification_list_button_yes);
        no = itemView.findViewById(R.id.notification_list_button_no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notification != null){
                    mDatabaseReference.child(Constants.NOTIFICATION).child(notification.getNotificationId()).updateChildren(updateNotification());

                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private Map<String,Object> updateNotification() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SHOWN,false);
        return result;
    }

    Notification notification;

    public void populate(Notification notification, Context context) {
        this.notification = notification;
        if (notification.getNotificationImage() != null && !notification.getNotificationImage().isEmpty()) {
            Picasso.with(context)
                    .load(notification.getNotificationImage())
                    .fit()
                    .centerCrop()
                    .into(img, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: ");
                        }

                        @Override
                        public void onError() {

                        }
                    });
        } else {
            Log.d(TAG, "populate: ");
            Picasso.with(context)
                    .load(R.drawable.ic_launcher_round)
                    .fit()
                    .centerCrop()
                    .into(img, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: ");
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
        tvMessage.setText(notification.getMessage());
        tvTitle.setText(notification.getTitle());

        if (notification.getCheckIfButtonShouldBeEnabled()) {
            yes.setVisibility(View.VISIBLE);
            no.setVisibility(View.VISIBLE);
        } else {
            yes.setVisibility(View.GONE);
            no.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClick(View v) {

    }
}
