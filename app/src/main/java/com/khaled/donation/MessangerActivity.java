package com.khaled.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.UserAdapter;
import com.khaled.donation.Models.Message;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityMessangerBinding;

import java.util.ArrayList;

public class MessangerActivity extends AppCompatActivity {

    ActivityMessangerBinding binding;
    FirebaseDatabase database;
    ArrayList<User> userArrayList;
    ArrayList<String> messageArrayList;
    ArrayList<User> allArrayList;
    String search;
    UserAdapter adapter;
    String currentUserId;
    SharedPreferences sp;
    User user;
    Message message;

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
        messageArrayList = new ArrayList<>();
        allArrayList = new ArrayList<>();

        database.getReference().child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot :snapshot.getChildren()){
                    message = dataSnapshot.getValue(Message.class);
//                    String m = dataSnapshot.getValue(String.class);
                    messageArrayList.add(dataSnapshot.getKey().toString());
//                    Toast.makeText(getApplicationContext(), "jjj"+dataSnapshot.getKey().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                        }

                        for (int i = 0 ; i < userArrayList.size(); i++){
//                            Toast.makeText(getApplicationContext(), "ddd"+userArrayList.get(i).getFullName(), Toast.LENGTH_SHORT).show();
                            for (int j = 0 ; j < messageArrayList.size() ; j++){
//                                Toast.makeText(getApplicationContext(), "ttt"+messageArrayList.get(j), Toast.LENGTH_SHORT).show();
                                String rr = currentUserId + userArrayList.get(i).getIdUser();
                                if (rr.equals(messageArrayList.get(j))){
//                                    Toast.makeText(getApplicationContext(), "rrrr", Toast.LENGTH_SHORT).show();
                                    allArrayList.add(userArrayList.get(i));
                                    binding.rvChat.hideShimmerAdapter();
                                }
                            }
                        }

                        adapter = new UserAdapter(allArrayList);
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

                                adapter = new UserAdapter(userArrayList);
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