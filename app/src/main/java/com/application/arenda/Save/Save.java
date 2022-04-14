package com.application.arenda.Save;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Save extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RecyclerView recyclerView;
    private String mParam1;
    private String mParam2;
    private View v;
    private List<ModelAll> modelLentasArray;
    private List<String> mySaves;
    private List<ModelAll> modelAlls;
    private ModelAll modelAll;
    private ViewHolderSave viewHolderSave;

    public Save() {
    }

    public static Save newInstance(String param1, String param2) {
        Save fragment = new Save();
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
        v = inflater.inflate(R.layout.fragment_save, container, false);
        //Инициализируем
        init();
        modelLentasArray = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        modelAlls = new ArrayList<>();
        viewHolderSave = new ViewHolderSave(Save.this,modelLentasArray);
        recyclerView.setAdapter(viewHolderSave);
        mysaves();
        // Inflate the layout for this fragment
        return v;
    }

    private void init(){
        recyclerView = v.findViewById(R.id.recyclerViewSave);
    }

    public void mysaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mySaves.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    mySaves.add(snapshot1.getKey());
                }
                
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readSaves() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                modelLentasArray.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    ModelAll modelAll = snapshot1.getValue(ModelAll.class);

                    for(String id : mySaves){
                        if(modelAll.getPostId().equals(id)){
                            modelLentasArray.add(modelAll);
                        }
                    }
                }
                viewHolderSave.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}