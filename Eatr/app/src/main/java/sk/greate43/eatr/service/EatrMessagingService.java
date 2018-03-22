package sk.greate43.eatr.service;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import sk.greate43.eatr.R;

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
//        Intent intent = new Intent(this, YourActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);

        long[] pattern = {500, 500, 500, 500, 500};

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "M_CH_ID")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setVibrate(pattern)
                .setLights(Color.BLUE, 1, 1)
                .setSound(defaultSoundUri)
                //.setContentIntent(pendingIntent)
                ;

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        }
    }

}
