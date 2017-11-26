package sk.greate43.eatr.entities;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by great on 11/4/2017.
 */

public class Seller implements Serializable {
    private static long serialVersionUID = 1L;


    private String dishName;
    private String cuisine;
    private Float expiryTime;
    private String pickUpLocation;
    private String imageUri;
    private Map<String, String> timeStamp;

    public Seller() {
        // Default constructor required for calls to DataSnapshot.getValue(Seller.class)
    }

    public void setTimeStamp(Map<String, String> timeStamp) {
        this.timeStamp = timeStamp;
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

    public Float getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Float expiryTime) {
        this.expiryTime = expiryTime;
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


}
