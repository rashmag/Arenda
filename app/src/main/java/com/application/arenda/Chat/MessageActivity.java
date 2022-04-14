package com.application.arenda.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.arenda.Ads.LentaAds;
import com.application.arenda.Model.ModelAll;
import com.application.arenda.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jgabrielfreitas.core.BlurImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    private String userId, userPhotoUrlIntent;
    private ImageView sendMessageBtn;
    private CheckBox checkBox;
    private String messageId;
    private BlurImageView blurImageView;
    private CircleImageView adsPhoto;
    private TextView userName, statusTextView;
    private FirebaseUser fUser;
    private DatabaseReference reference, referenceChat;
    private String postId;
    private EditText messageEd;
    private int count;
    private ViewHolderMessage viewHolderMessage;
    private List<ModelAll> lMessage;
    private RecyclerView recyclerView;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        Intent intent = getIntent();
        userId = intent.getExtras().getString("userId");
        postId = intent.getExtras().getString("postId");
        init();
        uploadUserInfo();
        SharedPreferences sharedPreferences = getSharedPreferences("Listeners", MODE_PRIVATE);
        boolean blurChecked = sharedPreferences.getBoolean("blur", false);
        if (blurChecked) {
            blurImageView.setBlur(3);
            checkBox.setChecked(true);
        } else {
            blurImageView.setBlur(0);
            checkBox.setChecked(false);
        }
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    sharedPreferences.edit().putBoolean("blur", true).apply();
                    blurImageView.setBlur(3);
                } else {
                    sharedPreferences.edit().putBoolean("blur", false).apply();
                    blurImageView.setBlur(0);
                }
            }
        });

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        checkStatus();
        messageEd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                statusTyping("Печатает...");
            }

        });
        sendMessageBtn.setTag("send");
        ModelAll modelMessage = new ModelAll();
        editText(modelMessage);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("testq","getMessageId0 = " + messageId);
                if (sendMessageBtn.getTag().equals("send")) {
                    String msg = messageEd.getText().toString();
                    if (!msg.equals("")) {
                        sendMessage(fUser.getUid(), postId, msg, postId);
                    } else {
                        messageEd.setError("Введите сообщение");
                    }
                    messageEd.setText("");
                } else {
                    if (!messageEd.getText().toString().equals("") && sendMessageBtn.getTag().equals("pencil")) {
                        sendMessageBtn.setImageDrawable(getResources().getDrawable(R.drawable.
                                send_icon));
                        sendMessageBtn.setTag("send");
                        messageEd.setFocusable(false);
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat")
                                .child(messageId);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", messageEd.getText().toString());
                        reference.updateChildren(hashMap);
                        messageEd.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(messageEd.getWindowToken(), 0);
                    } else {
                        messageEd.setError("Введите реадктируемое сообщение");
                    }
                }

            }
        });
        DatabaseReference referencePost = FirebaseDatabase.getInstance().
                getReference("Post").
                child(postId).child("arrayimagesurl");
        referencePost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    if (count != -1000) {
                        count++;
                        if (count == 1) {
                            Glide.with(getApplicationContext())
                                    .load(String.valueOf(String.valueOf(snapshot1.child("adsurl").getValue())))
                                    .into(adsPhoto);
//                        holder.setImageView(getActivity().getApplication(),
//                                String.valueOf(snapshot1.child("adsurl").getValue()));
                        }
                        if (count == 2) {
                            count = -1000;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adsPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this,
                        LentaAds.class);
                intent.putExtra("postId", postId);
                intent.putExtra("publisherId", userId);
                startActivity(intent);
            }
        });
        seenMessage(userId);
        readMessages(fUser.getUid(), postId, userPhotoUrlIntent);

    }


    private void uploadUserInfo() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ModelAll modelAuth = snapshot1.getValue(ModelAll.class);
                    if (MessageActivity.this == null) {
                        return;
                    }

                    if (modelAuth.getId() != null) {
                        if (modelAuth.getId().equals(userId)) {
                            if (modelAuth.getUserPhotoUri() != null) {
                                userPhotoUrlIntent = modelAuth.getUserPhotoUri();
                            }
                            if (modelAuth.getUserName() != null) {
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

    private void sendMessage(String sender, String receiver, String message,
                             String postId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);
        hashMap.put("postId", postId);
        hashMap.put("isseen", false);
        reference.child("Chat").push().setValue(hashMap);
    }

    private void init() {
        recyclerView = findViewById(R.id.recyclerViewMessageActivity);
        adsPhoto = findViewById(R.id.adsPhotoMessageActivity);
        userName = findViewById(R.id.userNameMessageActivity);
        messageEd = findViewById(R.id.messageEd);
        statusTextView = findViewById(R.id.status);
        sendMessageBtn = findViewById(R.id.sendMessageBtn);
        checkBox = findViewById(R.id.checkBox);
        blurImageView = findViewById(R.id.blurImageView);
    }

    private void checkStatus() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelAll modelAuth = dataSnapshot.getValue(ModelAll.class);
                    if (modelAuth.getStatus() != null && modelAuth.getId() != null) {
                        if (modelAuth.getId().equals(userId)) {
                            if (modelAuth.getStatus().equals("online")) {
                                statusTextView.setText("online");
                            } else if (modelAuth.getStatus().equals("typing...")) {
                                statusTextView.setText("typing...");
                            } else if (modelAuth.getStatus().equals("offline")) {
                                statusTextView.setText("offline");
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

    private void statusTyping(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(fUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
        Handler handler = new Handler();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("status", "online");

                reference.updateChildren(hashMap);
            }
        }, 6000);
    }

    private void readMessages(String myId, String postId, String imageUrl) {
        lMessage = new ArrayList<>();

        referenceChat = FirebaseDatabase.getInstance().getReference("Chat");
        referenceChat.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lMessage.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelAll modelMessage = dataSnapshot.getValue(ModelAll.class);
                    if (modelMessage.getReceiver().equals(myId) && modelMessage.getSender().
                            equals(postId) ||
                            modelMessage.getReceiver().equals(postId) && modelMessage.getSender().
                                    equals(myId)) {
                        modelMessage.setMessageId(dataSnapshot.getKey());
                        lMessage.add(modelMessage);
                    }
                    viewHolderMessage = new ViewHolderMessage(MessageActivity.this,
                            lMessage, imageUrl,
                            sendMessageBtn);
                    recyclerView.setAdapter(viewHolderMessage);
                    recyclerView.smoothScrollToPosition(viewHolderMessage.getItemCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void seenMessage(final String userid) {
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        valueEventListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ModelAll modelMessage = dataSnapshot.getValue(ModelAll.class);
                    if (modelMessage.getReceiver().equals(fUser.getUid()) && modelMessage.getSender()
                            .equals(userid)) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void status(String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(fUser.getUid());

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
            reference.removeEventListener(valueEventListener);
            status("offline");
        }
    }

    public void editText(ModelAll modelMessage) {
        if (sendMessageBtn.getTag().equals("pencil")) {
            Log.d("testq","getMessageId1 = " + modelMessage.getMessageId());
            messageId = modelMessage.getMessageId();
            String text = modelMessage.getMessage();
            messageEd.setText(text);
            messageEd.post(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager inputMethodManager =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.toggleSoftInputFromWindow(
                            messageEd.getApplicationWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
                    messageEd.requestFocus();
                    messageEd.setSelection(messageEd.length());
                }
            });
            sendMessageBtn.setImageDrawable(getResources().getDrawable(R.drawable.edit_icon));
            sendMessageBtn.setTag("pencil");
        }
    }
}