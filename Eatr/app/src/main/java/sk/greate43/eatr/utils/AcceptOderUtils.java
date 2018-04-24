package sk.greate43.eatr.utils;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import sk.greate43.eatr.activities.SellerActivity;
import sk.greate43.eatr.entities.Food;
import sk.greate43.eatr.entities.Notification;
import sk.greate43.eatr.entities.Profile;
import sk.greate43.eatr.entities.Review;
import sk.greate43.eatr.fragmentDialogs.AcceptOrderDialogFragment;

public class AcceptOderUtils {
    private FirebaseDatabase database;
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ValueEventListener foodValueListener;
    private ValueEventListener notificationValueListener;
    private ValueEventListener reviewValueListener;

    private Notification notification;
    private Food food;
    private Profile profile;
    private ValueEventListener profileValueListener;
    private Observable<Food> foodObservable;
    private Observable<Profile> profileObservable;


    public AcceptOderUtils() {
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference();
        user = mAuth.getCurrentUser();
    }


    public void checkIfOrderIsBookedAndShowOrderAcceptDialog(final Activity activity) {
        foodValueListener = mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.POSTED_BY).equalTo(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                showData(dataSnapshot, activity);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    private void showData(DataSnapshot dataSnapshot, Activity activity) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getValue() != null) {
                collectFood((Map<String, Object>) ds.getValue(), activity);
            }
        }


    }

    private void collectFood(Map<String, Object> value, Activity activity) {
        final Food food = new Food();
        food.setPushId((String) value.get(Constants.PUSH_ID));
        food.setDishName((String) value.get(Constants.DISH_NAME));
        food.setCuisine((String) value.get(Constants.CUISINE));
        if (value.get(Constants.EXPIRY_TIME) != null) {
            food.setExpiryTime((long) value.get(Constants.EXPIRY_TIME));
        }
        food.setIngredientsTags(String.valueOf(value.get(Constants.INGREDIENTS_TAGS)));
        food.setImageUri((String) value.get(Constants.IMAGE_URI));
        food.setPrice(Long.parseLong(String.valueOf(value.get(Constants.PRICE))));
        food.setNumberOfServings((long) value.get(Constants.NO_OF_SERVINGS));
        food.setNumberOfServingsPurchased((long) value.get(Constants.NO_OF_SERVINGS_PURCHASED));

        food.setLatitude((double) value.get(Constants.LATITUDE));
        food.setPickUpLocation((String) value.get(Constants.PICK_UP_LOCATION));
        food.setLongitude((double) value.get(Constants.LONGITUDE));
        food.setCheckIfOrderIsActive((boolean) value.get(Constants.CHECK_IF_ORDER_IS_ACTIVE));
        food.setCheckIfFoodIsInDraftMode((boolean) value.get(Constants.CHECK_IF_FOOD_IS_IN_DRAFT_MODE));
        food.setCheckIfOrderIsPurchased((boolean) value.get(Constants.CHECK_IF_ORDER_IS_PURCHASED));

        if (value.get(Constants.CHECK_IF_ORDERED_IS_BOOKED) != null)
            food.setCheckIfOrderIsBooked((boolean) value.get(Constants.CHECK_IF_ORDERED_IS_BOOKED));

        if (value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS) != null)
            food.setCheckIfOrderIsInProgress((boolean) value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS));


        if (value.get(Constants.TIME_STAMP) != null) {
            food.setTime((long) value.get(Constants.TIME_STAMP));
        }
        if (value.get(Constants.PURCHASED_BY) != null) {
            food.setPurchasedBy(String.valueOf(value.get(Constants.PURCHASED_BY)));
        }


        if (value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS) != null) {
            food.setCheckIfOrderIsInProgress((Boolean) value.get(Constants.CHECK_IF_ORDER_IS_IN_PROGRESS));
        }


        if (value.get(Constants.POSTED_BY) != null) {
            food.setPostedBy((String) value.get(Constants.POSTED_BY));
        }

        if (value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED) != null) {
            food.setCheckIfOrderIsCompleted((boolean) value.get(Constants.CHECK_IF_ORDER_IS_COMPLETED));
        }

        if (!food.getCheckIfOrderIsActive()
                && !food.getCheckIfOrderIsPurchased()
                && !food.getCheckIfFoodIsInDraftMode()
                && food.getCheckIfOrderIsBooked()
                && !food.getCheckIfOrderIsInProgress()
                && !food.getCheckIfOrderIsCompleted()

                ) {

            foodObservable = Observable.create(emitter -> {
                emitter.onNext(food);
                emitter.onComplete();
            });

            profileValueListener = mDatabaseReference.child(Constants.PROFILE).child(food.getPurchasedBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Log.d(TAG, "onDataChange: " + dataSnapshot);
                    showProfileData(dataSnapshot);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });


            notificationValueListener = mDatabaseReference.child(Constants.NOTIFICATION)
                    .orderByChild(Constants.ORDER_ID).equalTo(food.getPushId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.getValue() != null) {
                                    collectNotification((Map<String, Object>) ds.getValue());

                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

            getBuyerOverallReview(food.getPurchasedBy(), activity);


        }


    }

    private void showProfileData(DataSnapshot dataSnapshot) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        collectProfile((Map<String, Object>) dataSnapshot.getValue());
    }

    private void collectProfile(Map<String, Object> value) {
        final Profile profile = new Profile();
        profile.setUserId(String.valueOf(value.get(Constants.USER_ID)));
        profile.setFirstName(String.valueOf(value.get(Constants.FIRST_NAME)));
        profile.setLastName(String.valueOf(value.get(Constants.LAST_NAME)));
        profile.setProfilePhotoUri(String.valueOf(value.get(Constants.PROFILE_PHOTO_URI)));
        if (value.get(Constants.EMAIL) != null) {
            profile.setEmail(String.valueOf(value.get(Constants.EMAIL)));
        }
        profile.setUserType(String.valueOf(value.get(Constants.USER_TYPE)));

        profileObservable = Observable.create(emitter -> {
            emitter.onNext(profile);
            emitter.onComplete();
        });
    }

    String userId = "";

    private void getBuyerOverallReview(String userId, Activity activity) {
        this.userId = userId;
        if (userId != null) {


            reviewValueListener = mDatabaseReference.child(Constants.REVIEW).orderByChild(Constants.USER_ID).equalTo(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    showReviewData(dataSnapshot, activity);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }


    }

    private void showReviewData(DataSnapshot dataSnapshot, Activity activity) {
        if (dataSnapshot.getValue() == null) {
            return;
        }

        ;
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            collectReview((Map<String, Object>) ds.getValue());
        }
        itemCount *= 3;
        ratingAvg = ratingAvg / itemCount;


        if (foodObservable != null & profileValueListener != null & notificationObservable != null) {
            Observable.zip(foodObservable, profileObservable, notificationObservable, (v1, v2, v3) -> {
                Object[] objects = new Object[3];
                objects[0] = v1;
                objects[1] = v2;
                objects[2] = v3;


                return objects;
            })// Run on a background thread
                    .subscribeOn(Schedulers.newThread())
                    // Be notified on the main thread
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(observer(activity, ratingAvg));
        }

        
        ratingAvg = 0;
        itemCount = 0;

    }

    private Observer<Object[]> observer(Activity activity, float ratingAvg) {
        return new Observer<Object[]>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object[] objects) {
                if (objects[0] instanceof Food) {
                    food = (Food) objects[0];
                }

                if (objects[1] instanceof Profile) {
                    profile = (Profile) objects[1];
                }
                if (objects[2] instanceof Notification) {
                    notification = (Notification) objects[2];
                }


                AcceptOrderDialogFragment acceptOrderDialogFragment = AcceptOrderDialogFragment.newInstance(food, notification, profile, ratingAvg);

                FragmentTransaction ft = ((SellerActivity) activity).getSupportFragmentManager().beginTransaction();

                Fragment prev = ((SellerActivity) activity).getSupportFragmentManager().findFragmentByTag(acceptOrderDialogFragment.TAG_FRAGMENT);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);


                ft.add(acceptOrderDialogFragment, acceptOrderDialogFragment.TAG_FRAGMENT).commitAllowingStateLoss();

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

    }

    private long itemCount = 0;
    private float ratingAvg = 0;
    private static final String TAG = "AcceptOderUtils";

    private void collectReview(Map<String, Object> value) {
        Review review = new Review();
        review.setReviewId((String) value.get(Constants.REVIEW_ID));
        review.setOrderId((String) value.get(Constants.ORDER_ID));

        if (value.get(Constants.QUESTION_ONE_ANSWER) != null)
            review.setQuestionOneAnswer(Double.parseDouble(String.valueOf(value.get(Constants.QUESTION_ONE_ANSWER))));

        if (value.get(Constants.QUESTION_TWO_ANSWER) != null)
            review.setQuestionTwoAnswer(Double.parseDouble(String.valueOf(value.get(Constants.QUESTION_TWO_ANSWER))));

        if (value.get(Constants.QUESTION_THREE_ANSWER) != null)
            review.setQuestionThreeAnswer(Double.parseDouble(String.valueOf(value.get(Constants.QUESTION_THREE_ANSWER))));

        review.setReviewGivenBy((String) value.get(Constants.REVIEW_GIVEN_BY));
        review.setUserId((String) value.get(Constants.USER_ID));
        review.setReviewType((String) value.get(Constants.REVIEW_TYPE));


        if (review.getReviewType() != null && review.getReviewType().equals(Constants.REVIEW_FROM_SELLER)) {
            // reviews.add(review);
            Log.d(TAG, "collectReview: rating " + review.getQuestionOneAnswer());
            ratingAvg += (float) (review.getQuestionOneAnswer() + review.getQuestionTwoAnswer() + review.getQuestionThreeAnswer());
            Log.d(TAG, "collectReview: total " + ratingAvg);
            itemCount++;
        }


    }

    Observable<Notification> notificationObservable;

    private void collectNotification(Map<String, Object> value) {
        final Notification notification = new Notification();
        notification.setOrderId(String.valueOf(value.get(Constants.ORDER_ID)));
        notification.setNotificationId(String.valueOf(value.get(Constants.NOTIFICATION_ID)));
        notification.setMessage(String.valueOf(value.get(Constants.MESSAGE)));
        notification.setTitle(String.valueOf(value.get(Constants.TITLE)));
        notification.setCheckIfButtonShouldBeEnabled((boolean) value.get(Constants.CHECK_IF_BUTTON_SHOULD_BE_ENABLED));
        if (value.get(Constants.NOTIFICATION_IMAGE) != null) {
            notification.setNotificationImage(String.valueOf(Constants.NOTIFICATION_IMAGE));
        }
        notification.setCheckIfNotificationAlertShouldBeSent((boolean) value.get(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SENT));
        notification.setCheckIfNotificationAlertShouldBeShown((boolean) value.get(Constants.CHECK_IF_NOTIFICATION_ALERT_SHOULD_BE_SHOWN));
        notification.setSenderId(String.valueOf(value.get(Constants.SENDER_ID)));
        notification.setReceiverId(String.valueOf(value.get(Constants.RECEIVER_ID)));
        notification.setNotificationType(String.valueOf(value.get(Constants.NOTIFICATION_TYPE)));
        if (notification.getReceiverId().equals(user.getUid()) && notification.getCheckIfNotificationAlertShouldBeShown()) {
            notificationObservable = Observable.create(emitter -> {
                emitter.onNext(notification);
                emitter.onComplete();
            });
        }

    }

    public void removeListener() {
        if (foodValueListener != null) {
            mDatabaseReference.child(Constants.FOOD).orderByChild(Constants.POSTED_BY).equalTo(user.getUid()).removeEventListener(foodValueListener);
        }

        if (notificationValueListener != null) {
            mDatabaseReference.child(Constants.NOTIFICATION)
                    .orderByChild(Constants.ORDER_ID).equalTo(food.getPushId()).removeEventListener(notificationValueListener);
        }

        if (reviewValueListener != null) {
            mDatabaseReference.child(Constants.REVIEW).orderByChild(Constants.USER_ID).equalTo(userId).removeEventListener(reviewValueListener);
        }

        if (profileValueListener != null) {
            mDatabaseReference.child(Constants.PROFILE).child(food.getPurchasedBy()).removeEventListener(profileValueListener);
        }
    }
}
