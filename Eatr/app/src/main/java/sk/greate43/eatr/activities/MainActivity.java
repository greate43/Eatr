/**
 * Activities Package
 */
package sk.greate43.eatr.activities;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import sk.greate43.eatr.R;
import sk.greate43.eatr.fragments.AuthenticationFragment;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.AvailabilityInCities;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static sk.greate43.eatr.utils.Constants.REQUEST_FINE_LOCATION_PERMISSION;


public class MainActivity extends AppCompatActivity implements ReplaceFragment, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "MainActivity";

    private FusedLocationProviderClient mFusedLocationClient;
    private String mCity;
    private GoogleApiClient mGoogleApiClient;
    private View mView;
    private Button gpsOnBtn;
    private Location mLocation;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private int REQUEST_CHECK_SETTINGS = 12112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView = findViewById(R.id.actvivity_main_frame_layout_fragment_container);
        gpsOnBtn = findViewById(R.id.activity_main_turn_gps_on);

        if (isGooglePlayServicesAvailable(this)) {

            Log.d(TAG, "onCreate: google service available and up to date");

        }

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        if (!checkIfGpsIsEnabled()) {
            createLocationRequests();
            disableUi();
        } else {
            enableUi();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.actvivity_main_frame_layout_fragment_container, AuthenticationFragment.newInstance())
                .commit();


        gpsOnBtn.setOnClickListener(v -> {
            if (!checkIfGpsIsEnabled()) {
                createLocationRequests();
            }

        });
    }

    private void createLocationRequests() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.SUCCESS:
                    getLastKnowLocation();
                    break;

                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                    break;
                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                    break;
                case LocationSettingsStatusCodes.CANCELED:
                    View view = findViewById(R.id.actvivity_main_frame_layout_fragment_container);

                    Snackbar.make(view, " App Might Not be Fully Functional if Gps Is Off   ", Snackbar.LENGTH_LONG).show();
                    disableUi();
                    break;
            }
        });
    }

    private void enableUi() {
        mView.setVisibility(View.VISIBLE);
        gpsOnBtn.setVisibility(View.GONE);
    }

    private void disableUi() {
        mView.setVisibility(View.GONE);
        gpsOnBtn.setVisibility(View.VISIBLE);
    }

    public void onStart() {
        mGoogleApiClient.connect();
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        Log.d(TAG, "onStop: ");
        super.onStop();
    }

//    private void askUserToStartGpsDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Would You Like To Turn On The Gps?");
//        builder.setPositiveButton(android.R.string.yes, (dialog, id) -> {
////            mView.setVisibility(View.VISIBLE);
////            gpsOnBtn.setVisibility(View.GONE);
//
//            startGpsFromSettings();
//            dialog.dismiss();
//        });
//        builder.setNegativeButton(R.string.No, (dialog, id) -> {
//            View view = findViewById(R.id.actvivity_main_frame_layout_fragment_container);
//            Snackbar.make(view, " App Might Not be Fully Functional if Gps Is Off   ", Snackbar.LENGTH_LONG).show();
//            disableUi();
//            dialog.dismiss();
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

//
//    private void startGpsFromSettings() {
//
//        if (!checkIfGpsIsEnabled()) {
//            Intent gpsOptionsIntent = new Intent(
//                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            startActivity(gpsOptionsIntent);
//        }
//    }

    private boolean checkIfGpsIsEnabled() {
        LocationManager manager;

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkIfGpsIsEnabled()) {
            //createLocationRequests();
            disableUi();
        } else {
            enableUi();
            getLastKnowLocation();
        }
    }

    private void getLastKnowLocation() {
        int HasFineLocationPermission = ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);

        if (HasFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
            return;
        }
        if (checkIfGpsIsEnabled()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Log.d(TAG, "getLastKnowLocation: "+mLocation);
            if (mLastLocation != null) {

                Geocoder geocoder;
                List<Address> addresses = null;
                geocoder = new Geocoder(this, Locale.getDefault());

                try {
//                double longitude = mLastLocation.getLongitude();
//                double latitude = mLastLocation.getLatitude();
                    addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (addresses != null) {
                    //  String address = addresses.get(0).getAddressLine(0);
                    // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    mCity = addresses.get(0).getLocality();
//                            String state = addresses.get(0).getAdminArea();
//                            String country = addresses.get(0).getCountryName();
//                            //  String postalCode = addresses.get(0).getPostalCode();
//                            String knownName = addresses.get(0).getFeatureName(); // O
                    if (mCity.equalsIgnoreCase(AvailabilityInCities.ABBOTTABAD)) {
//                        View view = findViewById(R.id.actvivity_main_frame_layout_fragment_container);
//                        Snackbar.make(view, mCity, Snackbar.LENGTH_LONG).show();
                    } else {
                        View view = findViewById(R.id.actvivity_main_frame_layout_fragment_container);
                        Snackbar.make(view, "Sorry we are not yet available in " + mCity, Snackbar.LENGTH_LONG).show();
                    }


                    Toast.makeText(this, "Current City is " + mCity, Toast.LENGTH_LONG).show();
                    Log.d(TAG, "getLastKnowLocation: " + mCity);
                }
            } else {
                //getLastKnowLocation();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastKnowLocation();
                }
                break;
        }
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        getLastKnowLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: ");
    }
}