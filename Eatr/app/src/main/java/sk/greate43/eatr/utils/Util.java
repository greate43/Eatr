package sk.greate43.eatr.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import sk.greate43.eatr.service.NotificationService;

/**
 * Created by great on 3/23/2018.
 */

public class Util {
    public static void ScheduleAlarm(Context context) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (alarmManager != null) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, 1000, 5000, pendingIntent);
        }
    }

}
