package com.application.arenda.Lenta;

import android.os.Bundle;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.application.arenda.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class TabLenta extends Fragment {
    private TabLayout tabLayout;
    public ViewPager2 viewPager;
    private MainAdapter adapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View v;

    public TabLenta() {
    }

    public static TabLenta newInstance(String param1, String param2) {
        TabLenta fragment = new TabLenta();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        v = inflater.inflate(R.layout.fragment_tab_lenta,
                container, false);
        init();
        MotionLayout motionLayout = v.findViewById(R.id.motionLayoutTabLenta);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        adapter = new MainAdapter(fm,getLifecycle(),viewPager,motionLayout);
        viewPager.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Лента"));
        tabLayout.addTab(tabLayout.newTab().setText("Карта"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
        // Inflate the layout for this fragment
        return v;
    }


    private void init() {
        tabLayout = v.findViewById(R.id.tab_layout);
        viewPager = v.findViewById(R.id.view_pager);
    }
}