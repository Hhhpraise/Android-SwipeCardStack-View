package com.praise.photoalbum;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {


    private List<ItemModel> itemModels;
    Context context;

    public CardStackAdapter(List<ItemModel> itemModels, Context context) {
        this.itemModels = itemModels;
        this.context = context;
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setData(itemModels.get(position));
        holder.imageView.setSaturation(1);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                holder.imageView.setSaturation(0);
                return false;
            }
        });
        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadUrl("Photo Album",itemModels.get(position).getImage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView name, id;
        ImageFilterView imageView;
        ImageView downloadBtn;

            public ViewHolder(@NonNull  View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.item_day);
                id = itemView.findViewById(R.id.item_age);
                imageView = itemView.findViewById(R.id.item_image);
                downloadBtn = itemView.findViewById(R.id.download);
            }
        void setData(ItemModel data) {
            Picasso.get()
                    .load(data.getImage())
                    .fit()
                    .centerCrop()
                    .into(imageView);
            name.setText(data.getName());
            id.setText(data.getId()+" Likes");

        }
        }
    public List<ItemModel> getItems() {
        return itemModels;
    }
    public void setItems(List<ItemModel> items) {
        this.itemModels = items;
    }

    void  downloadUrl(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toast.makeText(context.getApplicationContext(), "Downloading.", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context.getApplicationContext(), "Image download failed.", Toast.LENGTH_SHORT).show();
        }
    }


}
