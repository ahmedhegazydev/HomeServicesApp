package com.ahmed.homeservices.models;

public class Category {

    private String title;
    private int iconResId;
    private String downloadUrl;
    public Category() {
    }

    public String getTitle() {
        return title;
    }

    public Category(String title, int iconResId) {
        this.title = title;
        this.iconResId = iconResId;
    }

    public Category(String title, String downloadUrl) {
        this.title = title;
        this.downloadUrl = downloadUrl;
    }


    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
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
