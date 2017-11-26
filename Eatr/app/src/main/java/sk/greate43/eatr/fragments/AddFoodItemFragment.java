package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.originqiu.library.EditTag;
import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Seller;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddFoodItemFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final int CAMERA_RESULT = 111;
    private static final int GALLERY_RESULT = 222;

    private static final int REQUEST_CAMERA_AND_WRITE_PERMISSION = 1111;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 2222;

    private static final String TAG = "AddFoodItemFragment";
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 4444;

    int PLACE_PICKER_REQUEST = 1;

    ImageView imgChooseImage;
    GoogleApiClient mGoogleApiClient;
    StorageReference storageRef;
    private Location mLastLocation;
    private TextInputEditText etDishName;
    private TextInputEditText etCuisine;
    private EditTag etIncidentsTags;
    private TextInputLayout tilIncidentsTags;
    private TextInputEditText etPickLocation;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private Uri imgUri;
    private ProgressDialog dialogUploadingImage;

    //  private String mCurrentPhotoPath;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_food_item, container, false);

        if (!checkIfGpsIsEnabled()) {
            askUserToStartGpsDialog();
        }

        dialogUploadingImage = new ProgressDialog(getActivity());
        dialogUploadingImage.setCanceledOnTouchOutside(false);

        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();

        storageRef = FirebaseStorage.getInstance().getReference();

        etDishName = view.findViewById(R.id.fragment_add_food_item_edit_text_dish_name);
        etCuisine = view.findViewById(R.id.fragment_add_food_item_edit_text_cuisine);
        etIncidentsTags = view.findViewById(R.id.fragment_add_food_item_edit_tag_ingredient_tag);
        etPickLocation = view.findViewById(R.id.fragment_add_food_item_edit_text_pick_location);
        tilIncidentsTags=view.findViewById(R.id.fragment_add_food_item_til_ingredient_tag);
        FloatingActionButton btnGetLocation = view.findViewById(R.id.fragment_add_food_item_button_get_location);
        Button btnShareFood = view.findViewById(R.id.fragment_add_food_item_button_share_food);

        btnGetLocation.setOnClickListener(this);
        btnShareFood.setOnClickListener(this);

        imgChooseImage = view.findViewById(R.id.fragment_add_food_item_image_view_choose_image);
        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectPictureFromGalleryOrCameraDialog();

            }
        });


// Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        return view;
    }

    private void selectPictureFromGalleryOrCameraDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    private void askUserToStartGpsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Would You Like To Turn On The Gps?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                startGpsFromSettings();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.No, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                showToast("App Might Not be Fully Functional if Gps Is Off  ");
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void choosePhotoFromGallery() {
        int HasReadPermission = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
        if (HasReadPermission != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            return;
        }

        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY_RESULT);

    }

    private void takePhotoFromCamera() {

        int HasCameraPermission = ContextCompat.checkSelfPermission(getActivity(), CAMERA);
        int HasWriteExternalStoragePermPermission = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);

        if (HasCameraPermission != PackageManager.PERMISSION_GRANTED || HasWriteExternalStoragePermPermission != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(getActivity(), new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_AND_WRITE_PERMISSION);
            return;
        }


        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_RESULT);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_RESULT) {
            if (data != null) {
                imgUri = data.getData();
                imgChooseImage.setImageURI(Uri.parse(String.valueOf(imgUri)));
                imgChooseImage.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Log.d(TAG, "onActivityResult: " + imgUri.getLastPathSegment());
            }

        } else if (requestCode == CAMERA_RESULT) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            String pathOfBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);

            imgUri = Uri.parse(pathOfBmp);

            imgChooseImage.setImageURI(Uri.parse(String.valueOf(imgUri)));
            imgChooseImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Log.d(TAG, "onActivityResult: " + imgUri);

        }

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                Place place = PlacePicker.getPlace(data, getActivity());
                etPickLocation.setText(place.getAddress());
                //  String toastMsg = String.format("Place: %s", place.getAddress());
                //showToast(toastMsg);
            }
        }


    }


    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }


    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int HasFineLocationPermission = ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);

        if (HasFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0);
                // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                //  String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // O


                etPickLocation.setText(knownName + " " + city + " " + state + " " + country);
            }
        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        assert connectivityManager != null;
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

    private void writeSellerData(final String username, final String dishName, final String cuisine, final String ingredientsTags, final String pickUpLocation, Uri imgUri) {
        dialogUploadingImage.setMessage("Uploading Image........");
        dialogUploadingImage.show();
        StorageReference sellerRef = storageRef.child("Photos").child(dishName).child(imgUri.getLastPathSegment());

        sellerRef.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                        Seller seller = new Seller();
                        seller.setDishName(dishName);
                        seller.setCuisine(cuisine);
                        seller.setIngredientsTags(ingredientsTags);
                        seller.setPickUpLocation(pickUpLocation);
                        seller.setImageUri(downloadUrl);
                        seller.setTimeStamp(ServerValue.TIMESTAMP);

                        mDatabaseReference.child("eatr").child(username).child(dishName).setValue(seller);
                        if (dialogUploadingImage.isShowing()) {
                            dialogUploadingImage.dismiss();
                        }
                        getActivity().finish();

                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Seller seller = new Seller();
                        seller.setDishName(dishName);
                        seller.setCuisine(cuisine);
                        seller.setIngredientsTags(ingredientsTags);
                        seller.setPickUpLocation(pickUpLocation);
                        seller.setImageUri("");
                        seller.setTimeStamp(ServerValue.TIMESTAMP);

                        mDatabaseReference.child("eatr").child(username).child(dishName).setValue(seller);
                        Log.d(TAG, "onFailure: " + exception.getLocalizedMessage());
                        if (dialogUploadingImage.isShowing()) {
                            dialogUploadingImage.dismiss();
                        }
                        getActivity().finish();

                    }
                });


    }

//    private File createImageFile() throws IOException {
//        // Create an image file name
//
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
//        String imageFileName = "PNG_" + timeStamp + "_";
//        File storageDir;
//        String state = Environment.getExternalStorageState();
//        Log.d(TAG, "createImageFile: state" + state);
//        // Make sure it's available
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            // We can read and write the media
//            storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        } else {
//            // Load another directory, probably local memory
//            storageDir = getFilesDir();
//        }
//
//
//
//
//        Log.d(TAG, "createImageFile:mystring " + mystring);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".png",         /* suffix */
//                storageDir      /* directory */
//        );
//        Log.d(TAG, "createImageFile: image " + image);
//
//        // Save a file: path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = image.getAbsolutePath();
//        Log.d(TAG, "createImageFile: mCurrentPhotoPath " + mCurrentPhotoPath);
//        return image;
//    }

    private void pickPlace() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        if (!checkIfGpsIsEnabled()) {
            askUserToStartGpsDialog();
        } else {
            try {
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }


    private void startGpsFromSettings() {

        if (!checkIfGpsIsEnabled()) {
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
    }

    private boolean checkIfGpsIsEnabled() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        assert manager != null;
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_add_food_item_button_get_location:
                pickPlace();
                break;
            case R.id.fragment_add_food_item_button_share_food:

                if (
                        !TextUtils.isEmpty(etDishName.getText().toString())
                                && !TextUtils.isEmpty(etCuisine.getText().toString())
                                && !etIncidentsTags.getTagList().isEmpty()
                                && !TextUtils.isEmpty(etPickLocation.getText().toString())
                                && imgUri != null
                        ) {
                    writeSellerData("greate43"
                            , etDishName.getText().toString()
                            , etCuisine.getText().toString()
                            , etIncidentsTags.getTagList().toString()
                            , etPickLocation.getText().toString()
                            , imgUri
                    );
                } else if (TextUtils.isEmpty(etDishName.getText().toString())) {
                    etDishName.setError("Dish Name is Empty  ");
                } else if (TextUtils.isEmpty(etCuisine.getText().toString())) {
                    etDishName.setError("Cuisine Name is Empty  ");
                } else if (TextUtils.isEmpty(etCuisine.getText().toString())) {
                    etCuisine.setError("Dish Name is Empty  ");
                } else if (etIncidentsTags.getTagList().isEmpty()) {

                    tilIncidentsTags.setError("There Should be at Lest 1 Tag");
                } else if (TextUtils.isEmpty(etPickLocation.getText().toString())) {
                    etDishName.setError("Pick Up Name is Empty  ");
                }

                break;


        }
    }

}