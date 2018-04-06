package sk.greate43.eatr.entities;

import android.net.Uri;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by great on 11/4/2017.
 */
public class Food implements Serializable {
    private static final String TAG = "Food";
    private static long serialVersionUID = 1L;
    private String dishName;
    private String cuisine;
    private String ingredientsTags;
    private String pickUpLocation;
    private String imageUri;
    private Map<String, String> timeStamp;
    private String time;
    private boolean checkIfOrderIsActive;
    private long expiryTime;
    private long price;
    private long numberOfServings;
    private long numberOfServingsPurchased;
    private Uri image;
    private double longitude;
    private double latitude;
    private boolean checkIfFoodIsInDraftMode;
    private String pushId;
    private boolean checkIfOrderIsPurchased;
    private String purchasedBy;
    private String postedBy;
    private long purchasedDate;
    private boolean checkIfOrderIsInProgress;
    private boolean checkIfOrderIsAccepted;
    private boolean checkIfOrderIsBooked;
    private boolean checkIfMapShouldBeClosed;
    private boolean checkIfOrderIsCompleted;
    private boolean checkIfReviewDialogShouldBeShown;

    public Food() {
        // Default constructor required for calls to DataSnapshot.getValue(Food.class)
    }

    public boolean getCheckIfReviewDialogShouldBeShown() {
        return checkIfReviewDialogShouldBeShown;
    }

    public void setCheckIfReviewDialogShouldBeShown(boolean checkIfReviewDialogShouldBeShown) {
        this.checkIfReviewDialogShouldBeShown = checkIfReviewDialogShouldBeShown;
    }

    public long getNumberOfServingsPurchased() {
        return numberOfServingsPurchased;
    }

    public void setNumberOfServingsPurchased(long numberOfServingsPurchased) {
        this.numberOfServingsPurchased = numberOfServingsPurchased;
    }

    public boolean getCheckIfOrderIsCompleted() {
        return checkIfOrderIsCompleted;
    }

    public void setCheckIfOrderIsCompleted(boolean checkIfOrderIsCompleted) {
        this.checkIfOrderIsCompleted = checkIfOrderIsCompleted;
    }


    public boolean getcheckIfMapShouldBeClosed() {
        return checkIfMapShouldBeClosed;
    }

    public void setCheckIfMapShouldBeClosed(boolean checkIfMapShouldBeClosed) {
        this.checkIfMapShouldBeClosed = checkIfMapShouldBeClosed;
    }

    public boolean getCheckIfOrderIsBooked() {
        return checkIfOrderIsBooked;
    }

    public void setCheckIfOrderIsBooked(boolean checkIfOrderIsBooked) {
        this.checkIfOrderIsBooked = checkIfOrderIsBooked;
    }

    public boolean getCheckIfOrderIsInProgress() {
        return checkIfOrderIsInProgress;
    }

    public void setCheckIfOrderIsInProgress(boolean checkIfOrderIsInProgress) {
        this.checkIfOrderIsInProgress = checkIfOrderIsInProgress;
    }

    public long getPurchasedDate() {
        return purchasedDate;
    }

    public void setPurchasedDate(long purchasedDate) {
        this.purchasedDate = purchasedDate;
    }

    public boolean getCheckIfFoodIsInDraftMode() {
        return checkIfFoodIsInDraftMode;
    }

    public void setCheckIfFoodIsInDraftMode(boolean checkIfFoodIsInDraftMode) {
        this.checkIfFoodIsInDraftMode = checkIfFoodIsInDraftMode;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPurchasedBy() {
        return purchasedBy;
    }

    public void setPurchasedBy(String purchasedBy) {
        this.purchasedBy = purchasedBy;
    }

    public boolean getCheckIfOrderIsPurchased() {
        return checkIfOrderIsPurchased;
    }

    public void setCheckIfOrderIsPurchased(boolean checkIfOrderIsPurchased) {
        this.checkIfOrderIsPurchased = checkIfOrderIsPurchased;
    }

    public String getPushId() {
        return pushId;
    }

    public void setPushId(String pushId) {
        this.pushId = pushId;
    }

    public boolean getCheckIfOrderIsAccepted() {
        return checkIfOrderIsAccepted;
    }

    public void setCheckIfOrderIsAccepted(boolean checkIfOrderIsAccepted) {
        this.checkIfOrderIsAccepted = checkIfOrderIsAccepted;
    }

    @Exclude
    public Uri getImage() {
        return image;
    }

    @Exclude
    public void setImage(Uri image) {
        this.image = image;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getIngredientsTags() {
        return ingredientsTags;
    }

    public void setIngredientsTags(String ingredientsTags) {
        this.ingredientsTags = ingredientsTags;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public Map<String, String> getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Map<String, String> timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean getCheckIfOrderIsActive() {
        return checkIfOrderIsActive;
    }

    public void setCheckIfOrderIsActive(boolean checkIfOrderIsActive) {
        this.checkIfOrderIsActive = checkIfOrderIsActive;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }


    public long getNumberOfServings() {
        return numberOfServings;
    }

    public void setNumberOfServings(long numberOfServings) {
        this.numberOfServings = numberOfServings;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    @Exclude
    public String getTime() {
        return time;
    }

    @Exclude
    public void setTime(String time) {
        this.time = time;
    }


}
