package com.ahmed.homeservices.fragments;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
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
import com.ahmed.homeservices.adapters.grid.CategoriesAdapter;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.utils.Utils;
import com.github.florent37.expansionpanel.ExpansionLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FragmentServices extends Fragment implements AdapterView.OnItemClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private static final String TAG = "FragmentServices";
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
    @BindView(R.id.etDatePicker)
    TextInputEditText etDatePicker;
    @BindView(R.id.etTimePicker)
    TextInputEditText etTimePicker;
    private Calendar now;
    private ArrayList<Category> categories = new ArrayList<>();
    // Source activity that display message in text view.
    private AlertDialog spotsDialog;
    private TimePickerDialog tpd;

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


    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        //ButterKnife.bind(getActivity(), view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVars();
        setAdapterTo(null);
//        fetchCatIntoGridView();
        passScrollFromExpantionToGridView();
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
