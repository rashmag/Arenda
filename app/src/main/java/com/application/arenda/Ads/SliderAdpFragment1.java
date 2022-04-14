package com.application.arenda.Ads;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.application.arenda.R;
import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.zolad.zoominimageview.ZoomInImageView;

import java.util.List;

public class SliderAdpFragment1 extends SliderViewAdapter<SliderAdpFragment1.Holder> {
    private List<String> list;
    private CropImages cropImages;
    public SliderAdpFragment1(List<String> list, CropImages cropImages){

        this.list = list;
        this.cropImages = cropImages;
    }
    @Override
    public SliderAdpFragment1.Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_slider_image_fragment,parent,false);
        return new SliderAdpFragment1.Holder(view);
    }


    @Override
    public void onBindViewHolder(SliderAdpFragment1.Holder viewHolder, int position) {
        Glide.with(cropImages).load(list.get(position)).into(viewHolder.imageView);
    }

    @Override
    public int getCount() {
        return list.size();
    }
    public class Holder extends SliderViewAdapter.ViewHolder{
        ZoomInImageView imageView;
        public Holder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view_crop_fragment);

        }
    }
}
