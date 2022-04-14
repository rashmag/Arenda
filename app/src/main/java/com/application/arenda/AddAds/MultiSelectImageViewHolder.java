package com.application.arenda.AddAds;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.arenda.R;

import java.io.IOException;
import java.util.List;

public class MultiSelectImageViewHolder extends RecyclerView.Adapter<MultiSelectImageViewHolder.PhotoViewHolder> {
    private Context context;
    private List<Uri> mListPhotos;

    public MultiSelectImageViewHolder(Context context) {
        this.context = context;
    }

    public void setData(List<Uri> list){
        this.mListPhotos = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo,parent
                ,false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Uri uri = mListPhotos.get(position);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),
                    uri);
            holder.imgPhoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if(mListPhotos == null){
            return 0;
        }else{
            return mListPhotos.size();
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView imgPhoto;
        public PhotoViewHolder(View itemView){
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
        }
    }

}