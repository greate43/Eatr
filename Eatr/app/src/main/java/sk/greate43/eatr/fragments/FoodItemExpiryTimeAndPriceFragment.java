package sk.greate43.eatr.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Seller;


public class FoodItemExpiryTimeAndPriceFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FoodItemExpiryTimeAndPr";
    private static final String ADD_FOOD_ITEM_FRAGMENTS = "ADD_FOOD_ITEM_FRAGMENTS";
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
    private Seller seller;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;

    public static FoodItemExpiryTimeAndPriceFragment newInstance(Seller seller) {
        Bundle args = new Bundle();
        args.putSerializable(ADD_FOOD_ITEM_FRAGMENTS, seller);
        FoodItemExpiryTimeAndPriceFragment foodItemExpiryTimeAndPriceFragment = new FoodItemExpiryTimeAndPriceFragment();
        foodItemExpiryTimeAndPriceFragment.setArguments(args);
        return foodItemExpiryTimeAndPriceFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seller = (Seller) getArguments().getSerializable(ADD_FOOD_ITEM_FRAGMENTS);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        storageRef = FirebaseStorage.getInstance().getReference();


        return view;
    }

    private void writeSellerData(final String username, final String dishName, final String cuisine, final String ingredientsTags, final String pickUpLocation, Uri imgUri, final long price, final long expiryTime, final long numberOfServings) {

        StorageReference sellerRef = storageRef.child("Photos").child(dishName).child(imgUri.getLastPathSegment());

        sellerRef.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {


                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        String downloadUrl = String.valueOf(taskSnapshot.getDownloadUrl());

                        seller = new Seller();
                        seller.setDishName(dishName);
                        seller.setCuisine(cuisine);
                        seller.setIngredientsTags(ingredientsTags);
                        seller.setPickUpLocation(pickUpLocation);
                        seller.setImageUri(downloadUrl);
                        seller.setTimeStamp(ServerValue.TIMESTAMP);
                        seller.setExpiryTime(expiryTime);
                        seller.setPrice(price);
                        seller.setNumberOfServings(numberOfServings);

                        mDatabaseReference.child("eatr").child(username).child(dishName).setValue(seller);


                    }


                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        seller = new Seller();
                        seller.setDishName(dishName);
                        seller.setCuisine(cuisine);
                        seller.setIngredientsTags(ingredientsTags);
                        seller.setPickUpLocation(pickUpLocation);
                        seller.setImageUri("");
                        seller.setTimeStamp(ServerValue.TIMESTAMP);
                        seller.setExpiryTime(expiryTime);
                        seller.setPrice(price);
                        seller.setNumberOfServings(numberOfServings);
                        mDatabaseReference.child("eatr").child(username).child(dishName).setValue(seller);
                        Log.d(TAG, "onFailure: " + exception.getLocalizedMessage());


                        //  getActivity().finish();

                    }
                });


    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fragment_food_item_expiry_time_and_price_button_1_hour:
                btnExpiryTimeOneHour.setSelected(true);
                btnExpiryTimeFourHour.setSelected(false);
                btnExpiryTimeEightHour.setSelected(false);
                btnExpiryTimeSixteenHour.setSelected(false);
                expiryTime = 1;

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_4_hour:
                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(true);
                btnExpiryTimeEightHour.setSelected(false);
                btnExpiryTimeSixteenHour.setSelected(false);
                expiryTime = 4;

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_8_hour:
                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(false);
                btnExpiryTimeEightHour.setSelected(true);
                btnExpiryTimeSixteenHour.setSelected(false);
                expiryTime = 8;

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_16_hour:
                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(false);
                btnExpiryTimeEightHour.setSelected(false);
                btnExpiryTimeSixteenHour.setSelected(true);
                expiryTime = 16;

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_show_preview:
                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_post_food:
                if (seller != null) {
                    writeSellerData("greate43"
                            , seller.getDishName()
                            , seller.getCuisine()
                            , seller.getIngredientsTags()
                            , seller.getPickUpLocation()
                            , seller.getImage()
                            , Long.parseLong(etPrice.getText().toString())
                            , Long.parseLong(etExpiryTime.getText().toString())
                            , Long.parseLong(etNumberOfServings.getText().toString()));
                }

//                FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(getActivity()));
//
//                Job myJob = dispatcher.newJobBuilder()
//                        .setService(CheckOrderStatusService.class) // the JobService that will be called
//                        .setTag(seller.getDishName())// uniquely identifies the job
//                        .build();
//
//                dispatcher.mustSchedule(myJob);

                getActivity().finish();
                break;
        }

    }
}