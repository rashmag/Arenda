package com.application.arenda.User;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Profile extends Fragment {
    private TextView out, userName;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ProgressBar progressbarProfile;
    private String userPhotoUri;
    private String userId;
    private ViewHolderProfile viewHolderProfile;
    private List<ModelAll> modelAlls;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private
    DatabaseReference reference,reference1;
    private String mParam1;
    private String facebookUserId = "";
    private CircleImageView userPhoto;
    private View v;
    private DatabaseReference databaseReference;
    private String mParam2;
    private FirebaseUser firebaseUser;
    private SharedPreferences sharedPreferences;
    private String providerAuth;
    private GoogleSignInClient googleSignInClient;

    public Profile() {
        // Required empty public constructor
    }

    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
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
        v = inflater.inflate(R.layout.fragment_profile, container, false);
        // Inflate the layout for this fragment
        init();
        //Google
        googleSignInClient = GoogleSignIn.getClient(getActivity(),
                GoogleSignInOptions.DEFAULT_SIGN_IN);
        sharedPreferences =
                getActivity().getSharedPreferences("Auth", Context.MODE_PRIVATE);
        // Inflate the layout for this fragment
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        modelAlls = new ArrayList<>();
        viewHolderProfile = new ViewHolderProfile(Profile.this, modelAlls, userId);
        recyclerView.setAdapter(viewHolderProfile);
        database = FirebaseDatabase.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference1 = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        databaseReference = database.getReference("Users");
        myAds();
        providerAuth = sharedPreferences.getString("providerAuth", "email");

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                intent.putExtra("userName", userName.getText().toString());
                intent.putExtra("userPhotoUri", userPhotoUri);
                startActivity(intent);
            }
        });
        out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }
                        }
                    });
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();

                    Fragment selectedFragment = new ProfileAuth();
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).
                            replace(R.id.fragmentContainer,
                                    selectedFragment).commit();
                }
            }
        });
        uploadUserInfo();
        return v;
    }

    private void uploadUserInfo() {
        if (providerAuth.equals("facebook")) {
            String name = firebaseUser.getDisplayName();
            userName.setText(name);
            for (UserInfo profile : firebaseUser.getProviderData()) {
                // check if the provider id matches "facebook.com"
                if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    facebookUserId = profile.getUid();
                }
            }
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ModelAll modelAuth = snapshot1.getValue(ModelAll.class);
                        if (getActivity() == null) {
                            return;
                        }

                        if (modelAuth.getId() != null) {
                            if (modelAuth.getId().equals(userId)) {
                                if(modelAuth.getUserPhotoUri() == null){
                                    userPhotoUri = "https://graph.facebook.com/" +
                                            facebookUserId + "/picture?height=500";
                                    Glide.with(getActivity()).load(userPhotoUri).into(userPhoto);
                                    Log.d("testAuth","getUserPhotoUri == null");
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("userPhotoUri", userPhotoUri);
                                    reference1.updateChildren(hashMap);
                                }else{
                                    Log.d("testAuth","getUserPhotoUri != null");
                                    Glide.with(getActivity()).load(modelAuth.getUserPhotoUri())
                                            .into(userPhoto);
                                }
                                if(modelAuth.getUserName() == null) {
                                    Log.d("testAuth","getUserName == null");
                                    String name = firebaseUser.getDisplayName();
                                    userName.setText(name);
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("userName", name);
                                    reference1.updateChildren(hashMap);
                                }else{
                                    Log.d("testAuth","getUserName != null");
                                    userName.setText(modelAuth.getUserName());
                                }
                            }else{
                                String name = firebaseUser.getDisplayName();
                                userPhotoUri = "https://graph.facebook.com/" +
                                        facebookUserId + "/picture?height=500";
                                Glide.with(getActivity()).load(userPhotoUri).into(userPhoto);
                                userName.setText(name);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("userName", name);
                                hashMap.put("id", userId);
                                hashMap.put("search", name);
                                hashMap.put("userPhotoUri", userPhotoUri);
                                reference1.updateChildren(hashMap);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if(providerAuth.equals("google")){
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ModelAll modelAuth = snapshot1.getValue(ModelAll.class);
                        if (getActivity() == null) {
                            return;
                        }

                        if (modelAuth.getId() != null) {
                            if (modelAuth.getId().equals(userId)) {
                                if(modelAuth.getUserPhotoUri() == null){
                                    Log.d("testAuth","getUserPhotoUri == null");
                                    userPhotoUri = firebaseUser.getPhotoUrl().toString();
                                    Glide.with(getActivity()).load(userPhotoUri).into(userPhoto);
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("userPhotoUri", userPhotoUri);
                                    reference1.updateChildren(hashMap);
                                }else{
                                    Log.d("testAuth","getUserPhotoUri != null");
                                    Glide.with(getActivity()).load(modelAuth.getUserPhotoUri())
                                            .into(userPhoto);
                                }
                                if(modelAuth.getUserName() == null) {
                                    Log.d("testAuth","getUserName == null");
                                    String name = firebaseUser.getDisplayName();
                                    userName.setText(name);
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("userName", name);
                                    reference1.updateChildren(hashMap);
                                }else{
                                    Log.d("testAuth","getUserName != null");
                                        userName.setText(modelAuth.getUserName());
                                }
                            }else{
                                String name = firebaseUser.getDisplayName();
                                userPhotoUri = firebaseUser.getPhotoUrl().toString();
                                Glide.with(getActivity()).load(userPhotoUri).into(userPhoto);
                                userName.setText(name);
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("userName", name);
                                hashMap.put("id", userId);
                                hashMap.put("search", name);
                                hashMap.put("userPhotoUri", userPhotoUri);
                                reference1.updateChildren(hashMap);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if(providerAuth.equals("password")){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        ModelAll modelAuth = snapshot1.getValue(ModelAll.class);
                        if (getActivity() == null) {
                            return;
                        }

                        if (modelAuth.getId() != null) {
                            if (modelAuth.getId().equals(userId)) {
                                if (modelAuth.getUserPhotoUri() != null) {
                                    userPhotoUri = modelAuth.getUserPhotoUri();
                                    Glide.with(getActivity().getApplicationContext()).load(userPhotoUri)
                                            .apply(new RequestOptions().placeholder(R.drawable.photo_loading)).into(userPhoto);
                                }
                                if(modelAuth.getUserName() != null) {
                                    userName.setText(modelAuth.getUserName());
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void init() {
        userName = v.findViewById(R.id.userNameProfile);
        userPhoto = v.findViewById(R.id.userPhotoPtofile);
        progressbarProfile = v.findViewById(R.id.progressbarProfile);
        out = v.findViewById(R.id.out);
        recyclerView = v.findViewById(R.id.recyclerView);
    }

    private void myAds() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelAlls.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelAll modelAll = snapshot1.getValue(ModelAll.class);
                    if (FirebaseAuth.getInstance().getCurrentUser().getUid() != null) {
                        if (modelAll.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            modelAlls.add(modelAll);
                            progressbarProfile.setVisibility(View.GONE);
                        } else {
                            progressbarProfile.setVisibility(View.GONE);
                        }
                    }
                    Collections.reverse(modelAlls);
                    viewHolderProfile.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}