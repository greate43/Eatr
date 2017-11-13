package sk.greate43.eatr.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.jetbrains.annotations.Contract;

/**
 * Created by great on 11/4/2017.
 */

public class Seller implements Parcelable {


    public static final Creator<Seller> CREATOR = new Creator<Seller>() {
        @NonNull
        @Override
        public Seller createFromParcel(Parcel in) {
            return new Seller(in);
        }

        @NonNull
        @Contract(pure = true)
        @Override
        public Seller[] newArray(int size) {
            return new Seller[size];
        }
    };
    private String dishName;
    private String cuisine;
    private Float expiryTime;
    private String pickUpLocation;
    private String imageUri;

    public Seller() {
        // Default constructor required for calls to DataSnapshot.getValue(Seller.class)
    }



    public Seller(String dishName, String cuisine, Float expiryTime, String pickUpLocation, String imageUri) {
        this.dishName = dishName;
        this.cuisine = cuisine;
        this.expiryTime = expiryTime;
        this.pickUpLocation = pickUpLocation;
        this.imageUri = imageUri;
    }

    public Seller(Parcel in) {
        dishName = in.readString();
        cuisine = in.readString();
        if (in.readByte() == 0) {
            expiryTime = null;
        } else {
            expiryTime = in.readFloat();
        }
        pickUpLocation = in.readString();
        imageUri = in.readString();
    }

    public String getDishName() {
        return dishName;
    }

    public String getCuisine() {
        return cuisine;
    }

    public Float getExpiryTime() {
        return expiryTime;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public String getImageUri() {
        return imageUri;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dishName);
        dest.writeString(cuisine);
        dest.writeFloat(expiryTime);
        dest.writeString(pickUpLocation);
        dest.writeString(imageUri);
    }
}
