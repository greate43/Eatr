package sk.greate43.eatr.entities;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by great on 3/22/2018.
 */

public class Notification implements Serializable {

    private String notificationImage;
    private String message;
    private String title;
    private String senderId;
    private String receiverId;
    private String orderId;
    private String notificationId;
    private String notificationType;
    private Map<String, String> timeStamp;

    private boolean checkIfButtonShouldBeEnabled;
    private boolean checkIfNotificationAlertShouldBeSent;
    private boolean checkIfNotificationAlertShouldBeShown;
    private boolean checkIfDialogShouldBeShown;


    public Notification() {

    }

    public boolean getCheckIfDialogShouldBeShown() {
        return checkIfDialogShouldBeShown;
    }

    public void setCheckIfDialogShouldBeShown(boolean checkIfDialogShouldBeShown) {
        this.checkIfDialogShouldBeShown = checkIfDialogShouldBeShown;
    }

    public Map<String, String> getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Map<String, String> timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

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

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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
