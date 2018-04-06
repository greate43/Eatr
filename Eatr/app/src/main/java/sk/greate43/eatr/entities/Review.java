package sk.greate43.eatr.entities;

import java.io.Serializable;

public class Review implements Serializable{
    private static long serialVersionUID = 4L;
    private double overAllFoodQuality;
    private String reviewGivenBy;
    private String reviewId;
    private String userId;
    private String orderId;

    public Review() {
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
