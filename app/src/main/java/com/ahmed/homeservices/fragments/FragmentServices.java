package com.ahmed.homeservices.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;
import androidx.fragment.app.Fragment;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.adapters.grid.AttachedAdapter;
import com.ahmed.homeservices.adapters.grid.CategoriesAdapter;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.models.Service;
import com.ahmed.homeservices.utils.Utils;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

//import com.esafirm.imagepicker.features.ImagePicker;
//import com.esafirm.imagepicker.model.Image;

public class FragmentServices extends Fragment implements AdapterView.OnItemClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks {

    public static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
//            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final String TAG = "FragmentServices";
    private static final int RC_CAMERA_AND_STORAGE = 121;
    // Minimal x and y axis swipe distance.
    private static int MIN_SWIPE_DISTANCE_X = 100;
    private static int MIN_SWIPE_DISTANCE_Y = 100;
    // Maximal x and y axis swipe distance.
    private static int MAX_SWIPE_DISTANCE_X = 1000;
    private static int MAX_SWIPE_DISTANCE_Y = 1000;
    @BindView(R.id.gridviewCategories)
    GridView gridView;
    GestureDetectorCompat gestureDetectorCompat;
    @BindView(R.id.expansionLayoutGridView)
    ExpansionLayout expansionLayoutGridView;
    @BindView(R.id.expansionLayoutDate)
    ExpansionLayout expansionLayoutDate;
    @BindView(R.id.expansionLayoutAttach)
    ExpansionLayout expansionLayoutAttach;
    @BindView(R.id.gridviewSelectedCats)
    GridView gridviewSelectedCats;
    @BindView(R.id.gridviewAttachedPhotos)
    GridView gridviewAttachedPhotos;
    @BindView(R.id.etDatePicker)
    TextInputEditText etDatePicker;
    @BindView(R.id.etTimePicker)
    TextInputEditText etTimePicker;
    Context context;
    StorageReference ref = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Calendar now;
    private ArrayList<Category> categories = new ArrayList<>();
    // Source activity that display message in text view.
    private AlertDialog spotsDialog;
    private TimePickerDialog tpd;
    private ArrayList<Uri> uris = new ArrayList<>();

    @OnClick(R.id.sampleHeader)
    public void sampleHeader(View v) {
        expansionLayoutGridView.expand(true);
        expansionLayoutDate.expand(false);
        Log.e(TAG, "sampleHeader: ");
    }

    @OnClick(R.id.btnOrderNow)
    public void btnOrderNow(View v) {

        if (etDatePicker.getText().toString().length() == 0) {
            if (expansionLayoutDate != null) {
                expansionLayoutDate.expand(true);
                return;
            }
        }
        if (etTimePicker.getText().toString().length() == 0) {
            if (expansionLayoutDate != null) {
                expansionLayoutDate.expand(true);
                return;
            }
        }

        Service service = new Service();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null)
            return;

        RefBase.refSendRequest(firebaseUser.getUid())
                .setValue(service)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Request sent", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed Request Sent", Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @OnClick(R.id.btnUploadPhoto)
    public void btnUploadPhoto(View v) {
//        Toast.makeText(context, "upload" +
//                "", Toast.LENGTH_SHORT).show();
        requestCamAndStoragePerms();
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        //ButterKnife.bind(getActivity(), view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVars();
        setAdapterTo(null);
//        fetchCatIntoGridView();
//        passScrollFromExpantionToGridView();
        intiVars();

    }

    private void intiVars() {

        etDatePicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    now = Calendar.getInstance();
                    DatePickerDialog dpd = DatePickerDialog.newInstance(
                            FragmentServices.this::onDateSet,
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                    );
// If you're calling this from a support Fragment
                    dpd.show(getFragmentManager(), "Datepickerdialog");
// If you're calling this from an AppCompatActivity
// dpd.show(getSupportFragmentManager(), "Datepickerdialog");
                }
            }
        });
        etTimePicker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    now = Calendar.getInstance();
                    TimePickerDialog dpd = TimePickerDialog.newInstance(
                            FragmentServices.this::onTimeSet,
                            now.get(Calendar.YEAR), // Initial year selection
                            now.get(Calendar.MONTH), // Initial month selection
                            false
                    );
// If you're calling this from a support Fragment
                    dpd.show(getFragmentManager(), "TimePickerDialog");
// If you're calling this from an AppCompatActivity
// dpd.show(getSupportFragmentManager(), "Datepickerdialog");
                }
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
//        etDatePicker.setText(date);

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        String strDate = format.format(now.getTime());
        etDatePicker.setText(strDate);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void passScrollFromExpantionToGridView() {
        // Create a common gesture listener object.
//        DetectSwipeGestureListener gestureListener = new DetectSwipeGestureListener();
        // Set activity in the listener.
//        gestureListener.setActivity(this);
        // Create the gesture detector with the gesture listener.
//        gestureDetectorCompat = new GestureDetectorCompat(getActivity(), gestureListener);
        //=============================================================
        expansionLayoutGridView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void fetchCatIntoGridView() {
        spotsDialog.show();
        RefBase.refCategoriesForService()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            ArrayList<Category> categoryArrayList = new ArrayList<>();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                categoryArrayList.add(dataSnapshot1.getValue(Category.class));
                            }
                            setAdapterTo(categoryArrayList);
                            spotsDialog.dismiss();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void initVars() {
        spotsDialog = Utils.getInstance().pleaseWait(getActivity());
    }

    private void setAdapterTo(ArrayList<Category> categoryArrayList) {
//        Category[] categories = new Category[10];
//        ArrayList<Category> categories = new ArrayList<>();
        categories.clear();
        categories.add(new Category("AC", R.drawable.ac));
        categories.add(new Category("Electrician", R.drawable.electrician));
        categories.add(new Category("Plumber", R.drawable.plumber));
        categories.add(new Category("Carpenter", R.drawable.hammer));
        categories.add(new Category("TV", R.drawable.tv));
        categories.add(new Category("Refrigerator", R.drawable.refrigerator));
        categories.add(new Category("Appliances", R.drawable.appliances));
        categories.add(new Category("RO", R.drawable.ro));
        categories.add(new Category("Computers", R.drawable.computers));
        categories.add(new Category("Mobile", R.drawable.mobile));
        categories.add(new Category("Home Secure", R.drawable.home_secure));
        categories.add(new Category("Pests Control", R.drawable.peast_control));
        categories.add(new Category("Car Wash", R.drawable.car_wash));
        categories.add(new Category("Cleaning", R.drawable.cleaning));
        categories.add(new Category("Painting", R.drawable.painting));
        categories.add(new Category("Washing Machine", R.drawable.car_wash));
        categories.add(new Category("Packers Movers", R.drawable.furniture));
        categories.add(new Category("Laundry", R.drawable.laundry));


//        GridView gridView = (GridView)findViewById(R.id.gridview);
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getActivity(), categories);
        gridView.setAdapter(categoriesAdapter);
        gridView.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.e(TAG, "onItemClick: " + categories.get(position).getTitle());


    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
//        etTimePicker.setText( "" + hourOfDay + ":" + minute);

        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        String am_pm = null;
        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
            am_pm = "PM";

        String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
        etTimePicker.setText(strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm);


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
        if (EasyPermissions.hasPermissions(getActivity(), PERMISSIONS)) {
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

    private void updateProfilePhoto() {
        @SuppressLint("ResourceType") BottomSheetMenuDialog dialog =
                new BottomSheetBuilder(getActivity()
                        , null)
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
                                    EasyImage.openCamera(Objects.requireNonNull(FragmentServices.this), 0);
//                                    ImagePicker.cameraOnly().start(FragmentServices.this); // Could be Activity, Fragment, Support Fragment
                                    break;
                                case R.id.chooseFromGellery:
                                    EasyImage.openGallery(Objects.requireNonNull(FragmentServices.this), 0);
//                                    ImagePicker.create(FragmentServices.this) // Activity or Fragment
//                                            .start();
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
//            // Get a list of picked images
//            List<Image> images = ImagePicker.getImages(data);
//            // or get a single image only
//            Image image = ImagePicker.getFirstImageOrNull(data);
////            Uri uri = Uri.fromFile(image.getPath());
//            setAdapterToGridViewAtachedPhoto(Uri.parse(image.getPath()));
//            Log.e(TAG, "onActivityResult: ");
//        }
        super.onActivityResult(requestCode, resultCode, data);
        EasyImage.handleActivityResult(requestCode,
                resultCode,
                data,
                getActivity()
//                FragmentServices.this
                , new DefaultCallback() {
                    @Override
                    public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                        Uri uri = Uri.fromFile(imageFile);
                        setAdapterToGridViewAtachedPhoto(uri);
                        Log.e(TAG, "onImagePicked: ");
//                Picasso.get().load(uri)
//                        .into(ivUserPhoto, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
//                            }
//
//                            @Override
//                            public void onError(Exception e) {
//
//                            }
//                        });
//                uploadPhoto(uri);
                    }
                });
    }

    private void setAdapterToGridViewAtachedPhoto(Uri uri) {
        gridviewAttachedPhotos.setVisibility(View.VISIBLE);
        uris.add(uri);
        AttachedAdapter attachedAdapter = new AttachedAdapter(getActivity(), uris);
        gridviewAttachedPhotos.setAdapter(attachedAdapter);
        attachedAdapter.notifyDataSetChanged();
        expansionLayoutAttach.expand(true);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);


    }

    @Override
    public void onStart() {
        super.onStart();
        createUserForTesting();
    }

    private void createUserForTesting() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("wowrar1234@gmail.com", "1234567")
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(FragmentServices.this.getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();


                        }

                    }
                });
    }

    public class DetectSwipeGestureListener extends GestureDetector.SimpleOnGestureListener {

        // Minimal x and y axis swipe distance.
        private int MIN_SWIPE_DISTANCE_X = 100;
        private int MIN_SWIPE_DISTANCE_Y = 100;

        // Maximal x and y axis swipe distance.
        private int MAX_SWIPE_DISTANCE_X = 1000;
        private int MAX_SWIPE_DISTANCE_Y = 1000;

        // Source activity that display message in text view.
        private FragmentServices activity = null;


        /* This method is invoked when a swipe gesture happened. */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // Get swipe delta value in x axis.
            float deltaX = e1.getX() - e2.getX();

            // Get swipe delta value in y axis.
            float deltaY = e1.getY() - e2.getY();

            // Get absolute value.
            float deltaXAbs = Math.abs(deltaX);
            float deltaYAbs = Math.abs(deltaY);

            // Only when swipe distance between minimal and maximal distance value then we treat it as effective swipe
            if ((deltaXAbs >= MIN_SWIPE_DISTANCE_X) && (deltaXAbs <= MAX_SWIPE_DISTANCE_X)) {
                if (deltaX > 0) {
//                this.activity.displayMessage("Swipe to left");
                } else {
//                this.activity.displayMessage("Swipe to right");
                }
            }

            if ((deltaYAbs >= MIN_SWIPE_DISTANCE_Y) && (deltaYAbs <= MAX_SWIPE_DISTANCE_Y)) {
                if (deltaY > 0) {
//                this.activity.displayMessage("Swipe to up");
                } else {
//                this.activity.displayMessage("Swipe to down");
                }
            }


            return true;
        }

        // Invoked when single tap screen.
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
//        this.activity.displayMessage("Single tap occurred.");
            return true;
        }

        // Invoked when double tap screen.
        @Override
        public boolean onDoubleTap(MotionEvent e) {
//        this.activity.displayMessage("Double tap occurred.");
            return true;
        }

    }

    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }
    }


}
