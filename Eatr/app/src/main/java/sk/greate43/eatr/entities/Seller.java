package sk.greate43.eatr.entities;

/**
 * Created by great on 11/4/2017.
 */

public class Seller {


    public String dishName;
    public String cuisine;
    public Float expiryTime;
    public String pickUpLocation;
    public String imageUri;

    public Seller() {

    }

    public Seller(String dishName, String cuisine, Float expiryTime, String pickUpLocation,String imageUri) {
        this.dishName = dishName;
        this.cuisine = cuisine;
        this.expiryTime = expiryTime;
        this.pickUpLocation = pickUpLocation;
        this.imageUri = imageUri;
    }
}
