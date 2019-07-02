package com.ahmed.homeservices.view_holders;

import android.widget.ImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


// Your "view holder" that holds references to each subview
public class VHAttachedFiles {
    //    public final CircularProgressButton progressButton;
    public final FloatingActionButton fabClose;
    public final ImageView imageView;
    public final FloatingActionButton fabUpload;


    public VHAttachedFiles(ImageView imageView, FloatingActionButton fabClose, FloatingActionButton fabUpload) {
        this.fabClose = fabClose;
        this.fabUpload = fabUpload;
        this.imageView = imageView;
    }

}