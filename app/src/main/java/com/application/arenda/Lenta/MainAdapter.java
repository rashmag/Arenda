package com.application.arenda.Lenta;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter  extends FragmentStateAdapter {
    private ViewPager2 viewPager2;
    private MotionLayout motionLayout;
    public MainAdapter(@NonNull FragmentManager fragmentManager,
                       @NonNull Lifecycle lifecycle, ViewPager2 viewPager2,
                       MotionLayout motionLayout) {
        super(fragmentManager, lifecycle);
        this.viewPager2 = viewPager2;
        this.motionLayout = motionLayout;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new Lenta(viewPager2,motionLayout);
            case 1:
                return new SearchNearbyMapsFragment(viewPager2,motionLayout);
            default:
                return new Lenta(viewPager2,motionLayout);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
