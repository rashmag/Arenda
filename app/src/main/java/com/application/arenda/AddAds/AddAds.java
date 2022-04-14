package com.application.arenda.AddAds;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.arenda.Ads.MoneyTextWatcher;
import com.application.arenda.MainActivity;
import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.xw.repo.BubbleSeekBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class AddAds extends Fragment {
    private ProgressBar progressBar;
    private BubbleSeekBar seekBarBubbleTime;
    private TextInputEditText priceEditText;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ModelAll modelAll, modelAll2;
    private ModelAll modelAuth;
    private String currentPrice;
    private Button timeSeekBar;
    private TextView hoursBtnTime, daysBtnTime, weekBtnTime,
            mouthBtnTime, yearsBtnTime;
    private RecyclerView recyclerView,arrayPriceRecyclerView;
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
    private Button sendAds;
    private EditText photoName, textViewDirection;
    private String mParam2;
    private Button btnSelectedImages;
    private TextView addAddress;
    private String selectTimeSeekBar = "Ч",time;


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
        priceEditText.addTextChangedListener(new MoneyTextWatcher(priceEditText));
        modelPrices = new ArrayList<>();
//        priceEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Toast.makeText(getActivity(), "0", Toast.LENGTH_SHORT).show();
//                if (!priceEditText.getText().toString().equals("")) {
//                    priceEditText.removeTextChangedListener(this);
//                    String cleanString = priceEditText.getText().toString().replace("[Р,.]",
//                            "");
//                    BigDecimal parsed = new BigDecimal(cleanString).setScale(2,BigDecimal.ROUND_FLOOR)
//                            .divide(new BigDecimal(100),BigDecimal.ROUND_FLOOR);
////                    double parsed = Double.parseDouble(cleanString);
//                    String formatted = NumberFormat.getCurrencyInstance().format(parsed);
//                    currentPrice = formatted;
//                    priceEditText.setText(formatted);
//                    priceEditText.addTextChangedListener(this);
//                    Toast.makeText(getActivity(), "1 = ", Toast.LENGTH_SHORT).show();
////                    priceEditText.setText(priceEditText.getText().toString() + " р");
//                }
//            }
//        });
        hoursBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_check);
        hoursBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeSeekBar = "Ч";
                seekBarBubbleTime.getConfigBuilder().max(24 * 1).sectionCount(24).build();
                hoursBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_check);
                daysBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                weekBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                mouthBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                yearsBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
            }
        });
        //Добавляем цену
        timeSeekBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (modelPrices.size()+1 < 6) {
                    switch (selectTimeSeekBar) {
                        case "Ч":
                            time = " часа";
                            break;
                        case "Д":
                            time = " дня";
                            break;
                        case "Н":
                            time = " недели";
                            break;
                        case "М":
                            time = " месяца";
                            break;
                        case "Г":
                            time = " год";
                            break;
                    }
                    Log.d("multiPrice", "Цена = " + priceEditText.getText().toString()
                            + " Время = " + seekBarBubbleTime.getProgress());
                    if(seekBarBubbleTime.getProgress() == 0){
                        Toast.makeText(getActivity(), "Выберите срок",
                                Toast.LENGTH_SHORT).show();
                    }else if(priceEditText.getText().toString().equals("")) {
                        priceEditText.setError("Введите цену");
                    }else{
                        String price = priceEditText.getText().toString();
                        String progressTime = seekBarBubbleTime.getProgress() + "";
                        ModelPrice modelPrice = new ModelPrice();
                        modelPrice.setPrice(price);
                        modelPrice.setTime(progressTime + time);
                        modelPrices.add(modelPrice);
                        if (modelPrices != null) {
                            multiPriceViewHolder.setData(modelPrices);
                        }
                    }
                }
            }
        });
        daysBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeSeekBar = "Д";
                seekBarBubbleTime.getConfigBuilder().max(31 * 1).sectionCount(31).build();
                daysBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_check);
                hoursBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                weekBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                mouthBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                yearsBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
            }
        });
        weekBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeSeekBar = "Н";
                seekBarBubbleTime.getConfigBuilder().max(4 * 1).sectionCount(4).build();
                weekBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_check);
                hoursBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                daysBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                mouthBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                yearsBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
            }
        });
        mouthBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeSeekBar = "М";
                seekBarBubbleTime.getConfigBuilder().max(12 * 1).sectionCount(12).build();
                mouthBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_check);
                hoursBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                daysBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                weekBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                yearsBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
            }
        });
        yearsBtnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectTimeSeekBar = "Г";
                seekBarBubbleTime.getConfigBuilder().max(1 * 1).sectionCount(1).build();
                yearsBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_check);
                hoursBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                daysBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                weekBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
                mouthBtnTime.setBackgroundResource(R.drawable.form_from_btn_time_arenda_uncheck);
            }
        });
        seekBarBubbleTime.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress,
                                          float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
//        seekBarBubbleTime.setCustomSectionTextArray(new BubbleSeekBar.CustomSectionTextArray() {
//            @NonNull
//            @Override
//            public SparseArray<String> onCustomize(int sectionCount,
//                                                   @NonNull SparseArray<String> array) {
//                array.clear();
//                array.put(1,"Н");
//                array.put(5,"о");
//                array.put(9,"с");
//                return array;
//            }
//        });
        MainActivity ma = (MainActivity) this.getActivity();
        if (ma.placeIntent != null) {
            addAddress.setText(ma.placeIntent);
        }
        if (ma.latIntent != 0 && ma.lonIntent != 0) {
            latInt = ma.latIntent;
            lonInt = ma.lonIntent;
        }
        multiSelectImageViewHolder = new MultiSelectImageViewHolder(getActivity());
        multiPriceViewHolder = new MultiPriceViewHolder(getActivity(),false);
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
        modelAll.setAddress(addAddress.getText().toString());
        modelAll.setDirection(textViewDirection.getText().toString());
        modelAll.setPublisher(firebaseAuth);
        Log.d("priceTest"," push0 = " + push);
        databaseReference.child(push).setValue(modelAll).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    for (int i = 0; i < modelPrices.size(); i++) {
                        Log.d("priceTest","i = " + i + modelPrices.get(i) +
                                " push1 = " + push);
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
                        Log.d("test123", "i1 = " + downloadUri.toString());
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
                                btnSelectedImages.setText("Выбрать заново");
                            } else {
                                btnSelectedImages.setText("Выбрать изображение");
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
        recyclerView = v.findViewById(R.id.rcv_photo);
        textViewDirection = v.findViewById(R.id.textViewDirection);
        priceEditText = v.findViewById(R.id.priceEditText);
        seekBarBubbleTime = v.findViewById(R.id.seekBarBubbleTime);
        timeSeekBar = v.findViewById(R.id.timeSeekBar);
        hoursBtnTime = v.findViewById(R.id.hoursBtnTime);
        daysBtnTime = v.findViewById(R.id.daysBtnTime);
        weekBtnTime = v.findViewById(R.id.weekBtnTime);
        mouthBtnTime = v.findViewById(R.id.mouthBtnTime);
        yearsBtnTime = v.findViewById(R.id.yearsBtnTime);
        progressBar = v.findViewById(R.id.progressBar_main);
        sendAds = v.findViewById(R.id.sendAds);
        photoName = v.findViewById(R.id.photoName);
        addAddress = v.findViewById(R.id.addAddress);
        btnSelectedImages = v.findViewById(R.id.btnSelectedImages);
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}