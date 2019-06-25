package com.ahmed.homeservices.activites.login_register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.home.MainActivity;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.customfonts.EditText_Roboto_Regular;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.jgabrielfreitas.core.BlurImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";
    @BindView(R.id.BlurImageViewRegister)
    BlurImageView BlurImageView;
    @BindView(R.id.etUserName)
    EditText_Roboto_Regular etUserName;
    @BindView(R.id.etUserEmail)
    EditText_Roboto_Regular etUserEmail;
    @BindView(R.id.etUserPassword)
    EditText_Roboto_Regular etUserPassword;
    @BindView(R.id.etUserConfPass)
    EditText_Roboto_Regular etUserConfPass;
    @BindView(R.id.etUserPhone)
    EditText_Roboto_Regular etUserPhone;
    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.ShimmerLayout)
    ShimmerLayout ShimmerLayout;
    @BindView(R.id.tvFree)
    TextView tvFree;
    @BindView(R.id.tvPremium)
    TextView tvPremium;
    Handler handler = new Handler();
    int blurVal = 1;
    boolean flag = true, flag2 = true;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
//            blurVal = new Random().nextInt(23);
            if (flag2) {
                blurVal++;
            } else {
                blurVal--;
            }
//            if (blurVal <= 23 && blurVal >= 1) {
//                BlurImageView.setBlur(blurVal);
//                handler.removeCallbacks(runnable);
//                startBluringImageView();
//            }
            BlurImageView.setBlur(blurVal);
            if (blurVal == 23 / 2) {
//                blurVal = 1;
                flag2 = !flag2;
//                handler.removeCallbacks(runnable);
//                startBluringImageView();
            } else {

//                handler.removeCallbacks(runnable);
//                startBluringImageView();
                if (blurVal == 1) {
                    flag2 = !flag2;
                }
            }
            if (flag) {
                handler.removeCallbacks(runnable);
                startBluringImageView();
            } else {
                handler.removeCallbacks(runnable);
            }
            Log.e(TAG, String.valueOf(blurVal));
        }
    };
    AlertDialog spotsDialog;
    boolean freeOrPremSelected = false;
    User user = new User();

    @Override
    protected void onDestroy() {
//        handler.removeCallbacks(runnable);
        flag = false;
        super.onDestroy();

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
        flag = false;
        lottieAnimationView.cancelAnimation();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        startBluringImageView();
    }

    private void startBluringImageView() {
        handler.postDelayed(runnable, 600);
    }

    @OnClick(R.id.Login)
    public void login(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    @OnClick(R.id.register)
    public void register(View view) {
        if (checkInfo()) {
            return;
        }

        user.setUserEmail(etUserEmail.getText().toString());
        user.setUserPassword(etUserPassword.getText().toString());
        user.setUserPhoneNumber(etUserPhone.getText().toString());
        user.setUserName(etUserName.getText().toString());
//        user.setUserType(new Random().nextBoolean() ? Constants.USER_TYPE_FREE : Constants.USER_TYPE_PREMIUM);
        user.setUserStatusActivation(new Random().nextBoolean());
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String today = formatter.format(date);
        user.setCreateDate(today);
        user.setAddressOrCurruntLocation("Giza");


//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

        spotsDialog.show();


        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(user.getUserEmail())
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();
                            if (signInMethods.contains(EmailAuthProvider.EMAIL_PASSWORD_SIGN_IN_METHOD)) {
                                // User can sign in with email/password
                                Toast.makeText(RegisterActivity.this, "Email Already Exist", Toast.LENGTH_SHORT).show();
                                login(null);
                            } else if (signInMethods.contains(EmailAuthProvider.EMAIL_LINK_SIGN_IN_METHOD)) {
                                // User can sign in with email/link
                            } else {
                                FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getUserEmail(), user.getUserPassword())
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                FirebaseDatabase.getInstance()
                                                        .getReference()
                                                        .child(Constants.APP_FIREBASE_DATABASE_REF)
                                                        .push()
                                                        .setValue(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                //login(null);
//                           sendEmailVerification();
//                                                                Toast.makeText(RegisterActivity.this, "User saved", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                                startActivity(intent);
                                                                finish();

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RegisterActivity.this, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
                                                        spotsDialog.dismiss();


                                                    }
                                                })
                                                        .addOnCanceledListener(new OnCanceledListener() {
                                                            @Override
                                                            public void onCanceled() {

                                                            }
                                                        });
                                                sendEmailVerification();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                spotsDialog.dismiss();
                                                Toast.makeText(RegisterActivity.this, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        } else {
                            Log.e(TAG, "Error getting sign in methods for user", task.getException());
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<SignInMethodQueryResult>() {
                    @Override
                    public void onSuccess(SignInMethodQueryResult signInMethodQueryResult) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();

                        spotsDialog.dismiss();
                    }
                });


//        } else {
//
//        }


    }

    private void sendEmailVerification() {
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(RegisterActivity.this, "Email verification sent to " +
                                FirebaseAuth.getInstance().getCurrentUser().getEmail()
                        , Toast.LENGTH_SHORT).show();
                spotsDialog.dismiss();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private boolean checkInfo() {
        if (isEmpty(etUserName.getText())) {
            //start anim
            startWobble(etUserName);
            etUserName.setError("Enter username");
            return true;
        }

        if (isEmpty(etUserEmail.getText())) {
            //start anim
            startWobble(etUserEmail);
            etUserEmail.setError("Enter email address");
            return true;
        }

        if (isEmpty(etUserPhone.getText())) {
            //start anim
            startWobble(etUserPhone);
            etUserPhone.setError("Enter phone number");
            return true;
        }

        if (isEmpty(etUserPassword.getText())) {
            //start anim
            startWobble(etUserPassword);
            etUserPassword.setError("Enter password");
            return true;
        }

        if (TextUtils.equals(etUserPassword.getText().toString(),
                etUserConfPass.getText().toString())) {
            //start anim
            startWobble(etUserPassword);
            etUserPassword.setError("Password does not match");
            return true;
        }

        return false;
    }

    private void startWobble(EditText_Roboto_Regular etUserName) {
        YoYo.with(Techniques.Wobble)
                .duration(400)
                .playOn(etUserName);
        Utils.getInstance().vibrate(this, 35);
    }

    private boolean isEmpty(Editable text) {
        if (TextUtils.isEmpty(text.toString())) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getInstance().fullScreen(this);
        setContentView(R.layout.activity_register_sub);
        ButterKnife.bind(this);
        init();
        startAnimLottie();
        startShimmerAnimation();
        setListenres();
    }

    private void setListenres() {
        tvFree.setOnClickListener(this);
        tvPremium.setOnClickListener(this);
    }

    private void startShimmerAnimation() {
        ShimmerLayout.startShimmerAnimation();
    }

    private void startAnimLottie() {
        lottieAnimationView.playAnimation();
    }

    private void init() {
        BlurImageView.setBlur(5);
        spotsDialog = Utils.getInstance().pleaseWait(this);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(tvFree)) {
//            freeOrPremSelected = !freeOrPremSelected;
            user.setUserType(Constants.USER_TYPE_FREE);

            Log.i(TAG, "onClick: " + user.getUserType());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvFree.setBackgroundColor(getColor(R.color.dodgerblue));
                tvFree.setTextColor(getColor(R.color.whitesmoke));
            } else {
                tvFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.dodgerblue));
                tvFree.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.whitesmoke));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvPremium.setBackgroundColor(getColor(R.color.whitesmoke));
                tvPremium.setTextColor(getColor(R.color.dodgerblue));
            } else {
                tvPremium.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.whitesmoke));
                tvPremium.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.dodgerblue));
            }

        }

        if (v.equals(tvPremium)) {
//            freeOrPremSelected = !freeOrPremSelected;
            user.setUserType(Constants.USER_TYPE_PREMIUM);

            Log.i(TAG, "onClick: " + user.getUserType());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvPremium.setBackgroundColor(getColor(R.color.dodgerblue));
                tvPremium.setTextColor(getColor(R.color.whitesmoke));
            } else {
                tvPremium.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.dodgerblue));
                tvPremium.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.whitesmoke));

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tvFree.setBackgroundColor(getColor(R.color.whitesmoke));
                tvFree.setTextColor(getColor(R.color.dodgerblue));
            } else {
                tvFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.whitesmoke));
                tvFree.setTextColor(ContextCompat.getColor(getApplicationContext(),
                        R.color.dodgerblue));
            }
        }


    }
}
