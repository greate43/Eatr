package sk.greate43.eatr.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

import java.util.Calendar;
import java.util.Map;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.Constants;


public class FoodItemExpiryTimeAndPriceFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FoodItemExpiryTimeAndPr";
    TextInputEditText etPrice;
    TextInputEditText etNumberOfServings;
    TextInputEditText etExpiryTime;
    TextView tvExpiryResult;
    Button btnShowPreview;
    Button btnPostFood;
    Button btnExpiryTimeOneHour;
    Button btnExpiryTimeFourHour;
    Button btnExpiryTimeEightHour;
    Button btnExpiryTimeSixteenHour;
    long expiryTime;
    long numberOfServings;
    StorageReference storageRef;
    private Food food;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ReplaceFragment replaceFragment;

    public static FoodItemExpiryTimeAndPriceFragment newInstance(Food food) {

        FoodItemExpiryTimeAndPriceFragment fragment = new FoodItemExpiryTimeAndPriceFragment();
        Bundle args = new Bundle();
        args.putSerializable(Constants.ARGS_FOOD, food);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            food = (Food) getArguments().getSerializable(Constants.ARGS_FOOD);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_food_item_expiry_time_and_price, container, false);
        etPrice = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_edit_text_price);
        etNumberOfServings = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_edit_text_servings);
        etExpiryTime = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_edit_text_custom_hours);
        tvExpiryResult = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_text_view_expiry_results);
        btnShowPreview = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_button_show_preview);
        btnPostFood = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_button_post_food);
        btnExpiryTimeOneHour = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_button_1_hour);
        btnExpiryTimeFourHour = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_button_4_hour);
        btnExpiryTimeEightHour = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_button_8_hour);
        btnExpiryTimeSixteenHour = view.findViewById(R.id.fragment_food_item_expiry_time_and_price_button_16_hour);


        btnShowPreview.setOnClickListener(this);
        btnExpiryTimeSixteenHour.setOnClickListener(this);
        btnExpiryTimeEightHour.setOnClickListener(this);
        btnExpiryTimeFourHour.setOnClickListener(this);
        btnExpiryTimeOneHour.setOnClickListener(this);
        btnPostFood.setOnClickListener(this);


        if (food != null) {
            etPrice.setText(String.valueOf(food.getPrice()));
            etNumberOfServings.setText(String.valueOf(food.getNumberOfServings()));
            etExpiryTime.setText(String.valueOf(food.getExpiryTime()));
            expiryTime = food.getExpiryTime();
        }

        etExpiryTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    btnExpiryTimeOneHour.setSelected(false);
                    btnExpiryTimeFourHour.setSelected(false);
                    btnExpiryTimeEightHour.setSelected(false);
                    btnExpiryTimeSixteenHour.setSelected(false);

                    try {
                        expiryTime = getTimeHrsFromNow(Integer.parseInt(String.valueOf(s)));

                    } catch (NumberFormatException ignored) {

                    }
                    showExpiryTime(expiryTime);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();
        user = mAuth.getCurrentUser();

        return view;
    }

    private void writeSellerData(final String pushId, final String dishName, final String cuisine, final String ingredientsTags, final String pickUpLocation, Uri imgUri, final long price, final long expiryTime, final String numberOfServings, final double longitude, final double latitude) {

        StorageReference sellerRef = storageRef.child(Constants.PHOTOS).child(user.getUid()).child(dishName).child(imgUri.getLastPathSegment());

        sellerRef.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                        food = new Food();
                        food.setPushId(pushId);
                        food.setDishName(dishName);
                        food.setCuisine(cuisine);
                        food.setIngredientsTags(ingredientsTags);
                        food.setPickUpLocation(pickUpLocation);
                        food.setImageUri(downloadUrl);
                        food.setTimeStamp(ServerValue.TIMESTAMP);
                        food.setExpiryTime(expiryTime);
                        food.setPrice(price);
                        food.setNumberOfServings(Long.parseLong(numberOfServings));
                        food.setCheckIfOrderIsActive(true);
                        food.setCheckIfFoodIsInDraftMode(false);

                        food.setLongitude(longitude);
                        food.setLatitude(latitude);
                        mDatabaseReference.child(Constants.FOOD).child(user.getUid()).child(pushId).setValue(food);


                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        food = new Food();
                        food.setDishName(pushId);
                        food.setCuisine(cuisine);
                        food.setIngredientsTags(ingredientsTags);
                        food.setPickUpLocation(pickUpLocation);
                        food.setImageUri("");
                        food.setTimeStamp(ServerValue.TIMESTAMP);
                        food.setExpiryTime(expiryTime);
                        food.setPrice(price);
                        food.setNumberOfServings(Long.parseLong(numberOfServings));
                        food.setCheckIfOrderIsActive(true);
                        food.setCheckIfFoodIsInDraftMode(false);
                        food.setLongitude(longitude);
                        food.setLatitude(latitude);
                        mDatabaseReference.child(Constants.FOOD).child(user.getUid()).child(dishName).setValue(food);
                        Log.d(TAG, "onFailure: " + exception.getLocalizedMessage());


                    }
                });


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fragment_food_item_expiry_time_and_price_button_1_hour:
                etExpiryTime.setText("");
                btnExpiryTimeOneHour.setSelected(true);
                btnExpiryTimeFourHour.setSelected(false);
                btnExpiryTimeEightHour.setSelected(false);
                btnExpiryTimeSixteenHour.setSelected(false);
                expiryTime = getTimeHrsFromNow(1);

                showExpiryTime(expiryTime);

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_4_hour:
                etExpiryTime.setText("");

                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(true);
                btnExpiryTimeEightHour.setSelected(false);
                btnExpiryTimeSixteenHour.setSelected(false);
                expiryTime = getTimeHrsFromNow(4);
                showExpiryTime(expiryTime);

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_8_hour:
                etExpiryTime.setText("");

                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(false);
                btnExpiryTimeEightHour.setSelected(true);
                btnExpiryTimeSixteenHour.setSelected(false);
                expiryTime = getTimeHrsFromNow(8);
                showExpiryTime(expiryTime);

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_16_hour:
                etExpiryTime.setText("");

                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(false);
                btnExpiryTimeEightHour.setSelected(false);
                btnExpiryTimeSixteenHour.setSelected(true);
                expiryTime = getTimeHrsFromNow(16);
                showExpiryTime(expiryTime);

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_show_preview:

                if (replaceFragment != null) {

                    showPreview(
                            food.getPushId(),
                            food.getDishName(),
                            food.getCuisine(),
                            food.getIngredientsTags(),
                            food.getPickUpLocation(),
                            food.getImageUri(),
                            ServerValue.TIMESTAMP,
                            expiryTime,
                            etPrice.getText().toString(),
                            etNumberOfServings.getText().toString(),
                            food.getLongitude(),
                            food.getLatitude(),
                            food.getImage()
                    );
                    replaceFragment.onFragmentReplaced(ShowPreviewFragment.newInstance(food));


                }
                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_post_food:
                if (food != null) {
                    if (!etExpiryTime.getText().toString().isEmpty()) {
                        expiryTime = Long.parseLong(etExpiryTime.getText().toString());
                    }
                    if (
                            !TextUtils.isEmpty(etNumberOfServings.getText())
                                    && !TextUtils.isEmpty(etPrice.getText())) {
                        writeSellerData(
                                food.getPushId()
                                , food.getDishName()
                                , food.getCuisine()
                                , food.getIngredientsTags()
                                , food.getPickUpLocation()
                                , food.getImage()
                                , Long.parseLong(etPrice.getText().toString())
                                , expiryTime
                                , etNumberOfServings.getText().toString()
                                , food.getLongitude()
                                , food.getLatitude()
                        );
                    }else if(TextUtils.isEmpty(etNumberOfServings.getText())){
                        etNumberOfServings.setError("Number of Servings is Empty ");
                    }else if(TextUtils.isEmpty(etPrice.getText())){
                        etPrice.setError("Price is Empty ");
                    }
                }

//                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity().getApplication()));
//
//                Job myJob = dispatcher.newJobBuilder()
//                        .setService(CheckOrderStatusService.class) // the JobService that will be called
//                        .setTag(food.getDishName())// uniquely identifies the job
//                        .addConstraint(Constraint.ON_ANY_NETWORK)
//                        .build();
//
//                dispatcher.mustSchedule(myJob);

                if (getActivity() != null) {
                    getActivity().finish();
                }
                break;
        }

    }

    private void showPreview(String pushId, String dishName, String cuisine, String ingredientsTags, String pickUpLocation, String imageUri, Map<String, String> timestamp, long expiryTime, String price, String noOfServings, double longitude, double latitude, Uri imgUri) {

        food = new Food();
        food.setPushId(pushId);
        food.setDishName(dishName);
        food.setCuisine(cuisine);
        food.setIngredientsTags(ingredientsTags);
        food.setPickUpLocation(pickUpLocation);
        food.setImageUri(imageUri);
        food.setTimeStamp(timestamp);
        food.setExpiryTime(expiryTime);
        food.setPrice(Long.parseLong(price));
        food.setNumberOfServings(Long.parseLong(noOfServings));
        food.setCheckIfOrderIsActive(true);
        food.setCheckIfFoodIsInDraftMode(false);
        food.setLongitude(longitude);
        food.setLatitude(latitude);
        food.setImage(imgUri);

    }


    private long getTimeHrsFromNow(int time) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, time);
        return calendar.getTime().getTime();

    }

    private void showExpiryTime(long time) {
        tvExpiryResult.setText(String.format("Expires %s", DateUtils
                .getRelativeTimeSpanString(time,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS,
                        0)));
    }

    @Override
    public void onStart() {
        super.onStart();
        database.goOnline();
    }

    @Override
    public void onStop() {
        super.onStop();
        database.goOffline();
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
}
