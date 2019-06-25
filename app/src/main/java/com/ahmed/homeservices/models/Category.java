package com.ahmed.homeservices.models;

public class Category {

    private String title;
    private int iconResId;

    public Category() {
    }

    public String getTitle() {
        return title;
    }

    public Category(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
