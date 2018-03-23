package sk.greate43.eatr.entities;

import java.io.Serializable;

/**
 * Created by great on 3/22/2018.
 */

public class Notification implements Serializable {
    private static long serialVersionUID = 3L;
    private boolean checkIfButtonShouldBeEnabled;
    private boolean checkIfNotificationAlertShouldBeSent;
    private boolean checkIfNotificationAlertShouldBeShown;
    private String notificationImage;
    private String message;
    private String title;
    private String senderId;
    private String receiverId;
    private String orderId;
    private String notificationId;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public boolean getCheckIfNotificationAlertShouldBeShown() {
        return checkIfNotificationAlertShouldBeShown;
    }

    public void setCheckIfNotificationAlertShouldBeShown(boolean checkIfNotificationAlertShouldBeShown) {
        this.checkIfNotificationAlertShouldBeShown = checkIfNotificationAlertShouldBeShown;
    }

    public boolean getCheckIfNotificationAlertShouldBeSent() {
        return checkIfNotificationAlertShouldBeSent;
    }

    public void setCheckIfNotificationAlertShouldBeSent(boolean checkIfNotificationAlertShouldBeSent) {
        this.checkIfNotificationAlertShouldBeSent = checkIfNotificationAlertShouldBeSent;
    }

    public String getNotificationImage() {
        return notificationImage;
    }

    public void setNotificationImage(String notificationImage) {
        this.notificationImage = notificationImage;
    }

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

    public boolean getCheckIfButtonShouldBeEnabled() {
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
