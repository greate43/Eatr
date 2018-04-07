package sk.greate43.eatr.activities;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.utils.Constants;

import static sk.greate43.eatr.utils.Constants.REQUEST_FINE_LOCATION_PERMISSION;

public class ListOfAllPostedFoodsMapsActivity extends FragmentActivity implements  GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private static final String TAG = "ListOfAllPostedFoodsMap";
    private GoogleMap mMap;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_all_posted_foods_maps);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.EXPIRY_TIME).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_map);



        mapFragment.getMapAsync(this);
    }

    private void showData(@NotNull DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }



        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {


                collectFood((Map<String, Object>) ds.getValue());
            }
        }
    }
    private void collectFood(Map<String, Object> value) {


        Log.d(TAG, "collectSeller: " + value);
        Food food = new Food();
        food.setPushId((String) value.get(Constants.PUSH_ID));
        food.setDishName((String) value.get(Constants.DISH_NAME));
        food.setCuisine((String) value.get(Constants.CUISINE));
        if (value.get(Constants.EXPIRY_TIME) != null) {
            food.setExpiryTime((long) value.get(Constants.EXPIRY_TIME));
        }
        food.setIngredientsTags(String.valueOf(value.get(Constants.INGREDIENTS_TAGS)));
        food.setImageUri((String) value.get(Constants.IMAGE_URI));
        food.setPrice((long) value.get(Constants.PRICE));
        food.setNumberOfServings((long) value.get(Constants.NO_OF_SERVINGS));
        food.setLatitude((double) value.get(Constants.LATITUDE));
        food.setLongitude((double) value.get(Constants.LONGITUDE));
        food.setPickUpLocation((String) value.get(Constants.PICK_UP_LOCATION));
        food.setCheckIfOrderIsActive((Boolean) value.get(Constants.CHECK_IF_ORDER_IS_ACTIVE));
        food.setCheckIfFoodIsInDraftMode((Boolean) value.get(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE));
        food.setCheckIfOrderIsPurchased((Boolean) value.get(Constants.CHECK_IF_ORDER_IS_PURCHASED));


        if (value.get(Constants.CHECK_IF_ORDERED_IS_BOOKED) != null)
            food.setCheckIfOrderIsBooked((boolean) value.get(Constants.CHECK_IF_ORDERED_IS_BOOKED));

        if (value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS) != null)
            food.setCheckIfOrderIsInProgress((boolean) value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS));


        if (value.get(Constants.TIME_STAMP) != null) {
            food.setTime(value.get(Constants.TIME_STAMP).toString());
        }
        if (value.get(Constants.PURCHASED_BY) != null) {
            food.setPurchasedBy((String) value.get(Constants.PURCHASED_BY));
        }


        if (value.get(Constants.POSTED_BY) != null) {
            food.setPostedBy((String) value.get(Constants.POSTED_BY));
        }
        if (value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED) != null) {
            food.setCheckIfOrderIsCompleted((boolean) value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED));
        }

        if (
                !food.getCheckIfFoodIsInDraftMode()
                        && !food.getCheckIfOrderIsPurchased()
                        && food.getCheckIfOrderIsActive()
                        && !food.getCheckIfOrderIsInProgress()
                        && !food.getCheckIfOrderIsBooked()
                        && !food.getCheckIfOrderIsCompleted()
                        && food.getNumberOfServings() > 0
                        && !food.getPostedBy().equals(user.getUid())
                ) {


          mMap.addMarker(new MarkerOptions().position(new LatLng(food.getLatitude(), food.getLongitude())).title(food.getPickUpLocation()));


        }

    }

    private void setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(this, new String[]
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
                // updateMapUiForBuyer(mMap, currentLocation, new LatLng(food.getLatitude(), food.getLongitude()));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new  LatLng(mLastLocation.getLatitude(), mLastLocation
                        .getLongitude()), 16));
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: ");
        setUpMap();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
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
}
