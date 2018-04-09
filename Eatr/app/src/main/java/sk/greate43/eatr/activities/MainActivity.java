/**
 * Activities Package
 */
package sk.greate43.eatr.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import sk.greate43.eatr.R;
import sk.greate43.eatr.fragments.AuthenticationFragment;
import sk.greate43.eatr.interfaces.ReplaceFragment;


public class MainActivity extends AppCompatActivity implements ReplaceFragment {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (isGooglePlayServicesAvailable(this)) {

            Log.d(TAG, "onCreate: google service available and up to date");

        }


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.actvivity_main_frame_layout_fragment_container, AuthenticationFragment.newInstance())
                .commit();


    }


    @Override
    public void onFragmentReplaced(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.actvivity_main_frame_layout_fragment_container, fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }


    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

}