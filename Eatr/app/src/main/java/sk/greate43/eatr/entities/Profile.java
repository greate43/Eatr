package sk.greate43.eatr.entities;

import java.io.Serializable;

/**
 * Created by great on 12/22/2017.
 */

public class Profile implements Serializable {
    private static long serialVersionUID = 2L;
    private String userId;
    private String firstName;
    private String lastName;
    private String userType;
    private String email;
    private String profilePhotoUri;
    private Boolean doesTheProfileNeedAnUpdate;


    public Profile() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getDoesTheProfileNeedAnUpdate() {
        return doesTheProfileNeedAnUpdate;
    }

    public void setDoesTheProfileNeedAnUpdate(Boolean doesTheProfileNeedAnUpdate) {
        this.doesTheProfileNeedAnUpdate = doesTheProfileNeedAnUpdate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getProfilePhotoUri() {
        return profilePhotoUri;
    }

    public void setProfilePhotoUri(String profilePhotoUri) {
        this.profilePhotoUri = profilePhotoUri;
    }


    public String getFullname(){
        return firstName+" "+lastName;
    }
}
