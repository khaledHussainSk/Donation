package com.khaled.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.ChatAdapter;
import com.khaled.donation.Adapters.RecyclerSearchAdapter;
import com.khaled.donation.Listeners.OnClickItemSearchListener;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityMessangerBinding;

import java.util.ArrayList;

public class MessangerActivity extends AppCompatActivity {

    ActivityMessangerBinding binding;
    FirebaseDatabase database;
    ArrayList<User> userArrayList;
    String search;
    ChatAdapter adapter;
    String currentUserId;
    SharedPreferences sp;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMessangerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        chat();

    }

    private void fixed() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sp.getString(MainActivity.USER_ID_KEY, null);
    }

    private void chat() {
        search = binding.etSearch.getText().toString();

        database = FirebaseDatabase.getInstance();

        FirebaseFirestore.getInstance()
                .collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        userArrayList = new ArrayList<>();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            User user = queryDocumentSnapshot.toObject(User.class);
                            if (!user.getIdUser().equals(currentUserId)) {
                                userArrayList.add(user);
                            }
                            binding.rvChat.hideShimmerAdapter();
                        }

                        adapter = new ChatAdapter(userArrayList);
                        binding.rvChat.showShimmerAdapter();
                        binding.rvChat.setAdapter(adapter);
                        binding.rvChat.setHasFixedSize(true);
                        binding.rvChat
                                .setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    }
                });

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                userArrayList.clear();
                FirebaseFirestore.getInstance()
                        .collection("Users")
                        .orderBy("fullName")
                        .startAt(charSequence.toString().trim())
                        .endAt(charSequence.toString().trim() + "\uf8ff").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                userArrayList = new ArrayList<>();

                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                    User user = queryDocumentSnapshot.toObject(User.class);
                                    if (!user.getIdUser().equals(currentUserId)) {
                                        userArrayList.add(user);
                                    }
                                    binding.rvChat.hideShimmerAdapter();
                                }

                                adapter = new ChatAdapter(userArrayList);
                                binding.rvChat.showShimmerAdapter();
                                binding.rvChat.setAdapter(adapter);
                                binding.rvChat.setHasFixedSize(true);
                                binding.rvChat
                                        .setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    public void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }
}