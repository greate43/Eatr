package sk.greate43.eatr.entities;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by great on 11/4/2017.
 */
public class Seller implements Serializable {
    private static final String TAG = "Seller";
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

    public Seller() {
        // Default constructor required for calls to DataSnapshot.getValue(Seller.class)
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

    public boolean isCheckIfOrderIsActive() {
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

    @Exclude
    public String getTime() {
        return time;
    }

    @Exclude
    public void setTime(String time) {
        this.time = time;
    }


}
