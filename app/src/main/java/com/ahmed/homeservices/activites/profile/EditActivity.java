package com.ahmed.homeservices.activites.profile;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.constants.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {


    private static final String TAG = "EditActivity";
    @BindView(R.id.toolbarEditProfile)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(view -> {
            //finish();
            onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        switch (getIntent().getStringExtra(Constants.EDIT_FIELD_TYPE)) {
            case Constants.EDIT_FULLNAME:
                Log.e(TAG, "EDIT_FULLNAME: ");


                break;
            case Constants.EDIT_PHONE_NUMBER:
                Log.e(TAG, "EDIT_PHONE_NUMBER: ");


                break;
            case Constants.EDIT_EMAIL:
                Log.e(TAG, "EDIT_EMAIL: ");

                break;
            case Constants.EDIT_PASSWORD:
                Log.e(TAG, "EDIT_PASSWORD: ");


                break;
            default:
                break;


        }

    }
}
