package sk.greate43.eatr.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.MainActivity;
import sk.greate43.eatr.entities.Notification;
import sk.greate43.eatr.utils.Constants;

/**
 * Created by great on 3/24/2018.
 */

public class NotificationJobService extends JobService {
    private static final String TAG = "NotificationJobService";


    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    public boolean onStartJob(JobParameters job) {

        Log.d(TAG, "onStartJob: ");
        mDatabaseReference.child(Constants.NOTIFICATION).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        Log.d(TAG, "onStopJob: ");
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        Log.d(TAG, "onCreate: ");

        database = FirebaseDatabase.getInstance();

        mDatabaseReference = database.getReference();
    }


    private void showData(DataSnapshot dataSnapshot) {


        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {
                collectNotification((Map<String, Object>) ds.getValue());
            }
        }


    }

    private void collectNotification(Map<String, Object> value) {
        Notification notification = new Notification();
        notification.setOrderId(String.valueOf(value.get(Constants.ORDER_ID)));
        notification.setNotificationId(String.valueOf(value.get(Constants.NOTIFICATION_ID)));
        notification.setMessage(String.valueOf(value.get(Constants.MESSAGE)));
        notification.setTitle(String.valueOf(value.get(Constants.TITLE)));
        notification.setCheckIfButtonShouldBeEnabled((boolean) value.get(Constants.CHECK_IF_BUTTON_SHOULD_BE_ENABLED));
        if (value.get(Constants.NOTIFICATION_IMAGE) != null) {
            notification.setNotificationImage(String.valueOf(Constants.NOTIFICATION_IMAGE));
        }
        notification.setCheckIfNotificationAlertShouldBeSent((boolean) value.get(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SENT));
        notification.setCheckIfNotificationAlertShouldBeShown((boolean) value.get(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SHOWN));
        notification.setSenderId(String.valueOf(value.get(Constants.SENDER_ID)));
        notification.setReceiverId(String.valueOf(value.get(Constants.RECEIVER_ID)));
        notification.setNotificationType(String.valueOf(value.get(Constants.NOTIFICATION_TYPE)));
        if (notification.getReceiverId().equalsIgnoreCase(user.getUid()) && notification.getCheckIfNotificationAlertShouldBeSent()) {

            sendNotificationData(notification.getTitle(), notification.getMessage());
            mDatabaseReference.child(Constants.NOTIFICATION).child(notification.getNotificationId()).updateChildren(updateNotification());

        }


    }

    private Map<String, Object> updateNotification() {
        HashMap<String, Object> results = new HashMap<>();
        results.put(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SENT, false);
        return results;
    }

    private void sendNotificationData(String messageTitle, String messageBody) {
        Log.d(TAG, "sendNotificationData: title= " + messageTitle + " " + "message= " + messageBody);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String CHANNEL_ID = "my_channel_01";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }
    }
}
