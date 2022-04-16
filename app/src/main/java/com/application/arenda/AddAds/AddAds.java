package com.application.arenda.AddAds;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.application.arenda.MainActivity;
import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class AddAds extends Fragment {
    private ProgressBar progressBar;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ModelAll modelAll, modelAll2;
    private ModelAll modelAuth;
    private String currentPrice;
    private EditText edTarifHour,edTarifDay;
    private RecyclerView recyclerView, arrayPriceRecyclerView;
    private MultiSelectImageViewHolder multiSelectImageViewHolder;
    private MultiPriceViewHolder multiPriceViewHolder;
    private StorageReference storageReference, storageReferenceMulti;
    private DatabaseReference databaseReference, reference1;
    private String firebaseAuth;
    private String push;
    private List<Uri> uriListSend;
    private List<ModelPrice> modelPrices;
    private static final int PICK_VIDEO = 1;
    private String mParam1;
    private double latInt, lonInt;
    private View v;
    private UploadTask uploadTask;
    private TextView sendAds, btnSelectedImages;
    private EditText photoName, textViewDirection;
    private String mParam2;
    private TextView addAddress, tvAddress,btnPrice;
    private String selectTimeSeekBar = "Ч", time;


    public AddAds() {
        // Required empty public constructor
    }

    public static AddAds newInstance(String param1, String param2) {
        AddAds fragment = new AddAds();
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
        v = inflater.inflate(R.layout.fragment_add_ads, container, false);
        getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.fragment_white));
        //Инициализируем
        init();
        modelPrices = new ArrayList<>();
        multiPriceViewHolder = new MultiPriceViewHolder(getActivity(), false);
        setAddPrice(modelPrices);

        MainActivity ma = (MainActivity) this.getActivity();
        if (ma.placeIntent != null) {
            tvAddress.setText(ma.placeIntent);
        }
        if (ma.latIntent != 0 && ma.lonIntent != 0) {
            latInt = ma.latIntent;
            lonInt = ma.lonIntent;
        }
        multiSelectImageViewHolder = new MultiSelectImageViewHolder(getActivity());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setFocusable(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(multiSelectImageViewHolder);

        LinearLayoutManager linearLayoutManagerPrice = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        arrayPriceRecyclerView.setLayoutManager(linearLayoutManagerPrice);
        arrayPriceRecyclerView.setFocusable(false);
        arrayPriceRecyclerView.setHasFixedSize(true);
        arrayPriceRecyclerView.setAdapter(multiPriceViewHolder);

        modelAll = new ModelAll();
        modelAuth = new ModelAll();
        storageReference = FirebaseStorage.getInstance().getReference("Photo");
        storageReferenceMulti = FirebaseStorage.getInstance().getReference("multiPhoto");

        databaseReference = FirebaseDatabase.getInstance().getReference("Post");
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            firebaseAuth = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        reference1 = FirebaseDatabase.getInstance().getReference("Users").
                child(firebaseAuth);
        btnSelectedImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPermission();
            }
        });
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapsActivityAddAddress.class);
                intent.putExtra("parent", "addAds");
                startActivity(intent);
            }
        });

        sendAds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(photoName.getText().toString())) {
                    photoName.setError("Введите название");
                } else if (TextUtils.isEmpty(addAddress.getText().toString())) {
                    addAddress.setError("Введите адрес");
                } else if (TextUtils.isEmpty(textViewDirection.getText().toString())) {
                    addAddress.setError("Опишите товар");
                } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    UploadVideo();
                } else {
                    Toast.makeText(getActivity(), "Ошибка.Вы не зарегестрированы", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Inflate the layout for this fragment
        return v;
    }

    private void setAddPrice(List<ModelPrice> modelPrices) {
        btnPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edTarifHour.getText().toString())){
                    ModelPrice modelPrice = new ModelPrice();
                    modelPrice.setPrice(edTarifHour.getText().toString());
                    modelPrice.setTime("₽ в час");
                    modelPrices.add(modelPrice);
                }else if(!TextUtils.isEmpty(edTarifDay.getText().toString())){
                    ModelPrice modelPrice = new ModelPrice();
                    modelPrice.setPrice(edTarifDay.getText().toString());
                    modelPrice.setTime("₽ за день");
                    modelPrices.add(modelPrice);
                }

                if (modelPrices != null) {
                    multiPriceViewHolder.setData(modelPrices);
                }
            }
        });
    }
//    private void UploadVideo() {
//        String videoNameEd = photoName.getText().toString();
//        String search = photoName.getText().toString().toLowerCase();
//        if (videoUri != null || !TextUtils.isEmpty(videoNameEd)) {
//            progressBar.setVisibility(View.VISIBLE);
//            final StorageReference reference = storageReference.child(System.currentTimeMillis()
//                    + "." + getExt(videoUri));
//            uploadTask = reference.putFile(videoUri);
//
//            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                @Override
//                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                    if (!task.isSuccessful()) {
//                        throw task.getException();
//                    }
//
//                    return reference.getDownloadUrl();
//                }
//            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Uri> task) {
//                            if (task.isSuccessful()) {
//                                String push = databaseReference.push().getKey();
//                                HashMap<String,Object> hashMap = new HashMap<>();
//
//                                Uri downloadUri = task.getResult();
//                                progressBar.setVisibility(View.GONE);
//                                modelLenta.setAdsurl(downloadUri.toString());
//                                    modelLenta.setName(videoNameEd);
//                                    modelLenta.setSearch(search);
//                                    modelLenta.setPostId(push);
//                                    modelLenta.setLat(latInt);
//                                    modelLenta.setLon(lonInt);
//                                    modelLenta.setAddress(addAddress.getText().toString());
//                                    modelLenta.setDirection(textViewDirection.getText().toString());
//                                    modelLenta.setPublisher(firebaseAuth);
//                                    hashMap.put("adsPhotoUri", downloadUri.toString());
//                                    reference1.updateChildren(hashMap);
//
//                                databaseReference.child(push).setValue(modelLenta);
//
//
//                            } else {
//                                progressBar.setVisibility(View.GONE);
//                                Toast.makeText(getActivity(), "Ошибка = " + task.getException(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        } else {
//            Toast.makeText(getActivity(), "All Fields are required", Toast.LENGTH_SHORT).show();
//        }
//        }

    private void UploadVideo() {
        push = databaseReference.push().getKey();

        String videoNameEd = photoName.getText().toString();
        String search = photoName.getText().toString().toLowerCase();
        progressBar.setVisibility(View.VISIBLE);
        modelAll.setName(videoNameEd);
        modelAll.setSearch(search);
        modelAll.setPostId(push);
        modelAll.setLat(latInt);
        modelAll.setLon(lonInt);
        modelAll.setAddress(tvAddress.getText().toString());
        modelAll.setDirection(textViewDirection.getText().toString());
        modelAll.setPublisher(firebaseAuth);
        databaseReference.child(push).setValue(modelAll).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < modelPrices.size(); i++) {
                        ModelPrice modelPrice = modelPrices.get(i);
                        databaseReference.child(push)
                                .child("arrayprice").child(i + "").setValue(modelPrice).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Цена"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });
        for (int i = 0; i < uriListSend.size(); i++) {
            Uri uri = uriListSend.get(i);
            final StorageReference reference = storageReferenceMulti.child(System.currentTimeMillis()
                    + "." + getExt(uri));
            uploadTask = reference.putFile(uri);
            int finalI = i;
            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        Uri downloadUri = task.getResult();
                        progressBar.setVisibility(View.GONE);
                        hashMap.put("adsPhotoUri", downloadUri.toString());
                        reference1.updateChildren(hashMap);
                        modelAll2 = new ModelAll();
                        modelAll2.setAdsurl(downloadUri.toString());
                        databaseReference.child(push)
                                .child("arrayimagesurl")
                                .child(finalI + "")
                                .setValue(modelAll2).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "" + finalI
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void requestPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                openBorromPicker();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }


        };

        TedPermission.with(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void openBorromPicker() {
        TedBottomPicker.with(getActivity())
                .setPeekHeight(1600)
                .showTitle(false)
                .setSelectMaxCount(6)
                .setSelectMaxCountErrorText("Больше 6 нельзя :(")
                .setCompleteButtonText("Готово")
                .setEmptySelectionText("Не выбрано")
                .showMultiImage(new TedBottomSheetDialogFragment.OnMultiImageSelectedListener() {
                    @Override
                    public void onImagesSelected(List<Uri> uriList) {
                        if (uriList != null && !uriList.isEmpty()) {
                            uriListSend = uriList;
                            multiSelectImageViewHolder.setData(uriList);
                            if (uriList.size() != 0) {
                                btnSelectedImages.setText("Загружить еще");
                            } else {
                                btnSelectedImages.setText("Выбрать");
                            }
                        }
                        // here is selected image uri list
                    }
                });
    }


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == PICK_VIDEO) {
//            if (resultCode == -1) {
//                try {
//                    videoUri = data.getData();
//                    imageAds.setImageURI(videoUri);
//                } catch (Exception e) {
//                    Toast.makeText(getActivity(), "Файл не выбран", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    private void init() {
        arrayPriceRecyclerView = v.findViewById(R.id.arrayPriceRVAddAds);
        recyclerView = v.findViewById(R.id.rv_photo);
        textViewDirection = v.findViewById(R.id.textViewDirection);
        progressBar = v.findViewById(R.id.progressBar_main);
        sendAds = v.findViewById(R.id.sendAds);
        photoName = v.findViewById(R.id.photoName);
        edTarifDay = v.findViewById(R.id.ed_tarif_day);
        btnPrice = v.findViewById(R.id.btn_price);
        edTarifHour = v.findViewById(R.id.ed_tarif_hour);
        addAddress = v.findViewById(R.id.addAddress);
        tvAddress = v.findViewById(R.id.tv_address);
        btnSelectedImages = v.findViewById(R.id.btnSelectedImages);
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}