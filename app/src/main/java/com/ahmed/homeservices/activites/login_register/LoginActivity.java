package com.ahmed.homeservices.activites.login_register;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.activites.home.MainActivity;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.customfonts.EditText_Roboto_Regular;
import com.ahmed.homeservices.customfonts.MyTextView_Roboto_Regular;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.irozon.sneaker.Sneaker;
import com.jgabrielfreitas.core.BlurImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.supercharge.shimmerlayout.ShimmerLayout;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 10212;
    private static final int STATE_INITIALIZED = 1;
    //    @BindView(R.id.etPhoneLogin)
//    EditText_Roboto_Regular etPhoneLogin;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;
    @BindView(R.id.BlurImageViewLogin)
    BlurImageView BlurImageView;
    @BindView(R.id.etPhoneLogin)
    EditText_Roboto_Regular etPhoneLogin;
    @BindView(R.id.ivLogo)
    ImageView ivLogo;
    @BindView(R.id.verifyEmail)
    MyTextView_Roboto_Regular verifyEmail;
    @BindView(R.id.etPassLoin)
    EditText_Roboto_Regular etPassLoin;
    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.ShimmerLayout)
    ShimmerLayout ShimmerLayout;
    @BindView(R.id.progressLogin)
    SpinKitView progressLogin;
    @BindView(R.id.login)
    MyTextView_Roboto_Regular login;
    int blurVal = 1;
    Handler handler = new Handler();
    boolean flag = true;
    boolean flag2 = true;
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
    @BindView(R.id.loginMain)
    MyTextView_Roboto_Regular loginMain;
    @BindView(R.id.llBack)
    LinearLayout llBack;
    GoogleSignInClient mGoogleSignInClient = null;
    CallbackManager mCallbackManager;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {

                Toast.makeText(LoginActivity.this, "User logged out", Toast.LENGTH_SHORT).show();

                //clear fields of profile activity here


            } else {
                loadUserProfile(currentAccessToken);
            }
        }
    };
    @BindView(R.id.countryCodePicker)
    CountryCodePicker countryCodePicker;
    FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    public static Bitmap getFacebookProfilePicture(String userID) {
        URL imageURL = null;
        try {
            //imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=normal");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.signUp)
    public void signUp(View view) {
//        finish();
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.enter, R.anim.exit);
    }

    private boolean checkInfo() {
        if (isEmpty(etPhoneLogin.getText())) {
            //start anim
            startWobble(etPhoneLogin);
            etPhoneLogin.setError("Enter email address");
            return true;
        }
        if (!Utils.getInstance().isValidEmail(etPhoneLogin.getText().toString())) {
            startWobble(etPhoneLogin);
            etPhoneLogin.setError("Enter valid email");
            return true;
        }


        if (isEmpty(etPassLoin.getText())) {
            //start anim
            startWobble(etPassLoin);
            etPassLoin.setError("Enter password");
            return true;
        }
        return false;
    }

    @OnClick(R.id.verifyEmail)
    public void verifyEmail(View view) {
        if (checkInfo()) {
            return;
        }


        spotsDialog.show();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, Constants.NETWORK_ERROR,
                                    Toast.LENGTH_SHORT).show();
                            spotsDialog.dismiss();

                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(LoginActivity.this, "Email verification sent to " +
                                            FirebaseAuth.getInstance().getCurrentUser().getEmail()
                                    , Toast.LENGTH_SHORT).show();
                            llBack.setVisibility(View.GONE);
                            loginMain.setVisibility(View.VISIBLE);
                            spotsDialog.dismiss();
                        }
                    });
        } else {

        }

    }

    @OnClick(R.id.loginMain)
    public void loginMain(View view) {
        login(null);
    }

    @OnClick(R.id.login)
    public void login(View view) {
        if (checkInfo()) {
            return;
        }


//        progressLogin.setVisibility(View.VISIBLE);
//        login.setText("");

//        fireLoginWithEmailPassword();
//        startPhoneNumberVerification(countryCodePicker.getSelectedCountryCodeWithPlus() + etPhoneLogin.getText().toString());
        fireLoginWithPhonePassword();
    }

    private void fireLoginWithPhonePassword() {

        FirebaseDatabase.getInstance().getReference(Constants.APP_FIREBASE_DATABASE_REF)
                .child(Constants.USERS)
                .equalTo(etPhoneLogin.getText().toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Log.e(TAG, "onDataChange: phone ");
                            FirebaseDatabase.getInstance().getReference(Constants.APP_FIREBASE_DATABASE_REF)
                                    .child(Constants.USERS)
                                    .equalTo(etPassLoin.getText().toString())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                Log.e(TAG, "onDataChange:  pass ");
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();

                                            } else {
                                                Toast.makeText(LoginActivity.this, Constants.INVALID_PASS, Toast.LENGTH_SHORT).show();
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Sneaker.with((Activity) getApplicationContext()).setTitle(Constants.NETWORK_ERROR).sneakError();
                                        }
                                    });
                        } else {
                            Toast.makeText(LoginActivity.this, Constants.INVALID_PHONE, Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Sneaker.with((Activity) getApplicationContext()).setTitle(Constants.NETWORK_ERROR).sneakError();
                    }
                });
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
                Log.d(TAG, "STATE_INITIALIZED");
                // Initialized state, show only the phone number field and start button
                break;
            case STATE_CODE_SENT:
                Log.d(TAG, "STATE_CODE_SENT");
//                Prefs.edit().remove(Constants.BOTTOM_SHEET_IS_SHOWN).apply();
                break;
            case STATE_VERIFY_FAILED:
                Log.d(TAG, "STATE_VERIFY_FAILED");

                break;
            case STATE_VERIFY_SUCCESS:
                Log.d(TAG, "STATE_VERIFY_SUCCESS");
                break;
            case STATE_SIGNIN_FAILED:
                Log.d(TAG, "STATE_SIGNIN_FAILED");
                // No-op, handled by sign-in check
                break;
            case STATE_SIGNIN_SUCCESS:
                Log.d(TAG, "STATE_SIGNIN_SUCCESS");
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

    //check phoneNumber is correct with it's country code then send code
    private void startPhoneNumberVerification(String phoneNumber) {
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

    private void fireLoginWithEmailPassword() {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(etPhoneLogin.getText().toString(),
                etPassLoin.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        spotsDialog.dismiss();
                        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                            if (!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Please verify your email address",
                                        Toast.LENGTH_SHORT).show();
                                //verifyEmail.setVisibility(View.VISIBLE);
                                llBack.setVisibility(View.VISIBLE);
                                loginMain.setVisibility(View.GONE);

                            } else {
                                //finish();
//                                Toast.makeText(LoginActivity.this, "Logged in success" +
//                                        "", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
//                            progressLogin.setVisibility(View.GONE);
//                            login.setText("Login");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
//                        SweetDialog.getInstance().error(LoginActivity.this, "Email or password not exist !!");
//                        progressLogin.setVisibility(View.GONE);
//                        login.setText("Login");
                        spotsDialog.dismiss();
//                        signUp(null);
                    }
                });
    }

    private void startWobble(View etUserName) {
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

    private void checkIfEmailVerified() {


    }

    @OnClick(R.id.tvForgotPassword)
    public void tvForgotPassword(View v) {
//        Toast.makeText(this, "Forgot password", Toast.LENGTH_SHORT).show();
        if (TextUtils.isEmpty(etPhoneLogin.getText().toString())) {
            startWobble(etPhoneLogin);
            etPhoneLogin.setError("Enter email address");
            return;
        }

        if (!Utils.getInstance().isValidEmail(etPhoneLogin.getText().toString())) {
            startWobble(etPhoneLogin);
            etPhoneLogin.setError("Enter valid email");
            return;
        }

        spotsDialog.show();

        FirebaseAuth.getInstance().sendPasswordResetEmail(etPhoneLogin.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        spotsDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Reset password email sent to " +
                                        etPhoneLogin.getText().toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, Constants.NETWORK_ERROR, Toast.LENGTH_SHORT).show();
                        spotsDialog.dismiss();
                    }
                });


    }

    @OnClick(R.id.loginUsingGoogle)
    public void loginUsingGoogle(View v) {
        signIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
//        callbackManager.onActivityResult(requestCode, resultCode, data);
//        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e);
                Toast.makeText(LoginActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generateKeyHash() {

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    //"com.ahmed.contactsbackup",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash", "KeyHash:" + Base64.encodeToString(md.digest(),
                        Base64.DEFAULT));
//                Toast.makeText(getApplicationContext(), Base64.encodeToString(md.digest(),
//                        Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

    }

    private void loadUserProfile(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        // Application code
                        try {
                            String firstName = object.getString("first_name");
                            String lastName = object.getString("last_name");
                            String fullName = object.getString("name");
                            String email = object.getString("email");
                            String id = object.getString("id");
//                            String gender = object.getString("gender");

//                            ivLogo.setImageBitmap(getFacebookProfilePicture(id));

                            Log.e(TAG, "onCompleted: " + firstName + " " + lastName + " " + fullName + " " + email + " "
                            );


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });


        //https://developers.facebook.com/docs/android/graph/
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,first_name,last_name,name,email,gender");
        request.setParameters(parameters);
        request.executeAsync();

    }

    public void checkLoginStatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            loadUserProfile(AccessToken.getCurrentAccessToken());
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
//        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.e(TAG, "onSuccess: ");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());

                    }
                })
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Google sign in success", Toast.LENGTH_SHORT).show();
                        // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        User user1 = new User();
                        user1.setUserName(user.getDisplayName());
                        user1.setUserEmail(user.getEmail());

//                        FirebaseDatabase.getInstance().getReference(Constant.DATABASE_ROOT_USERS)
//                                .child(user.getUid())
//                                .setValue(user1)
//                                .addOnCompleteListener(task1 -> {
//                                    if (task1.isComplete()) {
//                                        updateUI(user);
//                                    } else {
//                                        Toast.makeText(LoginActivity.this, Constant.ERROR_OCCURRES,
//                                                Toast.LENGTH_SHORT).show();
//                                    }
//                                });

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithCredential:failure", task.getException());
                        //Snackbar.make(main_container, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        Toast.makeText(LoginActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();

                    }

                });
    }

    @OnClick(R.id.loginUsingFacebook)
    public void loginUsingFaceBook(View v) {
        //        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));
//        email", "public_profile
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
//        Fabric.with(this, Crashlytics.getInstance());
        MobileAds.initialize(this, getString(R.string.AD_APP_ID));

        //replaced by meta data
//        FacebookSdk.sdkInitialize(getApplicationContext());
        //replaced by meta data
//        AppEventsLogger.activateApp(this);

        Utils.getInstance().fullScreen(this);
//        setContentView(R.layout.activity_login);
        setContentView(R.layout.activity_login_sub);
        ButterKnife.bind(this);
        init();
        startAnimLottie();
        startShimmerAnimation();
        mGoogleSignInClient = Utils.getInstance().initGoogleSignInClient(this);
        initFaceBook();
        checkUserSession();
//        generateKeyHash();
//        initFacebookLoginButton();
        checkLoginStatus();


    }

    private void checkUserSession() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
//                Toast.makeText(this, "Home Activity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }


            for (UserInfo userInfo : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (userInfo.getProviderId().equals("facebook.com")) {
                    Log.e(TAG, "User is signed in with Facebook");
                }
            }
        }
    }

    private void initFacebookLoginButton() {


        callbackManager = CallbackManager.Factory.create();

        String EMAIL = "email", publicProfile = "public_profile";

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList(EMAIL, publicProfile));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(LoginActivity.this, "Logged in success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LoginActivity.this, "Cancellled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code

                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void initFaceBook() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.e(TAG, "facebook:onSuccess:" + loginResult);
                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                //finish();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Login Canceled");
            }

            @Override
            public void onError(FacebookException exception) {
                //Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Login Failed" + exception.getMessage());
                Log.e(TAG, "facebook:onError", exception);
                Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                            updateUI(user);


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
//                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);

                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        startBluringImageView();
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

    private void startBluringImageView() {
        handler.postDelayed(runnable, 600);
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
                    etPhoneLogin.setError("Invalid Phone Number !");
                    Sneaker.with(LoginActivity.this).setTitle("Invalid Phone Number !").sneakError();
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
            }
        };
    }


}
