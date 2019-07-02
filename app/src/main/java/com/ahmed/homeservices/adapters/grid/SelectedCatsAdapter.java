package com.ahmed.homeservices.adapters.grid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.models.Category;
import com.ahmed.homeservices.view_holders.ViewHolderCat;

import java.util.ArrayList;

public class SelectedCatsAdapter extends BaseAdapter {

    private Context mContext;
    private Category[] categories;
    private ArrayList<Category> categoryArrayList = new ArrayList<>();

    public SelectedCatsAdapter(Context context, ArrayList<Category> categoryArrayList) {
        this.mContext = context;
        this.categoryArrayList = categoryArrayList;
    }

    public SelectedCatsAdapter(Context context, Category[] categories) {
        this.mContext = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        //return categories.length;
        return categoryArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;

    }

    @Override
    public Category getItem(int position) {
        return categoryArrayList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final Category category = categories[position];
        Category category = categoryArrayList.get(position);


        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layout_selected_cat_grid_item, null);

            final ImageView imageViewIcon = convertView.findViewById(R.id.ivIcon);
            final TextView tvCatTitle = convertView.findViewById(R.id.tvTitle);


            final ViewHolderCat viewHolder = new ViewHolderCat(tvCatTitle, imageViewIcon);
            convertView.setTag(viewHolder);
        }

        final ViewHolderCat viewHolder = (ViewHolderCat) convertView.getTag();


        viewHolder.imageViewIcon.setImageResource(category.getIconResId());
//        Picasso.get().load(category.getDownloadUrl())
//                .into(viewHolder.imageViewIcon);

//        viewHolder.tvTitle.setText(mContext.getString(category.getTitle()));
        viewHolder.tvTitle.setText(category.getTitle());


        return convertView;
    }

}
