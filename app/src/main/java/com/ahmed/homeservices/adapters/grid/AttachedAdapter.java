package com.ahmed.homeservices.adapters.grid;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.view_holders.VHAttachedFiles;
import com.ahmed.homeservices.view_holders.ViewHolderCat;

import java.util.ArrayList;

public class AttachedAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Uri> uris = new ArrayList<>();

    public AttachedAdapter(Context context, ArrayList<Uri> uris) {
        this.mContext = context;
        this.uris = uris;
    }


    @Override
    public int getCount() {
        //return categories.length;
        return uris.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;

    }

    @Override
    public Uri getItem(int position) {
        return uris.get(position);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //final Category category = categories[position];
        Uri uri = uris.get(position);


        // view holder pattern
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.layout_attached_photos_grid_item, null);

            final ImageView imageViewIcon = convertView.findViewById(R.id.ivPhoto);

            final VHAttachedFiles viewHolder = new VHAttachedFiles(imageViewIcon);
            convertView.setTag(viewHolder);
        }

        final ViewHolderCat viewHolder = (ViewHolderCat) convertView.getTag();


        viewHolder.imageViewIcon.setImageURI(uri);


        return convertView;
    }

}
