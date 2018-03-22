package sk.greate43.eatr.entities;

import java.io.Serializable;

/**
 * Created by great on 3/22/2018.
 */

public class Notification implements Serializable {
    private static long serialVersionUID = 3L;
    private boolean checkIfButtonShouldBeEnabled;
    private String message;
    private String title;
    private String senderId;
    private String receiverId;
    private String orderId;

    public String getSenderId() {
        return senderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public Notification() {

    }

    public boolean isCheckIfButtonShouldBeEnabled() {
        return checkIfButtonShouldBeEnabled;
    }

    public void setCheckIfButtonShouldBeEnabled(boolean checkIfButtonShouldBeEnabled) {
        this.checkIfButtonShouldBeEnabled = checkIfButtonShouldBeEnabled;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
