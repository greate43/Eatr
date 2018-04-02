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
import android.support.design.widget.Snackbar;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.originqiu.library.EditTag;
import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.Constants;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static sk.greate43.eatr.utils.Constants.CAMERA_RESULT;
import static sk.greate43.eatr.utils.Constants.GALLERY_RESULT;
import static sk.greate43.eatr.utils.Constants.PLACE_PICKER_REQUEST;
import static sk.greate43.eatr.utils.Constants.REQUEST_CAMERA_AND_WRITE_PERMISSION;
import static sk.greate43.eatr.utils.Constants.REQUEST_FINE_LOCATION_PERMISSION;
import static sk.greate43.eatr.utils.Constants.REQUEST_READ_EXTERNAL_STORAGE_PERMISSION;

public class AddFoodItemFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private static final String TAG = "AddFoodItemFragment";

    ImageView imgChooseImage;
    GoogleApiClient mGoogleApiClient;
    StorageReference storageRef;
    View view;
    private Location mLastLocation;
    private TextInputEditText etDishName;
    private TextInputEditText etCuisine;
    private EditTag etIncidentsTags;
    private TextInputLayout tilIncidentsTags;
    private TextInputEditText etPickLocation;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;
    private Uri imgUri;
    private ProgressDialog dialogUploadingImage;
    private ReplaceFragment replaceFragment;
    private Food food;
    private double longitude;
    private double latitude;
    private String pushId;
    private long price = 0;
    private long numberOfServings = 0;
    private long expiryTime = 0;
    private boolean checkIfOrderIsActive = false;
    private boolean checkIfFoodIsInDraftMode = true;

    @NonNull
    public static AddFoodItemFragment newInstance() {
        return new AddFoodItemFragment();
    }

    public static AddFoodItemFragment newInstance(Food food) {
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARGS_FOOD, food);
        AddFoodItemFragment addFoodItemFragment = new AddFoodItemFragment();
        addFoodItemFragment.setArguments(args);
        return addFoodItemFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null)
            getActivity().setTitle("Add Food Item Fragment");

        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable(Constants.ARGS_FOOD);
        }

    }

    //  private String mCurrentPhotoPath;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_food_item, container, false);

        if (!checkIfGpsIsEnabled()) {
            askUserToStartGpsDialog();
        }

        dialogUploadingImage = new ProgressDialog(getActivity());
        dialogUploadingImage.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();


        storageRef = FirebaseStorage.getInstance().getReference();


        //Log.d(TAG, "onCreateView: " + user.getUid());

        etDishName = view.findViewById(R.id.fragment_add_food_item_edit_text_dish_name);
        etCuisine = view.findViewById(R.id.fragment_add_food_item_edit_text_cuisine);
        etIncidentsTags = view.findViewById(R.id.fragment_add_food_item_edit_tag_ingredient_tag);
        etPickLocation = view.findViewById(R.id.fragment_add_food_item_edit_text_pick_location);
        tilIncidentsTags = view.findViewById(R.id.fragment_add_food_item_til_ingredient_tag);
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
            if (getActivity() != null)
                mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
        }


        if (food != null) {
            if (getActivity() != null && food.getImageUri() != null && !food.getImageUri().isEmpty()) {
                Picasso.with(getActivity())
                        .load(food.getImageUri())
                        .fit()
                        .centerCrop()
                        .into(imgChooseImage, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.d(TAG, "onSuccess: ");
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
            String tags = food.getIngredientsTags();
            String singleTag = "";
            if (tags.contains("[")) {
                singleTag = tags.replace("[", "");
            }
            if (singleTag.contains("]")) {

                Log.d(TAG, "onCreateView: tags 1 " + singleTag);

                singleTag = singleTag.replace("]", "");
            }


            etIncidentsTags.addTag(String.valueOf(singleTag));
            pushId = String.valueOf(food.getPushId());
            etDishName.setText(food.getDishName());
            etCuisine.setText(food.getCuisine());
            etPickLocation.setText(food.getPickUpLocation());
            price = food.getPrice();


            numberOfServings = food.getNumberOfServings();
            expiryTime = food.getExpiryTime();
            checkIfFoodIsInDraftMode = food.getCheckIfFoodIsInDraftMode();
            checkIfOrderIsActive = food.getCheckIfOrderIsActive();

        } else {
            pushId = String.valueOf(mDatabaseReference.push().getKey());
            Log.d(TAG, "onCreateView:push id " + pushId);
        }

        return view;
    }

    private void selectPictureFromGalleryOrCameraDialog() {
        if (getActivity() != null) {
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
    }

    private void askUserToStartGpsDialog() {
        if (getActivity() != null) {
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
    }

    public void choosePhotoFromGallery() {
        if (getActivity() != null) {
            int HasReadPermission = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);
            if (HasReadPermission != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
                return;
            }

            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, GALLERY_RESULT);
        }
    }

    private void takePhotoFromCamera() {
        if (getActivity() != null) {
            int HasCameraPermission = ContextCompat.checkSelfPermission(getActivity(), CAMERA);
            int HasWriteExternalStoragePermPermission = ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE);

            if (HasCameraPermission != PackageManager.PERMISSION_GRANTED || HasWriteExternalStoragePermPermission != PackageManager.PERMISSION_GRANTED) {


                requestPermissions(new String[]{CAMERA, WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_AND_WRITE_PERMISSION);
                return;
            }


            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_RESULT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY_RESULT) {
            if (resultCode == RESULT_OK && data != null) {
                imgUri = data.getData();

                setImage(imgUri);

                Log.d(TAG, "onActivityResult: 1 " + imgUri.getLastPathSegment());
            }

        } else if (requestCode == CAMERA_RESULT) {
            String pathOfBmp = null;
            if (resultCode == RESULT_OK && data != null) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                if (getActivity() != null) {
                    pathOfBmp = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "title", null);
                }
                imgUri = Uri.parse(pathOfBmp);

                setImage(imgUri);
                Log.d(TAG, "onActivityResult:  2 " + imgUri);
            }

        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK && data != null) {
                if (getActivity() != null) {
                    Place place = PlacePicker.getPlace(getActivity(), data);
                    longitude = place.getLatLng().longitude;
                    latitude = place.getLatLng().latitude;
                    etPickLocation.setText(place.getAddress());
                }
                //  String toastMsg = String.format("Place: %s", place.getAddress());
                //showToast(toastMsg);
            }
        }


    }

    public void setImage(Uri uri) {
        if (uri != null)
            Picasso.with(getActivity())
                    .load(uri)
                    .fit()
                    .centerCrop()
                    .into(imgChooseImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: ");
                        }

                        @Override
                        public void onError() {

                        }
                    });
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

        getLocation();
    }

    public void getLocation() {
        if (getActivity() != null) {
            int HasFineLocationPermission = ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);

            if (HasFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION_PERMISSION);
                return;
            }
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());

            try {
                longitude = mLastLocation.getLongitude();
                latitude = mLastLocation.getLatitude();
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


                etPickLocation.setText(String.format("%s %s %s %s", knownName, city, state, country));
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        assert connectivityManager != null;
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    private void writeSellerData(final String pushId, final String dishName, final String cuisine, final String ingredientsTags, final String pickUpLocation, final Uri imgUri, final double longitude, final double latitude, final long price, final long numberOfServings, final long expiryTime, final boolean checkIfFoodIsInDraftMode, final boolean checkIfOrderIsActive) {

        dialogUploadingImage.setMessage("Uploading Image........");
        dialogUploadingImage.show();
        // Get the data from an ImageView as bytes
        imgChooseImage.setDrawingCacheEnabled(true);
        imgChooseImage.buildDrawingCache();
        Bitmap bitmap = imgChooseImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = storageRef.child(Constants.PHOTOS).child(user.getUid()).child(pushId).putBytes(data);

        //     storageRef.child(Constants.PHOTOS).child(user.getUid()).child(dishName).child(imgUri.getLastPathSegment());


        //  storageRef.putFile(imgUri)
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads

                food = new Food();
                food.setPushId(pushId);
                food.setDishName(dishName);
                food.setCuisine(cuisine);
                food.setIngredientsTags(ingredientsTags);
                food.setPickUpLocation(pickUpLocation);
                food.setImageUri("");
                food.setImage(imgUri);
                food.setLongitude(longitude);
                food.setLatitude(latitude);
                food.setCheckIfFoodIsInDraftMode(checkIfFoodIsInDraftMode);
                food.setTimeStamp(ServerValue.TIMESTAMP);
                food.setCheckIfOrderIsActive(checkIfOrderIsActive);
                food.setPrice(price);
                food.setNumberOfServings(numberOfServings);
                food.setExpiryTime(expiryTime);
                food.setCheckIfOrderIsPurchased(false);
                food.setPostedBy(user.getUid());
                food.setPurchasedBy("");
                food.setCheckIfOrderIsInProgress(false);
                food.setCheckIfOrderIsAccepted(false);
                food.setCheckIfOrderIsBooked(false);
                food.setCheckIfOrderIsCompleted(false);
                food.setCheckIfMapShouldBeClosed(false);

                mDatabaseReference.child(Constants.FOOD).child(pushId).setValue(food);
                Log.d(TAG, "onFailure: " + exception.getLocalizedMessage());
                if (dialogUploadingImage.isShowing()) {
                    dialogUploadingImage.dismiss();
                }
                if (replaceFragment != null) {


                    replaceFragment.onFragmentReplaced(FoodItemExpiryTimeAndPriceFragment.newInstance(food));
                }

                //  getActivity().finish();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                // Get a URL to the uploaded content

                food = new Food();
                food.setPushId(pushId);
                food.setDishName(dishName);
                food.setCuisine(cuisine);
                food.setIngredientsTags(ingredientsTags);
                food.setPickUpLocation(pickUpLocation);
                food.setImageUri(String.valueOf(downloadUrl));
                food.setImage(imgUri);
                food.setLongitude(longitude);
                food.setLatitude(latitude);
                food.setCheckIfFoodIsInDraftMode(checkIfFoodIsInDraftMode);
                food.setTimeStamp(ServerValue.TIMESTAMP);
                food.setCheckIfOrderIsActive(checkIfOrderIsActive);
                food.setPrice(price);
                food.setNumberOfServings(numberOfServings);
                food.setExpiryTime(expiryTime);
                food.setCheckIfOrderIsPurchased(false);
                food.setPostedBy(user.getUid());
                food.setPurchasedBy("");
                food.setCheckIfOrderIsInProgress(false);
                food.setCheckIfOrderIsAccepted(false);
                food.setCheckIfOrderIsBooked(false);
                food.setCheckIfOrderIsCompleted(false);
                food.setCheckIfMapShouldBeClosed(false);


                mDatabaseReference.child(Constants.FOOD).child(pushId).setValue(food);
                if (dialogUploadingImage.isShowing()) {
                    dialogUploadingImage.dismiss();
                }

                if (replaceFragment != null) {


                    replaceFragment.onFragmentReplaced(FoodItemExpiryTimeAndPriceFragment.newInstance(food));
                }


            }


        });


    }

    private void pickPlace() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        if (!checkIfGpsIsEnabled()) {
            askUserToStartGpsDialog();
        } else {
            try {
                if (getActivity() != null) {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                }
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
        LocationManager manager = null;
        if (getActivity() != null) {

            manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }
        return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    @Override
    public void onClick(View v) {
        view = v;
        switch (v.getId()) {
            case R.id.fragment_add_food_item_button_get_location:
                pickPlace();
                break;
            case R.id.fragment_add_food_item_button_share_food:


                if (food != null && food.getImageUri() != null && !food.getImageUri().isEmpty()) {
                    imgUri = Uri.parse(food.getImageUri());
                }


                if (
                        !TextUtils.isEmpty(etDishName.getText().toString())
                                && !TextUtils.isEmpty(etCuisine.getText().toString())
                                && !etIncidentsTags.getTagList().isEmpty()
                                && !TextUtils.isEmpty(etPickLocation.getText().toString())
                                && imgUri != null
                        ) {


                    writeSellerData(
                            pushId
                            , etDishName.getText().toString()
                            , etCuisine.getText().toString()
                            , etIncidentsTags.getTagList().toString()
                            , etPickLocation.getText().toString()
                            , imgUri
                            , longitude
                            , latitude
                            , price
                            , numberOfServings
                            , expiryTime
                            , checkIfFoodIsInDraftMode
                            , checkIfOrderIsActive

                    );


                } else if (imgUri == null) {
                    Snackbar.make(view, "Please Select the image ", Snackbar.LENGTH_SHORT).show();

                } else if (TextUtils.isEmpty(etDishName.getText().toString())) {
                    etDishName.setError("Dish Name is Empty  ");
                } else if (TextUtils.isEmpty(etCuisine.getText().toString())) {
                    etCuisine.setError("Cuisine Name is Empty  ");
                } else if (TextUtils.isEmpty(etPickLocation.getText().toString())) {
                    etPickLocation.setError("Pick Up Name is Empty  ");
                } else if (etIncidentsTags.getTagList().isEmpty()) {

                    tilIncidentsTags.setError("There Should be at Lest 1 Tag");
                }

                break;


        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        if (context instanceof ReplaceFragment) {
            replaceFragment = (ReplaceFragment) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ReplaceFragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        replaceFragment = null;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: 1");
                    choosePhotoFromGallery();
                }
                break;
            case REQUEST_CAMERA_AND_WRITE_PERMISSION:

                if (grantResults.length > 0) {
                    Log.d(TAG, "onRequestPermissionsResult: 0");
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    Log.d(TAG, "onRequestPermissionsResult: 2 ");
                    if (cameraPermission && readExternalStorage) {
                        takePhotoFromCamera();
                    }
                }

                break;
            case REQUEST_FINE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                break;

        }
    }

}
