package com.ahmed.homeservices.activites.login_register;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.home.MainActivity;
import com.ahmed.homeservices.activites.phone.EnterSmsCodeActivity;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.customfonts.EditText_Roboto_Regular;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hbb20.CountryCodePicker;
import com.irozon.sneaker.Sneaker;
import com.jgabrielfreitas.core.BlurImageView;
import com.pixplicity.easyprefs.library.Prefs;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.supercharge.shimmerlayout.ShimmerLayout;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int STATE_INITIALIZED = 1;
    //    @BindView(R.id.etPhoneLogin)
//    EditText_Roboto_Regular etPhoneLogin;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    private static final String TAG = "RegisterActivity";
    @BindView(R.id.countryCodePicker)
    CountryCodePicker countryCodePicker;
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
    FirebaseAuth firebaseAuth;
    String phoneNumber;
    Gson gson = new Gson();
    Type type = new TypeToken<User>() {
    }.getType();
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    @Override
    protected void onStart() {
        super.onStart();
//        insertCatItemsIntoFireDatabaseTest();

    }

    private void insertCatItemsIntoFireDatabaseTest() {

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("Ac", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("electrician", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("plumber", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("Carpenter", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("TV", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("refrigerator", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("appliances", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("ro", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("computers", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("mobile", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("home_secure", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("peast_control", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("car_wash", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("Cleaning", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("painting", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("Washing Machine", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("Packers Movers", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));
        categories.add(new Category("laundry", "https://firebasestorage.google" +
                "apis.com/v0/b/facelockerapp.appspot.com/o/finished.png?alt=media&token" +
                "=f1604be5-00d8-49e2-8b2c-30a10858e495"));


        for (Category category :
                categories) {
            RefBase.refCategoriesForService().push().setValue(category);
        }

    }

    //check phoneNumber is correct then resend code
    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    //sign in with Auth credential of phone
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        spotsDialog.show();
        //Auto retriever (if user register before Move to MainActivity directly)
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
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
                            }
                            updateUI(STATE_SIGNIN_FAILED);
                        }

                    }
                });
    }

    private void updateUI(int uiState) {
        updateUI(uiState, firebaseAuth.getCurrentUser(), null);
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
                codeSentSuccess();
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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//          intent.putExtra(Constants.USER_TYPE, Constants.DRIVERS);
            startActivity(intent);
            finish();
        }
    }

    private void codeSentSuccess() {

        Prefs.putString(Constants.USER, gson.toJson(user, type));

        Intent intent = new Intent(this, EnterSmsCodeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        finish();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    //check phoneNumber is correct with it's country code then send code
    private void startPhoneNumberVerification(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        Prefs.edit().putString(Constants.PHONE_NUMBER, phoneNumber);
        spotsDialog.show();
        //Starts the phone number verification process for the given phone number.
        // Either sends an SMS with a 6 digit code to the phone number specified or triggers the callback with a complete AuthCredential that can be used to log in the user.
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,             // Phone number to verify
                60,                  // Timeout duration
                TimeUnit.SECONDS,     // Unit of timeout
                this,        // Activity (for callback binding)
                mCallbacks);       // OnVerificationStateChangedCallbacks

//        mVerificationInProgress = true;
//        mStatusText.setVisibility(View.INVISIBLE);
    }

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

//        registerUserUsingEmailPassword();
        String fullPhone = countryCodePicker.getSelectedCountryCodeWithPlus() + etUserPhone.getText().toString();
        Log.e(TAG, "register: " + fullPhone);
        startPhoneNumberVerification(fullPhone);


    }

    private void registerUserUsingEmailPassword() {
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
                                        .addOnSuccessListener(authResult -> {
                                            FirebaseDatabase.getInstance()
                                                    .getReference()
                                                    .child(Constants.APP_FIREBASE_DATABASE_REF)
                                                    .push()
                                                    .setValue(user)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task1) {
                                                            //login(null);
//                                                                sendEmailVerification();
//                                                                Toast.makeText(RegisterActivity.this, "User saved", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                            finish();

                                                        }
                                                    }).addOnFailureListener(e -> {
                                                Toast.makeText(RegisterActivity.this, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
                                                spotsDialog.dismiss();

                                            })
                                                    .addOnCanceledListener(() -> {

                                                    });
                                            sendEmailVerification();
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

        if (!TextUtils.equals(etUserPassword.getText().toString(),
                etUserConfPass.getText().toString())) {
            //start anim
            startWobble(etUserConfPass);
            etUserConfPass.setError("Password does not match");
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
        return TextUtils.isEmpty(text.toString());
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
                    etUserPhone.setError("Invalid Phone Number !");
                    Sneaker.with(RegisterActivity.this).setTitle("Invalid Phone Number !").sneakError();
                    startWobble(etUserPhone);
                    spotsDialog.dismiss();

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
                mVerificationId = verificationId;
                mResendToken = token;
                updateUI(STATE_CODE_SENT);


                Prefs.edit().putString(Constants.VERIFICATRION_ID, mVerificationId).apply();
            }
        };
    }

    public String getmVerificationId() {
        return mVerificationId;
    }

    public void setmVerificationId(String mVerificationId) {
        this.mVerificationId = mVerificationId;
    }

    public PhoneAuthProvider.ForceResendingToken getmResendToken() {
        return mResendToken;
    }

    public void setmResendToken(PhoneAuthProvider.ForceResendingToken mResendToken) {
        this.mResendToken = mResendToken;
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
