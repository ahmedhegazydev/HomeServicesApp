package com.ahmed.homeservices.adapters.grid;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.ahmed.homeservices.R;
import com.ahmed.homeservices.view_holders.VHAttachedFiles;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.UUID;

//import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

//import br.com.simplepass.loadingbutton.customViews.CircularProgressButton;

public class AttachedAdapter extends BaseAdapter {

    private static final String TAG = "AttachedAdapter";
    StorageReference ref = null;
    FirebaseStorage storage;
    StorageReference storageReference;
    ProgressDialog progressDialog;
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
            final FloatingActionButton fabUpload = convertView.findViewById(R.id.fabUpload);
            final FloatingActionButton fabClose = convertView.findViewById(R.id.fabClose);

            final VHAttachedFiles viewHolder = new VHAttachedFiles(imageViewIcon, fabClose, fabUpload);
            convertView.setTag(viewHolder);
        }

        final VHAttachedFiles viewHolder = (VHAttachedFiles) convertView.getTag();

        viewHolder.imageView.setImageURI(uri);
        viewHolder.fabClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fabClose:");
                uris.remove(position);
                notifyDataSetChanged();

            }
        });
        viewHolder.fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "fabUpload:");
                uploadPhoto(uris.get(position), position, view);

            }
        });
        return convertView;
    }

    private void uploadPhoto(Uri filePath, int position, View fabUpload) {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        if (filePath != null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();


            ref = storageReference.child("images/" + UUID.randomUUID().toString());
//            ref = storageReference.child("images/" + firebaseUser.getUid());

            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, "Uploaded", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener(uri -> {
//                            uris.remove(position);
//                            notifyDataSetChanged();
                            fabUpload.setEnabled(false);
                            fabUpload.setTag(uri);
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(mContext, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    });
        }

    }

}
