package sk.greate43.eatr.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.internal.operators.completable.CompletableOnErrorComplete;
import sk.greate43.eatr.R;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.interfaces.ReplaceFragment;
import sk.greate43.eatr.utils.Constants;


public class FoodItemExpiryTimeAndPriceFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FoodItemExpiryTimeAndPr";
    public static final int EXPIRY_TIME_NOT_SET = -1;
    public static final int ONE_HOUR = 1;
    public static final int FOUR_HOURS = 4;
    public static final int EIGHT_HOURS = 8;
    public static final int SIXTEEN_HOURS = 16;
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
    long expiryTime = EXPIRY_TIME_NOT_SET;
    long numberOfServings;
    StorageReference storageRef;
    private Food food;
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ReplaceFragment replaceFragment;
    private ProgressDialog mProgressDialog;
    private long expiryConstantValue;

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
        if (getActivity() != null)
            getActivity().setTitle("Step-2 Food expiry and Price");
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
            expiryConstantValue = food.getExpiryConstantValue();

            switch ((int) expiryConstantValue) {
                case ONE_HOUR:
                    btnExpiryTimeOneHour.setSelected(true);
                    btnExpiryTimeFourHour.setSelected(false);
                    btnExpiryTimeEightHour.setSelected(false);
                    btnExpiryTimeSixteenHour.setSelected(false);

                    break;
                case FOUR_HOURS:
                    btnExpiryTimeOneHour.setSelected(false);
                    btnExpiryTimeFourHour.setSelected(true);
                    btnExpiryTimeEightHour.setSelected(false);
                    btnExpiryTimeSixteenHour.setSelected(false);

                    break;
                case EIGHT_HOURS:
                    btnExpiryTimeOneHour.setSelected(false);
                    btnExpiryTimeFourHour.setSelected(false);
                    btnExpiryTimeEightHour.setSelected(true);
                    btnExpiryTimeSixteenHour.setSelected(false);

                    break;
                case SIXTEEN_HOURS:
                    btnExpiryTimeOneHour.setSelected(false);
                    btnExpiryTimeFourHour.setSelected(false);
                    btnExpiryTimeEightHour.setSelected(false);
                    btnExpiryTimeSixteenHour.setSelected(true);

                    break;

                default:
                    etExpiryTime.setText(String.valueOf(expiryConstantValue));

                    break;
            }
            expiryTime = getTimeHrsFromNow((int) expiryConstantValue);
            showExpiryTime(expiryTime);
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
                        expiryConstantValue = Integer.parseInt(String.valueOf(s));
                    } catch (NumberFormatException ex) {
                        Snackbar.make(view, ex.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
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

    private void writeSellerData(final String pushId, final long price, final long expiryTime, final long numberOfServings, long expiryConstantValue) {
        showProgressDialog();
        mDatabaseReference.child(Constants.FOOD).child(pushId).updateChildren(toMap(pushId, price, numberOfServings, expiryTime, expiryConstantValue));
        hideProgressDialog();


    }

    public Map<String, Object> toMap(String pushId, long price, long numberOfServings, long expiryTime, long expiryConstantValue) {
        HashMap<String, Object> result = new HashMap<>();
        result.put(Constants.PUSH_ID, pushId);
        result.put(Constants.PRICE, price);
        result.put(Constants.NO_OF_SERVINGS, numberOfServings);
        result.put(Constants.EXPIRY_TIME, expiryTime);
        result.put(Constants.CHECK_IF_ORDER_IS_ACTIVE, true);
        result.put(Constants.TIME_STAMP, ServerValue.TIMESTAMP);
        result.put(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE, false);
        result.put(Constants.EXPIRY_CONSTANT_VALUE, expiryConstantValue);
        return result;
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
                expiryTime = getTimeHrsFromNow(ONE_HOUR);
                expiryConstantValue = ONE_HOUR;
                showExpiryTime(expiryTime);

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_4_hour:
                etExpiryTime.setText("");

                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(true);
                btnExpiryTimeEightHour.setSelected(false);
                btnExpiryTimeSixteenHour.setSelected(false);
                expiryTime = getTimeHrsFromNow(FOUR_HOURS);
                expiryConstantValue = FOUR_HOURS;

                showExpiryTime(expiryTime);

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_8_hour:
                etExpiryTime.setText("");

                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(false);
                btnExpiryTimeEightHour.setSelected(true);
                btnExpiryTimeSixteenHour.setSelected(false);
                expiryTime = getTimeHrsFromNow(EIGHT_HOURS);
                expiryConstantValue = EIGHT_HOURS;

                showExpiryTime(expiryTime);

                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_16_hour:
                etExpiryTime.setText("");

                btnExpiryTimeOneHour.setSelected(false);
                btnExpiryTimeFourHour.setSelected(false);
                btnExpiryTimeEightHour.setSelected(false);
                btnExpiryTimeSixteenHour.setSelected(true);
                expiryTime = getTimeHrsFromNow(SIXTEEN_HOURS);
                expiryConstantValue = SIXTEEN_HOURS;

                showExpiryTime(expiryTime);

                break;
//            case R.id.fragment_food_item_expiry_time_and_price_button_show_preview:
//
//                if (replaceFragment != null) {
//
//                    showPreview(
//                            food.getPushId(),
//                            food.getDishName(),
//                            food.getCuisine(),
//                            food.getIngredientsTags(),
//                            food.getPickUpLocation(),
//                            food.getImageUri(),
//                            ServerValue.TIMESTAMP,
//                            expiryTime,
//                            etPrice.getText().toString(),
//                            etNumberOfServings.getText().toString(),
//                            food.getLongitude(),
//                            food.getLatitude(),
//                            food.getImage()
//                    );
//                    replaceFragment.onFragmentReplaced(ShowPreviewFragment.newInstance(food));
//
//
//                }
//                break;
            case R.id.fragment_food_item_expiry_time_and_price_button_post_food:
                if (food != null) {
//                    if (!etExpiryTime.getText().toString().isEmpty()) {
//                        expiryTime = Long.parseLong(etExpiryTime.getText().toString());
//                    }

                    if (
                            !TextUtils.isEmpty(etNumberOfServings.getText())
                                    && !TextUtils.isEmpty(etPrice.getText())
                                    && expiryTime != EXPIRY_TIME_NOT_SET
                                    && !TextUtils.equals(etNumberOfServings.getText(), "0")
                                    && !TextUtils.equals(etExpiryTime.getText(), "0")
                            ) {
                        writeSellerData(
                                food.getPushId()
                                , Long.parseLong(etPrice.getText().toString())
                                , expiryTime
                                , Long.parseLong(etNumberOfServings.getText().toString())
                                , expiryConstantValue
                        );

                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    } else if (TextUtils.isEmpty(etPrice.getText())) {
                        etPrice.setError("Price is Empty ");
                    } else if (TextUtils.isEmpty(etNumberOfServings.getText())) {
                        etNumberOfServings.setError("Number of Servings is Empty ");
                    } else if (TextUtils.equals(etNumberOfServings.getText(), "0")) {
                        etNumberOfServings.setError("No of Servings  Cant be 0");

                    } else if (expiryTime == EXPIRY_TIME_NOT_SET) {
                        etExpiryTime.setError("Expiry time Should be Set");
                    } else if (TextUtils.equals(etExpiryTime.getText(), "0")) {
                        etExpiryTime.setError("Expiry time Should be Set");

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
        calendar.add(Calendar.HOUR, time);
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
    }

    @Override
    public void onStop() {
        super.onStop();
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

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
