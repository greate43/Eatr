package sk.greate43.eatr.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;

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


        FirebaseApp.initializeApp(this);

    }

}
