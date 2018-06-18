package sk.greate43.eatr.utils;

import android.content.Context;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import sk.greate43.eatr.service.NotificationJobService;

/**
 * Created by great on 3/24/2018.
 */

public class Util {

    public static void ScheduleNotification(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job myJob = dispatcher.newJobBuilder()
                // the JobService that will be called
                .setService(NotificationJobService.class)
                // uniquely identifies the job
                .setTag("NotificationAlert")
                // one-off job
                .setRecurring(true)
                // don't persist forever
                .setLifetime(Lifetime.FOREVER)
                // start between 0 and 15 seconds from now
                .setTrigger(Trigger.executionWindow(0, 15))
                // don't overwrite an existing job with the same tag
                .setReplaceCurrent(false)
                // retry with exponential backoff
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                // constraints that need to be satisfied for the job to run
                .setConstraints(
                        // only run on an unmetered network
                        Constraint.ON_ANY_NETWORK
                )

                .build();

        dispatcher.mustSchedule(myJob);
    }



}
