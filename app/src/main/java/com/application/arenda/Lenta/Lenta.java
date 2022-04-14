package com.application.arenda.Lenta;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.application.arenda.Ads.LentaAds;
import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

public class Lenta extends Fragment {
    private SearchView searchView;
    private DatabaseReference databaseReference,dRefarrayImagesUrl,dRefarrayPrice;
    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private View v;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String  adsPhotoUrl,publisherId;
    private DatabaseReference referencePost;
    private String mParam1;
    private String mParam2;
    private ViewPager2 viewPager2;
    private int count,countPrice,kmInDec;
    private MotionLayout motionLayout;

    public Lenta(ViewPager2 viewPager2, MotionLayout motionLayout) {
        this.viewPager2 = viewPager2;
        this.motionLayout = motionLayout;
        // Required empty public constructor
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
        v = inflater.inflate(R.layout.fragment_lenta, container, false);
        init();
        // Inflate the layout for this fragment
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Post");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                firebaseSearch(newText);
                return false;
            }
        });
        return v;
    }


    private void init() {
        searchView = v.findViewById(R.id.searchView);
        recyclerView = v.findViewById(R.id.recyclerView);
    }


    private void firebaseSearch(String searchText) {
        String query = searchText.toLowerCase();
        Query firebaseQuery = databaseReference.orderByChild("search").startAt(query).
                endAt(query + "\uf8ff");

        FirebaseRecyclerOptions<ModelAll> options =
                new FirebaseRecyclerOptions.Builder<ModelAll>()
                        .setQuery(firebaseQuery, ModelAll.class)
                        .build();

        firebaseRecycler(options);
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP,ViewHolderLenta
            viewHolderLenta) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("radius", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        if(kmInDec == 0){
            viewHolderLenta.setAdsDistance(meterInDec + "м");
        }else {
            viewHolderLenta.setAdsDistance(kmInDec + " км ");
        }
        return Radius * c;
    }



    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<ModelAll> options =
                new FirebaseRecyclerOptions.Builder<ModelAll>()
                        .setQuery(databaseReference, ModelAll.class)
                        .build();

        firebaseRecycler(options);
    }

    @Override
    public void onResume() {
        super.onResume();


        Handler handlerMotion = new Handler();
        handlerMotion.removeCallbacksAndMessages(null);
        handlerMotion.postDelayed(new Runnable() {
            @Override
            public void run() {
                motionLayout.transitionToStart();
            }
        }, 100);
        Log.d("text",viewPager2.isUserInputEnabled() + "");
        viewPager2.setUserInputEnabled(true);
        Handler handler = new Handler();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!viewPager2.isUserInputEnabled()) {
                    Log.d("text",viewPager2.isUserInputEnabled() + "");
                    viewPager2.setUserInputEnabled(true);
                }
            }
        }, 500);
    }

    private void showDeleteDialog(String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Удаление");
        builder.setMessage("Вы действитеьно хотите удалить это объявление?");
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Query query = databaseReference.orderByChild("name").equalTo(name);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();
                        }
                        Toast.makeText(getActivity(), "Объявление удалено ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void isSaved(String postId, ImageView imageView){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.favorite);
                    imageView.setTag("saved");
                }else{
                    imageView.setImageResource(R.drawable.favorite_border);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void firebaseRecycler(FirebaseRecyclerOptions options){
        FirebaseRecyclerAdapter<ModelAll, ViewHolderLenta> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ModelAll, ViewHolderLenta>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ViewHolderLenta holder,
                                                    int position, @NonNull ModelAll
                                                            model) {
                        if (model.getPostId() != null) {
                            if(model.getLat() != 0 && model.getLon() != 0){
                                LatLng latLngStart = new LatLng(model.getLat(),
                                        model.getLon());
                                LatLng latLngEnd = new LatLng(42.69054264684427,46.114416507462195);
                                CalculationByDistance(latLngStart,latLngEnd,holder);
                            }
                        }
                        holder.setAdsData(model.getName());

                        count = 0;
                        dRefarrayImagesUrl = database.getReference("Post").
                                child(model.getPostId()).child("arrayimagesurl");
                        dRefarrayImagesUrl.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long value =  snapshot.getChildrenCount();
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    count++;
                                    if(count == 1){
                                        Log.d("testr3",String.valueOf(snapshot1.child("adsurl").getValue()));
                                        holder.setImageView(getActivity().getApplication(),
                                                String.valueOf(snapshot1.child("adsurl").getValue()));
                                    }
                                    if(count == value){
                                        count = 0;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Цена
                        countPrice = 0;
                        dRefarrayPrice = database.getReference("Post").
                                child(model.getPostId()).child("arrayprice");
                        dRefarrayPrice.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long value =  snapshot.getChildrenCount();
                                String price,time;
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    price = String.valueOf(snapshot1.child("price").getValue());
                                    time = String.valueOf(snapshot1.child("time").getValue());
                                    if(!price.equals("") && !time.equals("")) {
                                        countPrice++;
                                        if (countPrice == 1) {
                                            holder.setPriceAndTime(price, time);
                                        }
                                        if (countPrice == value) {
                                            countPrice = 0;
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //Избранные
                        isSaved(model.getPostId(),holder.favoriteBtn);
                        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                if (holder.favoriteBtn.getTag().equals("save")) {
                                    FirebaseDatabase.getInstance().getReference().child("Saves")
                                            .child(auth)
                                            .child(model.getPostId()).setValue("true");
                                }else{
                                    FirebaseDatabase.getInstance().getReference().child("Saves")
                                            .child(auth)
                                            .child(model.getPostId()).removeValue();
                                }
                            }
                        });
                        holder.setOnClickListener(new ViewHolderLenta.ClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                publisherId = getItem(position).getPublisher();
                                Intent intent = new Intent(getActivity(), LentaAds.class);
                                intent.putExtra("postId", model.getPostId());
                                intent.putExtra("publisherId", publisherId);
                                intent.putExtra("fragment", "Lenta");
                                startActivity(intent);
                            }

                            @Override
                            public void OnItemLongClick(View view, int position) {
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ViewHolderLenta onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_lenta, parent, false);
                        return new ViewHolderLenta(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}