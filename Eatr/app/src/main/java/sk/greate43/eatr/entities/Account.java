package sk.greate43.eatr.entities;

import com.google.firebase.database.Exclude;

import java.util.Map;

/**
 * Created by great on 3/31/2018.
 */

public class Account {
    private String userId;
    private double balance;
    private Map<String, String> paymentDate;
    private String orderId;



    @Exclude
    private double paymentInfo;
    @Exclude
    public double getPaymentInfo() {
        return paymentInfo;
    }
    @Exclude
    public void setPaymentInfo(double paymentInfo) {
        this.paymentInfo = paymentInfo;
    }

    public Account() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Map<String, String> getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Map<String, String> paymentDate) {
        this.paymentDate = paymentDate;
    }



    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
