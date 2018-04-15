package sk.greate43.eatr.entities;

import java.io.Serializable;

public class Review implements Serializable{
    private double overAllFoodQuality;
    private String reviewGivenBy;
    private String reviewId;
    private String userId;
    private String orderId;
    private String reviewType;

    public Review() {
    }

    public String getReviewType() {
        return reviewType;
    }

    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }

    public double getOverAllFoodQuality() {
        return overAllFoodQuality;
    }

    public void setOverAllFoodQuality(double overAllFoodQuality) {
        this.overAllFoodQuality = overAllFoodQuality;
    }

    public String getReviewGivenBy() {
        return reviewGivenBy;
    }

    public void setReviewGivenBy(String reviewGivenBy) {
        this.reviewGivenBy = reviewGivenBy;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
