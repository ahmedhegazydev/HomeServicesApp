package com.ahmed.homeservices.models;

import java.util.ArrayList;

public class ServiceRequest {

    String userId;
    ArrayList<Category> categoriesSelected;
    String requestFiredDate;
    String requestFiredTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Category> getCategoriesSelected() {
        return categoriesSelected;
    }

    public void setCategoriesSelected(ArrayList<Category> categoriesSelected) {
        this.categoriesSelected = categoriesSelected;
    }

    public String getRequestFiredDate() {
        return requestFiredDate;
    }

    public void setRequestFiredDate(String requestFiredDate) {
        this.requestFiredDate = requestFiredDate;
    }

    public String getRequestFiredTime() {
        return requestFiredTime;
    }

    public void setRequestFiredTime(String requestFiredTime) {
        this.requestFiredTime = requestFiredTime;
    }
}
