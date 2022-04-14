package com.application.arenda.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {
    private String userNameIntent, userPhotoUri, userIdIntent;
    private TextView userName;
    private Button uploadBtn;
    private static final int PICK_VIDEO = 1;
    private ModelAll modelAuth;
    private ProgressBar progressBar_main;
    private FirebaseUser firebaseUser;
    private CircleImageView userPhoto;
    private StorageReference storageReference;
    private Uri videoUri;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        init();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        modelAuth = new ModelAll();
        Intent intent = getIntent();

        storageReference = FirebaseStorage.getInstance().getReference("UserPhoto");
        userNameIntent = intent.getExtras().getString("userName");
        userIdIntent = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userPhotoUri = intent.getExtras().getString("userPhotoUri");
        if (userPhotoUri != null) {
            Glide.with(EditProfile.this).load(userPhotoUri).into(userPhoto);
        }
        userName.setText(userNameIntent);

        userPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_VIDEO);
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadVideo(userName.getText().toString());
            }
        });
    }

    private void UploadVideo(String userNameEd) {
//        if(videoUri == null){
//            videoUri = Uri.parse(userPhotoUri);
//        }
        editData(userNameEd);

    }

    private void editData(String userNameEd) {
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").
                child(firebaseUser.getUid());
        progressBar_main.setVisibility(View.VISIBLE);
        if (videoUri != null && !TextUtils.isEmpty(userNameEd)) {
            final StorageReference reference = storageReference.child(System.currentTimeMillis()
                    + "." + getExt(videoUri));
            uploadTask = reference.putFile(videoUri);

            Task<Uri> urltask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return reference.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
//                                modelAuth.setUserName(finalUserNameEd1);
//                                modelAuth.setUserPhotoUri(downloadUri.toString());
//                                modelAuth.setFullName(finalFullNameEd);
//                                modelAuth.setId(firebaseUser.getUid());

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("userName", userNameEd);
                                hashMap.put("id", userIdIntent);
                                hashMap.put("userPhotoUri", downloadUri.toString());
                                reference1.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressBar_main.setVisibility(View.GONE);
                                    }
                                });
                                userPhotoUri = downloadUri.toString();
                            } else {
                                progressBar_main.setVisibility(View.GONE);
                                Toast.makeText(EditProfile.this, "Ошибка = " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else if (!TextUtils.isEmpty(userNameEd)) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("userName", userNameEd);
            hashMap.put("id", userIdIntent);
            reference1.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressBar_main.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(EditProfile.this, "All Fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        progressBar_main = findViewById(R.id.progressBar_main);
        uploadBtn = findViewById(R.id.uploadBtn);
        userName = findViewById(R.id.userNameEditProfile);
        userPhoto = findViewById(R.id.userPhotoEditProfile);
    }

    private String getExt(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_VIDEO) {
            if (resultCode == -1) {
                try {
                    videoUri = data.getData();
                    userPhoto.setImageURI(videoUri);
                } catch (Exception e) {
                    Toast.makeText(EditProfile.this, "Изображение не выбрано",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            status("online");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            status("offline");
        }
    }
}