package com.ahmed.homeservices.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.home.MainActivity;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class Utils {
    private static final Utils ourInstance = new Utils();

    public static Utils getInstance() {
        return ourInstance;
    }

    private Utils() {
    }

    public void fullScreen(Activity activity) {
// remove title
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    public void vibrate(Activity activity, int milliSec) {
        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(milliSec);
        }
    }

    public AlertDialog pleaseWait(Activity activity) {
        AlertDialog spotsDialog = new SpotsDialog.Builder()
                .setContext(activity)
                .setCancelable(false)
                .setMessage("Please wait ...")
                .build();
        return spotsDialog;
    }

    public  boolean isValidEmail(String target) {
        //return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
        return (Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public GoogleSignInClient initGoogleSignInClient(Context context) {
        GoogleSignInClient googleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
//                .requestIdToken("764111094900-6ut82vlc5pg34clg74cl7bs3nlntf88i.apps.googleusercontent.com")
                .requestEmail()
                .build();

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();

        googleSignInClient = GoogleSignIn.getClient(context, gso);
        return googleSignInClient;
    }

    public void signOutFromGoogleFacebook(Context context){
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        Utils.getInstance().initGoogleSignInClient(context).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context, "Google signed out", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
