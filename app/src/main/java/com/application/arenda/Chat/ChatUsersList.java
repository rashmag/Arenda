package com.application.arenda.Chat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SearchView;

import com.application.arenda.Model.ModelAll;
import com.application.arenda.Notifications.Token;
import com.application.arenda.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatUsersList extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private View v;
    private SearchView searchView;
    private DatabaseReference reference, referencePost;
    private FirebaseUser firebaseUser;
    private List<ModelAll> arrayList;
    private List<String> usersList;
    private RecyclerView recyclerView;
    private ViewHolderChat viewHolderChat;

    public ChatUsersList() {
    }

    public static ChatUsersList newInstance(String param1, String param2) {
        ChatUsersList fragment = new ChatUsersList();
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
        v = inflater.inflate(R.layout.fragment_chat, container, false);
        init();
        usersList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        viewHolderChat = new ViewHolderChat();
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        readUsers();
        updateToken(FirebaseInstanceId.getInstance().getToken());
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                searchUsers(s.toLowerCase());
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                searchUsers(s.toLowerCase());
//                return false;
//            }
//        });
        // Inflate the layout for this fragment
        return v;
    }

//    private void searchUsers(String s) {
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
//                .startAt(s)
//                .endAt(s+"\uf8ff");
//
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                arrayList.clear();
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    ModelAuth auth = dataSnapshot.getValue(ModelAuth.class);
//
//                    assert auth!=null;
//                    assert user!=null;
//                    if(!auth.getId().equals(user.getUid())){
//                        arrayList.add(auth);
//                    }
//                }
//
//                viewHolderChat = new ViewHolderChat(ChatUsersList.this,arrayList);
//                recyclerView.setAdapter(viewHolderChat);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    private void readUsers() {
        reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ModelAll chat=snapshot.getValue(ModelAll.class);
                    assert chat != null;
                    if (chat.getSender().equals(firebaseUser.getUid())) {
                        usersList.add(chat.getReceiver());
                    }
                    if (chat.getReceiver().equals(chat.getPostId())) {
                        usersList.add(chat.getSender());
                    }
                }


                Set<String> hashSet = new HashSet<String>(usersList);
                usersList.clear();
                usersList.addAll(hashSet);
                readChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readChats() {
        arrayList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Post");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    ModelAll user=snapshot.getValue(ModelAll.class);
                    for(String id:usersList){
                        assert user != null;
                        if (user.getPostId().equals(id)) {
                            arrayList.add(user);
                        }
                    }
                }
                viewHolderChat = new ViewHolderChat(ChatUsersList.this,arrayList);
                recyclerView.setAdapter(viewHolderChat);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void updateToken(String token) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(token);
        reference.child(firebaseUser.getUid()).setValue(token1);
    }

    private void init() {
//        searchView = v.findViewById(R.id.searchUsers);
        recyclerView = v.findViewById(R.id.recyclerViewChat);
    }
}