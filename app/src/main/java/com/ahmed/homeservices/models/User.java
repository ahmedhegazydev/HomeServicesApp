package com.ahmed.homeservices.models;

import com.ahmed.homeservices.constants.Constants;

public class User {


    private String userName;//Full name not user name
    private String userPassword;
    private String userEmail;
    private String userPhoneNumber;
    private String userType = Constants.USER_TYPE_FREE;
    private boolean userStatusActivation = false;
    private String createDate;
    private String addressOrCurruntLocation;

    public User(String userName, String userPassword, String userEmail, String userPhoneNumber, String userType, boolean userStatusActivation) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userType = userType;
        this.userStatusActivation = userStatusActivation;
    }

    public User() {

    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getAddressOrCurruntLocation() {
        return addressOrCurruntLocation;
    }

    public void setAddressOrCurruntLocation(String addressOrCurruntLocation) {
        this.addressOrCurruntLocation = addressOrCurruntLocation;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public boolean isUserStatusActivation() {
        return userStatusActivation;
    }

    public void setUserStatusActivation(boolean userStatusActivation) {
        this.userStatusActivation = userStatusActivation;
    }
}
