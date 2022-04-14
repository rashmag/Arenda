package com.application.arenda.Save;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.arenda.Ads.LentaAds;
import com.application.arenda.Lenta.ViewHolderLenta;
import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.List;

public class ViewHolderSave extends RecyclerView.Adapter<ViewHolderSave.RecyclerViewHolder> {
    private View itemView;
    private Save save;
    private List<ModelAll> modelAlls;
    private String  url,publisherId;
    private int count,countPrice,kmInDec;
    String auth;

    public ViewHolderSave(Save save, List<ModelAll> modelAlls) {
        this.save = save;
        this.modelAlls = modelAlls;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, favoriteBtnSave;
        TextView tvItemNameSave;
        TextView distanceSave,priceTVSave,timeTVSave;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTVSave = itemView.findViewById(R.id.timeTVSave);
            priceTVSave = itemView.findViewById(R.id.priceTVSave);
            favoriteBtnSave = itemView.findViewById(R.id.favoriteBtnSave);
            imageView = itemView.findViewById(R.id.imageViewSave);
            tvItemNameSave = itemView.findViewById(R.id.tvItemNameSave);
            distanceSave = itemView.findViewById(R.id.distanceSave);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_save,
                parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        ModelAll modelSave = modelAlls.get(position);
        holder.tvItemNameSave.setText(modelSave.getName());
        auth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if(save.getActivity() != null) {
            String saveId = FirebaseDatabase.getInstance().
                    getReference().child("Saves").child(auth)
                    .child(modelSave.getPostId()).getKey();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().
                    getReference("Post");
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (modelSave.getPostId() != null && modelSave.getPostId().
                                equals(saveId)) {
                            if (modelSave.getLat() != 0 && modelSave.getLon() != 0) {
                                LatLng latLngStart = new LatLng(modelSave.getLat(),
                                        modelSave.getLon());
                                LatLng latLngEnd = new LatLng(42.69054264684427, 46.114416507462195);
                                CalculationByDistance(latLngStart, latLngEnd,holder);
                            }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            DatabaseReference dRefrrayImagesUrl = FirebaseDatabase.getInstance().
                    getReference("Post").
                    child(modelSave.getPostId()).child("arrayimagesurl");
            dRefrrayImagesUrl.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long value =  snapshot.getChildrenCount();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        count++;
                        if (count == 1) {
                            Glide.with(save.getContext()).load
                                    (String.valueOf(snapshot1.child("adsurl").getValue()))
                                    .into(holder.imageView);
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
            DatabaseReference dRefrrayPrice = FirebaseDatabase.getInstance().
                    getReference("Post").
                    child(modelSave.getPostId()).child("arrayprice");
            dRefrrayPrice.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long value =  snapshot.getChildrenCount();
                    String price,time;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        price = String.valueOf(snapshot1.child("price").getValue());
                        time = String.valueOf(snapshot1.child("time").getValue());
                        if(!price.equals("") && !time.equals("")){
                            countPrice++;
                            if (countPrice == 1) {
                                holder.priceTVSave.setText(price);
                                holder.timeTVSave.setText(time);
                            }
                            if(countPrice == value){
                                countPrice = 0;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        isSaved(modelSave.getPostId(), holder.favoriteBtnSave);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = modelSave.getAdsurl();
                publisherId = modelSave.getPublisher();
                Intent intent = new Intent(save.getActivity(), LentaAds.class);
                intent.putExtra("postId", modelSave.getPostId());
                intent.putExtra("publisherId", publisherId);
                save.getActivity().startActivity(intent);
            }
        });
        holder.favoriteBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.favoriteBtnSave.getTag().equals("save")) {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(auth)
                            .child(modelSave.getPostId()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance()
                            .getReference().child("Saves").child(auth)
                            .child(modelSave.getPostId()).removeValue();
                }
            }
        });
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP,RecyclerViewHolder
            holder) {
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
        Log.i("radiusSave", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        if(kmInDec == 0){
            holder.distanceSave.setText(meterInDec + " м");
        }else {
            holder.distanceSave.setText(kmInDec + " км");
        }
        return Radius * c;
    }

    @Override
    public int getItemCount() {
        return modelAlls.size();
    }

    private void isSaved(String postId, ImageView imageView) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postId).exists()) {
                    imageView.setImageResource(R.drawable.favorite);
                    imageView.setTag("saved");
                } else {
                    imageView.setImageResource(R.drawable.favorite_border);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
