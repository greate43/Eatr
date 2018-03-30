package sk.greate43.eatr.entities;

import java.io.Serializable;

/**
 * Created by great on 3/30/2018.
 */

public class LiveLocationUpdate implements Serializable {
    private static long serialVersionUID = 4L;
    private String buyerId;
    private String sellerId;
    private String OrderID;
    private double longitude;
    private double latitude;

    public LiveLocationUpdate() {

    }



    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
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
}
