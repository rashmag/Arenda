package com.application.arenda.Ads;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.application.arenda.R;
import com.smarteist.autoimageslider.SliderView;

import java.util.List;

public class CropImages extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SliderView sliderView;
    private int[] images = {R.drawable.one,R.drawable.two,R.drawable.three};
    private SliderAdpFragment1 sliderAdpFragment1;
    private String mParam1;
    private View v;
    private String mParam2;
    private List<String> list;
    private ImageView closeCropImage;
    private int positionImage;

    public CropImages(List<String> list,int positionImage) {
        this.positionImage = positionImage;
        this.list = list;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_crop_images, container, false);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        init();
        closeCropImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LentaAds lentaAds = (LentaAds)getActivity();
                lentaAds.setSliderPosition(sliderView.getCurrentPagePosition());
            }
        });
        sliderAdpFragment1 = new SliderAdpFragment1(list,CropImages.this);
        sliderView.setSliderAdapter(sliderAdpFragment1);
        sliderView.setCurrentPagePosition(positionImage);
        // Inflate the layout for this fragment
        return v;
    }

    private void init() {
        closeCropImage = v.findViewById(R.id.closeCropImage);
        sliderView = v.findViewById(R.id.slider_view);
    }
}