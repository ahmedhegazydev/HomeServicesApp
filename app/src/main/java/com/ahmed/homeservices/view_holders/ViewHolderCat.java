package com.ahmed.homeservices.view_holders;

import android.widget.ImageView;
import android.widget.TextView;

// Your "view holder" that holds references to each subview
public class ViewHolderCat {
    public final TextView tvTitle;
    public final ImageView imageViewIcon;


    public ViewHolderCat(TextView tvTitle, ImageView imageViewIcon) {
        this.tvTitle = tvTitle;
        this.imageViewIcon = imageViewIcon;
    }
}