package com.ahmed.homeservices.dialogs;

import android.app.Activity;

import cn.pedant.SweetAlert.SweetAlertDialog;

//import cn.pedant.SweetAlert.SweetAlertDialog;

public class SweetDialog {
    private static final SweetDialog ourInstance = new SweetDialog();

    public static SweetDialog getInstance() {
        return ourInstance;
    }

    private SweetDialog() {
    }


    public void error(Activity activity) {
        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("Something went wrong!")
                .show();
    }

    public void error(Activity activity, String s) {
        new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(s)
                .show();

    }
}
