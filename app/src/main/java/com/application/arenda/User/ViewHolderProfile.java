package com.application.arenda.User;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.application.arenda.Ads.LentaAds;
import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ViewHolderProfile extends RecyclerView.Adapter<ViewHolderProfile.RecyclerViewHolder> {
    private View itemView;
    private Profile profile;
    private List<ModelAll> modelAlls;
    private Dialog dialog;
    private int count,countPrice;
    private String userId;
    private String  url,publisherId;
    private String postId;

    public ViewHolderProfile(Profile profile, List<ModelAll> modelAlls, String userId) {
        this.profile = profile;
        this.userId = userId;
        this.modelAlls = modelAlls;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvItemNameProfile,priceTVProfile,timeTVProfile;


        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTVProfile = itemView.findViewById(R.id.timeTVProfile);
            priceTVProfile = itemView.findViewById(R.id.priceTVProfile);
            imageView = itemView.findViewById(R.id.imageViewProfile);
            tvItemNameProfile = itemView.findViewById(R.id.tvItemNameProfile);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        return new RecyclerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        ModelAll modelProfile = modelAlls.get(position);
        holder.tvItemNameProfile.setText(modelProfile.getName());
        //Изображение
        count = 0;
        if(profile.getActivity() != null) {
            DatabaseReference dRefrrayImagesUrl = FirebaseDatabase.getInstance().getReference("Post").
                    child(modelProfile.getPostId()).child("arrayimagesurl");
            dRefrrayImagesUrl.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long value =  snapshot.getChildrenCount();
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        count++;
                        if (count == 1) {
                            Glide.with(profile.getContext()).load
                                    (String.valueOf(snapshot1.child("adsurl").getValue())).into(holder.imageView);
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
            //Цена
            countPrice = 0;
            DatabaseReference  dRefarrayPrice = FirebaseDatabase.getInstance().getReference("Post").
                    child(modelProfile.getPostId()).child("arrayprice");
            dRefarrayPrice.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    long value =  snapshot.getChildrenCount();
                    String price,time;
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        price = String.valueOf(snapshot1.child("price").getValue());
                        time = String.valueOf(snapshot1.child("time").getValue());
                        countPrice++;
                        if(!price.equals("") && !time.equals("")) {
                            if (countPrice == 1) {
                                holder.priceTVProfile.setText(price);
                                holder.timeTVProfile.setText(time);
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
        }
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = modelProfile.getAdsurl();
                publisherId = modelProfile.getPublisher();
                Intent intent = new Intent(profile.getActivity(), LentaAds.class);
                intent.putExtra("postId", modelProfile.getPostId());
                intent.putExtra("publisherId", publisherId);
                intent.putExtra("fragment", "Profile");
                profile.getActivity().startActivity(intent);
            }
        });
        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteDialog(modelProfile);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelAlls.size();
    }

    private void showDeleteDialog(ModelAll modelAll) {
        dialog = new Dialog(profile.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView no = dialog.findViewById(R.id.no);
        TextView yes = dialog.findViewById(R.id.yes);

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().
                        getReference().child("Post");
                Query query = reference.orderByChild("postId").equalTo(modelAll.getPostId());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                            dataSnapshot1.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
