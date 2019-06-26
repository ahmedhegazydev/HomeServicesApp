package com.ahmed.homeservices.fragments;


import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.adapters.grid.CategoriesAdapter;
import com.ahmed.homeservices.fire_utils.RefBase;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.utils.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentServices extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.gridviewCategories)
    GridView gridView;
    private AlertDialog spotsDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        //ButterKnife.bind(getActivity(), view);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initVars();
//        setAdapterTo();
        fetchCatIntoGridView();
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
//        ArrayList<Category> categoryArrayList = new ArrayList<>();
//        categoryArrayList.add(new Category("Painting", R.drawable.profile));
//        categoryArrayList.add(new Category("Painting", R.drawable.profile));
//        categoryArrayList.add(new Category("Painting", R.drawable.profile));
//        categoryArrayList.add(new Category("Painting", R.drawable.profile));
//        categoryArrayList.add(new Category("Painting", R.drawable.profile));
//        categoryArrayList.add(new Category("Painting", R.drawable.profile));
//        categoryArrayList.add(new Category("Painting", R.drawable.profile));
//        categoryArrayList.add(new Category("Painting", R.drawable.profile));


//        GridView gridView = (GridView)findViewById(R.id.gridview);
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getActivity(), categoryArrayList);
        gridView.setAdapter(categoriesAdapter);
        gridView.setOnItemClickListener(this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }


}
