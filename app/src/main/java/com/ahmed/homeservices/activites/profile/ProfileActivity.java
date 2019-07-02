package com.ahmed.homeservices.activites.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class ProfileActivity extends AppCompatActivity implements
        EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {
    public static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int RC_CAMERA_AND_STORAGE = 121;
    @BindView(R.id.ivUserPhoto)
    ImageView ivUserPhoto;
    @BindView(R.id.llFullName)
    LinearLayout llFullName;
    @BindView(R.id.llEmail)
    LinearLayout llEmail;
    @BindView(R.id.llPhoneNumber)
    LinearLayout llPhoneNumber;
    @BindView(R.id.llPassword)
    LinearLayout llPassword;
    AlertDialog spotsDialog;
    @BindView(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.tvEmail)
    TextView tvEmail;
    @BindView(R.id.tvPassword)
    TextView tvPassword;
    @BindView(R.id.tvFullName)
    TextView tvFullName;
    StorageReference ref = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Intent intentEditActovoty;
    @BindView(R.id.toolbarProfile)
    Toolbar toolbar;
    @BindView(R.id.progress)
    SpinKitView progress;


    @OnClick(R.id.llFullName)
    public void llFullName(View v) {
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_FULLNAME);
        intentEditActovoty.putExtra(Constants.EDIT_FULLNAME, tvFullName.getText().toString());
        startActivity(intentEditActovoty);
    }

    @OnClick(R.id.llEmail)
    public void llEmail(View v) {
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_EMAIL);
        startActivity(intentEditActovoty);
    }

    @OnClick(R.id.llPhoneNumber)
    public void llPhoneNumber(View v) {
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_PHONE_NUMBER);
        startActivity(intentEditActovoty);
    }

    @OnClick(R.id.ivUserPhoto)
    public void ivUserPhoto(View v) {
        requestCamAndStoragePerms();
    }

    @OnClick(R.id.llPassword)
    public void llPassword(View v) {
        intentEditActovoty.putExtra(Constants.EDIT_FIELD_TYPE, Constants.EDIT_PASSWORD);
        startActivity(intentEditActovoty);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        initVars();
        setUserProfileData();
        initIntents();
        toolbar.setNavigationOnClickListener(view -> {
            //finish();
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initIntents() {
        intentEditActovoty = new Intent(this, EditActivity.class);

    }

    private void initVars() {
        spotsDialog = Utils.getInstance().pleaseWait(ProfileActivity.this);

    }

    private void setUserProfileData() {
//        spotsDialog.show();
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;
        RefBase.refUser(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            User user = dataSnapshot.getValue(User.class);

                            tvFullName.setText(user.getUserName());
                            tvEmail.setText(user.getUserEmail());
                            tvPassword.setText(user.getUserPassword());
                            tvPhoneNumber.setText(user.getUserPhoneNumber());

                            //get user photo
                            HashMap map = (HashMap<String, Object>) dataSnapshot.getValue();
                            if (map != null) {
                                if (map.get(Constants.USER_PHOTO) != null) {
                                    Picasso.get().load(map.get(Constants.USER_PHOTO).toString())
                                            .into(ivUserPhoto, new Callback() {
                                                @Override
                                                public void onSuccess() {
//                                                    Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                                    progress.setVisibility(View.GONE);

                                                }

                                                @Override
                                                public void onError(Exception e) {

                                                }
                                            });
                                }
                            }

                            spotsDialog.dismiss();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        spotsDialog.dismiss();
                    }
                });


    }

    private void updateProfilePhoto() {
        @SuppressLint("ResourceType") BottomSheetMenuDialog dialog = new BottomSheetBuilder(this, null)
                .setMode(BottomSheetBuilder.MODE_LIST)
//                .setMode(BottomSheetBuilder.MODE_GRID)
                .addDividerItem()
                .expandOnStart(true)
                .setDividerBackground(R.color.grey_400)
                .setBackground(R.drawable.ripple_grey)
                .setMenu(R.menu.menu_image_picker)
                .setItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.chooseFromCamera:
                            //EasyImage.openChooserWithGallery(getApplicationContext(), "Ch", int type);
                            EasyImage.openCamera(ProfileActivity.this, 0);
                            break;
                        case R.id.chooseFromGellery:
                            EasyImage.openGallery(ProfileActivity.this, 0);
                            break;
//                        case R.id.removePhoto:
//                            removePhoto();
//                            break;
                    }
                })
                .createDialog();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {

                Uri uri = Uri.fromFile(imageFile);
                Picasso.get().load(uri)
                        .into(ivUserPhoto, new Callback() {
                            @Override
                            public void onSuccess() {
                                Toast.makeText(ProfileActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                uploadPhoto(uri);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);


    }

    private void uploadPhoto(Uri filePath) {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        if (filePath != null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            ref = storageReference.child("images/" + UUID.randomUUID().toString());
//            ref = storageReference.child("images/" + firebaseUser.getUid());

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
                            //Log.d(TAG, "onSuccess: uri= "+ uri.toString());

//                                User user = new User();
//                                user.setUserImageProfile(uri.toString());
//                                user.setPassword(etEnterNewPassword.getText().toString());
//                                user.setUserEmail(firebaseUser.getEmail());
//                                user.setUserName(firebaseUser.getDisplayName());


                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            if (firebaseUser == null)
                                return;
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            RefBase.refUser(firebaseUser.getUid())
//                            DatabaseReference databaseReference = firebaseDatabase.getReferee(Constants.DATABASE_ROOT_USERS);
//                            databaseReference.child(firebaseUser.getUid())
                                    .child(Constants.USER_PHOTO)
                                    //.setValue(user)
                                    .setValue(uri.toString())
                                    .addOnCompleteListener(task -> {
                                        if (task.isComplete()) {
                                            Toast.makeText(ProfileActivity.this, "Photo updated",
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), Constants.NETWORK_ERROR,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        if (spotsDialog != null)
                                            spotsDialog.dismiss();

                                    });

                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        updateProfilePhoto();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRationaleAccepted(int requestCode) {
//        updateProfilePhoto();
    }

    @Override
    public void onRationaleDenied(int requestCode) {


    }

    @AfterPermissionGranted(RC_CAMERA_AND_STORAGE)
    private void requestCamAndStoragePerms() {
        if (EasyPermissions.hasPermissions(this, PERMISSIONS)) {
            // Already have permission, do the thing
            updateProfilePhoto();
        } else {
            // Do not have permissions, request them now
//            EasyPermissions.requestPermissions(this, getString(R.string.contacts_and_storage_rationale),
//                    RC_CONTACT_AND_STORAGE, perms);
            EasyPermissions.requestPermissions(
                    new PermissionRequest.Builder(this, RC_CAMERA_AND_STORAGE, PERMISSIONS)
                            .setRationale(R.string.cam_and_storage_rationale)
                            .setPositiveButtonText(R.string.rationale_ask_ok)
                            .setNegativeButtonText(R.string.rationale_ask_cancel)
//                            .setTheme(R.style.AppTheme)
                            .build());

        }
    }

}
