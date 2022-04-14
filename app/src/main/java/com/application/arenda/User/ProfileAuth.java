package com.application.arenda.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.application.arenda.Auth.Auth;
import com.application.arenda.R;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileAuth extends Fragment {
    private TextView status;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button enter;
    private String mParam1;
    private FirebaseAuth firebaseAuth;
    private String mParam2;
    private View v;
    public ProfileAuth() {
    }

    public static ProfileAuth newInstance(String param1, String param2) {
        ProfileAuth fragment = new ProfileAuth();
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
        v = inflater.inflate(R.layout.fragment_profile_auth, container, false);
        //Инициализируем
        init();
        firebaseAuth = FirebaseAuth.getInstance();
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),
                        Auth.class));
            }
        });
        if (firebaseAuth.getCurrentUser() != null) {
            status.setText("Зарегестрирован");
        }else {
            status.setText("Не зарегестрирован");
        }
        // Inflate the layout for this fragment
        return v;
    }

    private void init(){
        status = v.findViewById(R.id.status);
        enter = v.findViewById(R.id.enter);
    }
}