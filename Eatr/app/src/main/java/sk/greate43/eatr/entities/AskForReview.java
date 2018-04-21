package sk.greate43.eatr.entities;

import java.io.Serializable;

public class AskForReview implements Serializable {
    private String orderId;
    private boolean checkIfReviewDialogShouldBeShownForBuyer;
    private boolean checkIfReviewDialogShouldBeShownForSeller;
    private String postedBy;
    private String purchasedBy;

    public AskForReview() {
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean getCheckIfReviewDialogShouldBeShownForBuyer() {
        return checkIfReviewDialogShouldBeShownForBuyer;
    }

    public void setCheckIfReviewDialogShouldBeShownForBuyer(boolean checkIfReviewDialogShouldBeShownForBuyer) {
        this.checkIfReviewDialogShouldBeShownForBuyer = checkIfReviewDialogShouldBeShownForBuyer;
    }

    public boolean getCheckIfReviewDialogShouldBeShownForSeller() {
        return checkIfReviewDialogShouldBeShownForSeller;
    }

    public void setCheckIfReviewDialogShouldBeShownForSeller(boolean checkIfReviewDialogShouldBeShownForSeller) {
        this.checkIfReviewDialogShouldBeShownForSeller = checkIfReviewDialogShouldBeShownForSeller;
    }
}
