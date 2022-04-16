package com.application.arenda.AddAds;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.arenda.R;

import java.io.IOException;
import java.util.List;

public class MultiPriceViewHolder extends RecyclerView.Adapter<MultiPriceViewHolder.PhotoViewHolder> {
    private Context context;
    private List<ModelPrice> mListPrice;
    private boolean closeImageHide;
    public MultiPriceViewHolder(Context context,boolean closeImageHide) {
        this.context = context;
        this.closeImageHide = closeImageHide;
    }

    public void setData(List<ModelPrice> list){
        this.mListPrice = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MultiPriceViewHolder.PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_multi_price,parent
                ,false);
        return new MultiPriceViewHolder.PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        ModelPrice modelPrice = mListPrice.get(position);
        holder.multiTimeTextView.setText(modelPrice.getPrice() + modelPrice.getTime());
        if(closeImageHide){
            holder.deletePriceImg.setVisibility(View.GONE);
        }
        holder.deletePriceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            mListPrice.remove(modelPrice);
            notifyDataSetChanged();
            }
        });
        holder.idPriceCount.setText(position+1 + "");
    }

    @Override
    public int getItemCount() {
        if(mListPrice == null){
            return 0;
        }else{
            return mListPrice.size();
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        TextView multiTimeTextView,idPriceCount;
        ImageView deletePriceImg;
        public PhotoViewHolder(View itemView){
            super(itemView);
            deletePriceImg = itemView.findViewById(R.id.deletePriceImg);
            idPriceCount = itemView.findViewById(R.id.idPriceCount);
            multiTimeTextView = itemView.findViewById(R.id.multiTimeAndPriceTextView);
        }
    }

}