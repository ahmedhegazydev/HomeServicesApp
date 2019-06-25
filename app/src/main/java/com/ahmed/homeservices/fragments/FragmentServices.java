package com.ahmed.homeservices.fragments;


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
import com.ahmed.homeservices.models.Category;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentServices extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.gridviewCategories)
    GridView gridView;

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
        initGridViewCategories();
    }

    private void initGridViewCategories() {
//        Category[] categories = new Category[10];
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        categoryArrayList.add(new Category("Painting", R.drawable.profile));
        categoryArrayList.add(new Category("Painting", R.drawable.profile));
        categoryArrayList.add(new Category("Painting", R.drawable.profile));
        categoryArrayList.add(new Category("Painting", R.drawable.profile));
        categoryArrayList.add(new Category("Painting", R.drawable.profile));
        categoryArrayList.add(new Category("Painting", R.drawable.profile));
        categoryArrayList.add(new Category("Painting", R.drawable.profile));
        categoryArrayList.add(new Category("Painting", R.drawable.profile));


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
