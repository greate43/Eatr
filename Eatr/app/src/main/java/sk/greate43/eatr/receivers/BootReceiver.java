package sk.greate43.eatr.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sk.greate43.eatr.utils.Util;

/**
 * Created by great on 3/23/2018.
 */

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Util.ScheduleAlarm(context);
    }
}
