package sk.greate43.eatr.application;

import android.app.Application;
import android.os.StrictMode;

import com.google.firebase.FirebaseApp;

import sk.greate43.eatr.BuildConfig;

public class EatrApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);
      //  enableStrictModePolicy();
        FirebaseApp.initializeApp(this);

    }

    private void enableStrictModePolicy() {
        if (BuildConfig.DEBUG) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build();

            StrictMode.setThreadPolicy(policy);
        }
    }
}
