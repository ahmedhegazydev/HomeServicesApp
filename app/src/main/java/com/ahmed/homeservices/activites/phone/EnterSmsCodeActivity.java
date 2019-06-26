package com.ahmed.homeservices.activites.phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.home.MainActivity;
import com.ahmed.homeservices.activites.login_register.RegisterActivity;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.irozon.sneaker.Sneaker;
import com.pixplicity.easyprefs.library.Prefs;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EnterSmsCodeActivity extends AppCompatActivity {
    private static final String TAG = "EnterSmsCodeActivity";
    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    @BindView(R.id.etSmsCode)
    PinEntryEditText etSmsCode;
    @BindView(R.id.tvActivate)
    TextView tvActivate;
    @BindView(R.id.tvResendSmsCode)
    TextView tvResendSmsCode;
    AlertDialog spotsDialog;
    FirebaseAuth firebaseAuth;
    CountDownTimer countDownTimer;
    Type type = new TypeToken<User>() {
    }.getType();
    Gson gson = new Gson();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        tvResendSmsCode.setEnabled(false);
        if (countDownTimer != null)
            countDownTimer.start();


    }

    @OnClick(R.id.tvResendSmsCode)
    public void tvResendSmsCodeClicked(View v) {
        tvResendSmsCode.setEnabled(false);
        countDownTimer.start();
        RegisterActivity registerActivity = new RegisterActivity();
//        resendVerificationCode(registerActivity.getPhoneNumber(), registerActivity.getmResendToken());
        resendVerificationCode(
                Prefs.getString(Constants.PHONE_NUMBER, "")
                , registerActivity.getmResendToken());


    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    @OnClick(R.id.tvActivate)
    public void btnActivateClicked(View v) {
        if (TextUtils.isEmpty(etSmsCode.getText().toString())) {
            Toast.makeText(this, "Enter Sms code", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(this, "Not Empty", Toast.LENGTH_SHORT).show();
            if (etSmsCode.getText().toString().length() != 6) {
                Toast.makeText(this, "Enter full Sms code", Toast.LENGTH_SHORT).show();
            } else {
//                verifyPhoneNumberWithCode(new RegisterActivity().getmVerificationId()
//                        , etSmsCode.getText().toString());
                verifyPhoneNumberWithCode(Prefs.getString(Constants.VERIFICATRION_ID, "")
                        , etSmsCode.getText().toString());
            }

        }
    }

    @OnClick(R.id.llRechangePhoneNumber)
    public void llRechangePhoneNumberClicked(View v) {
//        Intent intent = new Intent(this, RegisterActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.enter, R.anim.exit);

        onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.getInstance().fullScreen(this);
        setContentView(R.layout.activity_enter_sms_code);
        ButterKnife.bind(this);
        initVars();
        initTimerResendSmsCode();
    }

    private void initTimerResendSmsCode() {
        countDownTimer = new CountDownTimer(60 * 60 * 1000, 1000) {
            // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
//                text1.setText("" + String.format(FORMAT,
//                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
//                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
//                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
//                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
//                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                int sec = (int) (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
                tvResendSmsCode.setText("Resend sms " + sec);
                tvResendSmsCode.setEnabled(false);
            }

            public void onFinish() {
                tvResendSmsCode.setText("Resend sms");
                tvResendSmsCode.setEnabled(true);
                countDownTimer.cancel();
                countDownTimer = null;
            }
        };

    }

    private void initVars() {
        spotsDialog = Utils.getInstance().pleaseWait(this);
        firebaseAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.e(TAG, "onCodeAutoRetrievalTimeOut: TimeOut");
            }

            //Called when verification is done without user interaction , ex- when user is verified without code,
            // it's takes PhoneAuthCredential (info about Auth Credential for phone)
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) { //listener for if the code is send to the same device,
                // credential phoneNum style and its details
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("onVerificationCompleted", "onVerificationCompleted:" + credential);

                // Update the UI and attempt sign in with the phone credential
//                updateUI(STATE_VERIFY_SUCCESS, credential);
//                signInWithPhoneAuthCredential(credential);
            }

            //Called when some error occurred such as failing of sending SMS or Number format exception
            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
//                btnRegisterPhoneNumber.setText(getString(R.string.lets_go));
                //Number format exception
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
//                    etUserPhone.setError("Invalid Phone Number !");
//                    Sneaker.with(EnterSmsCodeActivity.this).setTitle("Invalid Phone Number !").sneakError();
//                    spotsDialog.dismiss();

                } else if (e instanceof FirebaseTooManyRequestsException) { // Quota exceeded
                    // The SMS quota for the project has been exceeded (u send a lot of codes in short time )
//                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
//                            Snackbar.LENGTH_SHORT).show();
//                    initCountDownTimerResendCode();

//                    btnRegisterPhoneNumber.setEnabled(false);
//                    countDownTimer.start();
                }

                // Show a message and update the UI
//                updateUI(STATE_VERIFY_FAILED);
            }

            //Called when verification code is successfully sent to the phone number.
            //A 'token' that can be used to force re-sending an SMS verification code
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                Log.d(TAG, "onCodeSent2:" + token);
                // Save verification ID and resending token so we can use them later
                RegisterActivity registerActivity = new RegisterActivity();
                mVerificationId = registerActivity.getmVerificationId();
                mResendToken = registerActivity.getmResendToken();
                updateUI(STATE_CODE_SENT);
            }
        };
        etSmsCode.setOnPinEnteredListener(str -> {
            if (str.toString().equals("1234")) {

            } else {

            }
        });
        etSmsCode.requestFocus();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//                Intent intent = new Intent(this, RegisterActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    //sign in with Auth credential of phone
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        if (spotsDialog != null) {
            spotsDialog.show();
        }
        //Auto retriever (if user register before Move to MainActivity directly)
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = task.getResult().getUser();
                        updateUI(STATE_SIGNIN_SUCCESS, user);
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
//                                mVerificationField.setError("Invalid code.");
                            //send error to model then check it in Fragment to display error in fragment
//                                EventBus.getDefault().post(new MsgEvevntErrorSms(true));
                            Toast.makeText(this, "Invalid sms code", Toast.LENGTH_SHORT).show();
                        }
                        updateUI(STATE_SIGNIN_FAILED);
                    }

                });
    }

    private void updateUI(int uiState) {
        updateUI(uiState, FirebaseAuth.getInstance().getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        if (spotsDialog != null) {
            spotsDialog.dismiss();
        }
        switch (uiState) {
            case STATE_INITIALIZED:
                Log.e(TAG, "STATE_INITIALIZED");
                // Initialized state, show only the phone number field and start button
                break;
            case STATE_CODE_SENT:
                Log.e(TAG, "STATE_CODE_SENT");
//                Prefs.edit().remove(Constants.BOTTOM_SHEET_IS_SHOWN).apply();
                Intent intent = new Intent(this, EnterSmsCodeActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
                break;
            case STATE_VERIFY_FAILED:
                Log.e(TAG, "STATE_VERIFY_FAILED");

                break;
            case STATE_VERIFY_SUCCESS:
                Log.e(TAG, "STATE_VERIFY_SUCCESS");
                break;
            case STATE_SIGNIN_FAILED:
                Log.e(TAG, "STATE_SIGNIN_FAILED");
                // No-op, handled by sign-in check
                break;
            case STATE_SIGNIN_SUCCESS:
                Log.e(TAG, "STATE_SIGNIN_SUCCESS");
                // Np-op, handled by sign-in check
//                registerUserToFireDatabase(user);
                break;
        }

        if (user == null) {
            // Signed out
        } else {
            // Signed in
            registerUserInToFirebaseDatabase();

        }
    }

    private void registerUserInToFirebaseDatabase() {

        User user = gson.fromJson(Prefs.getString(Constants.USER, ""), type);

        if (spotsDialog != null)
            spotsDialog.show();


        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        RefBase.refUser()
//                .push()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        spotsDialog.dismiss();

                        Prefs.edit().putString(Constants.FIREBASE_UID, firebaseUser.getUid()).apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//          intent.putExtra(Constants.USER_TYPE, Constants.DRIVERS);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();

                        Toast.makeText(EnterSmsCodeActivity.this, "Registered",
                                Toast.LENGTH_SHORT).show();


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Sneaker.with((Activity) getApplicationContext()).setTitle(Constants.NETWORK_ERROR)
                                .sneakError();
                        spotsDialog.dismiss();
                    }
                });

    }


}
