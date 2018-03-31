package sk.greate43.eatr.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.TravelMode;

import org.jetbrains.annotations.Contract;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.entities.LiveLocationUpdate;
import sk.greate43.eatr.utils.Constants;

import static android.app.Activity.RESULT_OK;
import static sk.greate43.eatr.utils.Constants.REQUEST_FINE_LOCATION_PERMISSION;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, LocationListener {
    private static final String TAG = "MapFragment";
    private static final int overview = 0;
    LiveLocationUpdate liveLocationUpdate;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase database;
    FirebaseStorage mStorage;
    StorageReference storageRef;
    Marker sellerMaker;
    Marker buyerMarker;
    Polyline line;
    private GoogleMap mMap;
    private Location mLastLocation;
    private boolean mLocationUpdateState;
    private LocationRequest mLocationRequest;
    private Button btnOrder;
    private Food food;
    private GoogleApiClient mGoogleApiClient;
    private int REQUEST_CHECK_SETTINGS = 2;
    private String userType;
    private String price;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(Food food, String userType) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_TYPE, userType);
        args.putSerializable(Constants.ARGS_FOOD, food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userType = getArguments().getString(Constants.USER_TYPE);
            food = (Food) getArguments().getSerializable(Constants.ARGS_FOOD);

            switch (userType) {
                case Constants.TYPE_SELLER:
                    liveLocationUpdate = new LiveLocationUpdate();
                    break;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        btnOrder = view.findViewById(R.id.fragment_map_btn_end_order);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = mStorage.getReference();
        switch (userType) {
            case Constants.TYPE_BUYER:
                if (mGoogleApiClient == null) {
                    if (getActivity() != null)
                        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
                }


                createLocationRequest();
                break;
            case Constants.TYPE_SELLER:
                mDatabaseReference.child(Constants.LIVE_LOCATION_UPDATE).orderByChild(Constants.SELLER_ID).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        showData(dataSnapshot);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                    }
                });


                break;


        }

        switch (userType) {
            case Constants.TYPE_BUYER:
                btnOrder.setVisibility(View.GONE);
                break;
            case Constants.TYPE_SELLER:
                btnOrder.setBackgroundResource(R.color.red);
                btnOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAlertDialog();
                    }
                });
                break;

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);

        return view;
    }

    private void showAlertDialog() {
        if (getActivity() != null) {

            // Set up the input
            final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Enter The amount you were paid");
            builder.setPositiveButton("Complete Order", null);
            builder.setNegativeButton("cancel", null);
            builder.setView(input);


            final AlertDialog mAlertDialog = builder.create();
            mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialog) {

                    Button b = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
                            if (!TextUtils.isEmpty(input.getText())) {
                                price = input.getText().toString();

                                
                                mAlertDialog.dismiss();

                            } else {
                                input.setError("It cant be empty");

                            }
                        }
                    });
                }
            });
            mAlertDialog.show();
        }
    }

    private void showData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }


        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {
                collectUpdateUserLocation((Map<String, Object>) ds.getValue());
            }
        }


    }

    private void collectUpdateUserLocation(Map<String, Object> value) {
        Log.d(TAG, "collectUpdateUserLocation: " + value);

        liveLocationUpdate.setBuyerId(String.valueOf(value.get(Constants.BUYER_ID)));
        liveLocationUpdate.setSellerId(String.valueOf(value.get(Constants.SELLER_ID)));
        liveLocationUpdate.setOrderID(String.valueOf(value.get(Constants.ORDER_ID)));
        liveLocationUpdate.setLatitude((double) value.get(Constants.LATITUDE));
        liveLocationUpdate.setLongitude((double) value.get(Constants.LONGITUDE));

        if (mMap != null) {
            updateMapUiForSeller(mMap, new LatLng(food.getLatitude(), food.getLongitude()), new LatLng(liveLocationUpdate.getLatitude(), liveLocationUpdate.getLongitude()));
        }
    }

    private void updateMapUiForSeller(GoogleMap googleMap, LatLng origin, LatLng destination) {
        DirectionsResult results;
        results = getDirectionsDetails(origin, destination, TravelMode.DRIVING);
        if (results != null) {
            setUpSellerMaker(results, googleMap);
            positionSellerCamera(results.routes[overview], googleMap);
        }
    }

    private void positionSellerCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 16));
    }

    private void setUpSellerMaker(DirectionsResult results, GoogleMap mMap) {
        if (sellerMaker == null) {
            sellerMaker = mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress));
        }


        if (buyerMarker == null) {
            buyerMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_taxi_black_24dp)));
        } else {

            animateMarker(buyerMarker, buyerMarker.getPosition(), new LatLng(liveLocationUpdate.getLatitude(), liveLocationUpdate.getLongitude()), false, results);

            // myPosition.setPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }
    }

    @Nullable
    private DirectionsResult getDirectionsDetails(LatLng origin, LatLng destination, TravelMode mode) {
        Log.d(TAG, "getDirectionsDetails: origin " + origin.latitude);
        DateTime now = new DateTime();
        try {
            return DirectionsApi.newRequest(getGeoContext())
                    .mode(mode)
                    .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                    .departureTime(now)
                    .await();
        } catch (ApiException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupGoogleMapScreenSettings(googleMap);
        //    updateMapUiForBuyer(googleMap, new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        switch (userType) {
            case Constants.TYPE_BUYER:

                break;
            case Constants.TYPE_SELLER:

                break;


        }
    }

    private void updateMapUiForBuyer(GoogleMap googleMap, LatLng origin, LatLng destination) {
        mDatabaseReference.child(Constants.LIVE_LOCATION_UPDATE).child(food.getPushId()).updateChildren(updateLocation(origin));
        DirectionsResult results;
        results = getDirectionsDetails(origin, destination, TravelMode.DRIVING);
        if (results != null) {
            addBuyerPolyline(results, googleMap);
            positionBuyerCamera(results.routes[overview], googleMap);
            addBuyerMarkersToMap(results, googleMap);
        }
    }

    private Map<String, Object> updateLocation(LatLng origin) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.ORDER_ID, food.getPushId());
        result.put(Constants.SELLER_ID, food.getPostedBy());
        Log.d(TAG, "updateLocation: " + food.getPostedBy());
        result.put(Constants.BUYER_ID, food.getPurchasedBy());
        result.put(Constants.LATITUDE, origin.latitude);
        result.put(Constants.LONGITUDE, origin.longitude);
        return result;
    }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(false);
        mMap.setTrafficEnabled(false);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    private void addBuyerMarkersToMap(DirectionsResult results, GoogleMap mMap) {
//        if (myPosition == null) {
//            myPosition = mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].startLocation.lat, results.routes[overview].legs[overview].startLocation.lng)).title(results.routes[overview].legs[overview].startAddress).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_local_taxi_black_24dp)));
//        } else {
//            animateMarker(myPosition, myPosition.getPosition(), new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), false);
//
//            // myPosition.setPosition(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()));
//        }
        // animateMarker(marker,marker.getPosition(),new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),false);
        if (buyerMarker == null) {
            buyerMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[overview].legs[overview].endLocation.lat, results.routes[overview].legs[overview].endLocation.lng)).title(results.routes[overview].legs[overview].startAddress).snippet(getEndLocationTitle(results)));
        }
    }

    public void animateMarker(final Marker marker, final LatLng startPosition, final LatLng toPosition,
                              final boolean hideMarker, final DirectionsResult results) {


        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();

        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startPosition.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startPosition.latitude;

                marker.setPosition(new LatLng(lat, lng));
                marker.setTitle(results.routes[overview].legs[overview].startAddress);
                marker.setSnippet(getEndLocationTitle(results));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private void positionBuyerCamera(DirectionsRoute route, GoogleMap mMap) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(route.legs[overview].startLocation.lat, route.legs[overview].startLocation.lng), 16));
    }

    private void addBuyerPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[overview].overviewPolyline.getEncodedPath());
        if (line == null) {
            line = mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
        } else {
            line.setPoints(decodedPath);
        }
    }

    @NonNull
    @Contract(pure = true)
    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[overview].legs[overview].duration.humanReadable + " Distance :" + results.routes[overview].legs[overview].distance.humanReadable;
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext
                .setQueryRateLimit(6)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(2, TimeUnit.SECONDS)
                .setReadTimeout(2, TimeUnit.SECONDS)
                .setWriteTimeout(2, TimeUnit.SECONDS);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setUpMap();
        if (mLocationUpdateState) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        Log.d(TAG, "onLocationChanged: " + location.getLatitude());
        if (mLastLocation != null) {

            if (food != null) {
                updateMapUiForBuyer(mMap, new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(food.getLatitude(), food.getLongitude()));
            }


            calculateDistanceBetweenTwoPointsOnBackgroundThread();


        }
    }


    private void calculateDistanceBetweenTwoPointsOnBackgroundThread() {
        Observable<Boolean> observable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                emitter.onNext(calculateDistanceBetweenTwoPoints());
            }
        });

        Observer<Boolean> observer = new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean isUserNearMe) {
                if (isUserNearMe) {
                    Toast.makeText(getActivity(), "You have almost reached your location", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };


        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    private boolean calculateDistanceBetweenTwoPoints() {
        float[] results = new float[1];
        if (food != null)
            Location.distanceBetween(mLastLocation.getLatitude(), mLastLocation.getLongitude(), food.getLatitude(), food.getLongitude(), results);

        Log.d(TAG, "onLocationChanged: p0 " + results[0]);
        return results[0] < 50;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 2
        switch (userType) {
            case Constants.TYPE_BUYER:
                mGoogleApiClient.connect();
                break;
            case Constants.TYPE_SELLER:

                break;


        }


    }

    @Override
    public void onStop() {
        super.onStop();

        switch (userType) {
            case Constants.TYPE_BUYER:
                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
                break;
            case Constants.TYPE_SELLER:

                break;


        }


    }

    private void setUpMap() {
        if (getActivity() != null)
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]
                        {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
                return;
            }

        mMap.setMyLocationEnabled(true);

        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (locationAvailability != null && locationAvailability.isLocationAvailable()) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng currentLocation = new LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude());
                Log.d(TAG, "setUpMap: " + food);
                if (food != null) {
                    Log.d(TAG, "onNext: lat " + food.getLatitude() + " lng" + food.getLongitude());
                    updateMapUiForBuyer(mMap, currentLocation, new LatLng(food.getLatitude(), food.getLongitude()));
                }
            }
        }
    }

    protected void startLocationUpdates() {
        if (getActivity() != null)
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
                return;
            }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest,
                this);
    }

    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        // 2
        mLocationRequest.setInterval(10000);
        // 3
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        mLocationUpdateState = true;
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mLocationUpdateState = true;
                startLocationUpdates();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: ");
                    setUpMap();

                }
                break;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        switch (userType) {
            case Constants.TYPE_BUYER:
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

                break;
            case Constants.TYPE_SELLER:

                break;


        }
    }

    @Override
    public void onResume() {
        super.onResume();

        switch (userType) {
            case Constants.TYPE_BUYER:
                if (mGoogleApiClient.isConnected() && !mLocationUpdateState) {
                    startLocationUpdates();
                }
                break;
            case Constants.TYPE_SELLER:

                break;


        }


    }


}
