package sk.greate43.eatr.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import sk.greate43.eatr.R;
import sk.greate43.eatr.activities.MainActivity;

/**
 * Created by great on 3/20/2018.
 */

public class EatrMessagingService extends FirebaseMessagingService {
    private static final String TAG = "EatrMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String body = "";
        String title = "";
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {

            body = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();

            Log.d(TAG, "onMessageReceived: Title " + title + " body " + body);


        }

        sendNotificationData(title, body); //send notification to user


    }

    @Override
    public void onMessageSent(String msgId) {
        Log.d(TAG, "onMessageSent: " + msgId);
    }

    @Override
    public void onSendError(String msgId, Exception e) {
        Log.d(TAG, "onSendError: " + msgId);
        Log.d(TAG, "Exception: " + e);
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
                .setSmallIcon(R.drawable.logo)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, mBuilder.build());
        }
    }

}
