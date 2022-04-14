package com.application.arenda.Lenta;

import android.app.Application;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.arenda.R;
import com.bumptech.glide.Glide;

public class ViewHolderLenta extends RecyclerView.ViewHolder {
    //    SimpleExoPlayer exoPlayer;
//    PlayerView playerView;
    public ImageView favoriteBtn;
    public TextView distance,timeTVLenta,priceTVLenta;
    public ViewHolderLenta(@NonNull View itemView) {
        super(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mClickListener.OnItemLongClick(view, getAdapterPosition());
                return false;
            }
        });
    }
    public void setPriceAndTime(String price, String time){
        timeTVLenta = itemView.findViewById(R.id.timeTVLenta);
        timeTVLenta.setText(time);

        priceTVLenta = itemView.findViewById(R.id.priceTVLenta);
        priceTVLenta.setText(price);
    }

    public void setImageView(Application application,String Videourl) {
        ImageView imageView = itemView.findViewById(R.id.imageView);
        Uri video = Uri.parse(Videourl);
        Glide.with(application).load(video).into(imageView);
    }
    public void setAdsDistance( String distanceStr) {
        distance = itemView.findViewById(R.id.distance);
        distance.setText(distanceStr);
    }

    public void setAdsData( String name) {
        TextView textView = itemView.findViewById(R.id.tvItemName);
        favoriteBtn = itemView.findViewById(R.id.favoriteBtn);
        textView.setText(name);
    }

    private ViewHolderLenta.ClickListener mClickListener;

    public interface ClickListener {
        void onItemClick(View view, int position);

        void OnItemLongClick(View view, int position);
    }

    public void setOnClickListener(ViewHolderLenta.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}
