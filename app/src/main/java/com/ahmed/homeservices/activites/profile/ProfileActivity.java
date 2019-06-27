package com.ahmed.homeservices.activites.profile;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.User;
import com.ahmed.homeservices.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity {
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

    @OnClick(R.id.llFullName)
    public void llFullName(View v) {

    }

    @OnClick(R.id.llEmail)
    public void llEmail(View v) {

    }

    @OnClick(R.id.llPhoneNumber)
    public void llPhoneNumber(View v) {

    }

    @OnClick(R.id.ivUserPhoto)
    public void ivUserPhoto(View v) {

    }

    @OnClick(R.id.llPassword)
    public void llPassword(View v) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setUserProfileData();
        initVars();
    }

    private void initVars() {
        spotsDialog = Utils.getInstance().pleaseWait(ProfileActivity.this);

    }

    private void setUserProfileData() {
        spotsDialog.show();
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

                            spotsDialog.dismiss();

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        spotsDialog.dismiss();
                    }
                });


    }


}
