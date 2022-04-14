package com.application.arenda.Ads;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.arenda.R;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.List;

public class SliderAdpActivity extends SliderViewAdapter<SliderAdpActivity.Holder> {

    private List<String> list;
    private LentaAds lentaAds;
    public SliderAdpActivity(List<String> list, LentaAds lentaAds){
        this.list = list;
        this.lentaAds = lentaAds;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider_image_activity,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Glide.with(lentaAds).load(list.get(position)).into(viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lentaAds.callFramentCropImages(position);
            }
        });
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public class Holder extends SliderViewAdapter.ViewHolder{
        ImageView imageView;
        public Holder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view_crop_activity);
            
        }
    }
}
