package com.praise.photoalbum;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.praise.photoalbum.databinding.PictureEntityBinding;

import java.net.URL;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyviewHolder> {


        ArrayList<Picture> pictures ;
        Context context;

    public CustomAdapter(ArrayList<Picture> pictures, Context context) {
        this.pictures = pictures;
        this.context = context;
    }

    @NonNull

    @Override
    public MyviewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        return new MyviewHolder((PictureEntityBinding.inflate(layoutInflater)));
    }

    @Override
    public void onBindViewHolder(@NonNull  CustomAdapter.MyviewHolder holder, int position) {
        holder.binding.pictureName.setText(pictures.get(position).name+"");
        try{
            URL imageUrl = new URL(pictures.get(position).imageUrl+"");
            Log.d("image",imageUrl+"");
            RequestOptions options = new RequestOptions().centerCrop().placeholder(R.drawable.ic_launcher_foreground).error(R.mipmap.ic_launcher);
            Glide.with(context).load(imageUrl).apply(options).into(holder.binding.img);
        }
        catch (Exception e){

        }
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    public static class  MyviewHolder extends RecyclerView.ViewHolder{

     PictureEntityBinding binding;
     MyviewHolder(PictureEntityBinding b){
         super(b.getRoot());
         binding = b;
     }
    }
}
