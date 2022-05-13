package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.RvFollowersFollowingAdapter;
import com.khaled.donation.Adapters.RvPostsProfileAdapter;
import com.khaled.donation.Models.Friend;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityFollowersBinding;

import java.util.ArrayList;

public class FollowersActivity extends AppCompatActivity {
    ActivityFollowersBinding binding;
    ArrayList<User> users;
    RvFollowersFollowingAdapter adapter;
    String currentUserID;
    SharedPreferences sp;
    String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFollowersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        getFollowers();

    }

    private void fixed() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        users = new ArrayList<>();
        Intent intent = getIntent();
        id_user = intent.getStringExtra(RvPostsProfileAdapter.ID_USER_KEY);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getFollowers(){
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("Friends")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                users.clear();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    Friend friend = queryDocumentSnapshot.toObject(Friend.class);
                    if (id_user != null){
                        if (friend.getId_follower().equals(id_user)){
                            getUser(friend);
                        }
                    }else {
                        if (friend.getId_follower().equals(currentUserID)){
                            getUser(friend);
                        }
                    }

                }
                adapter = new RvFollowersFollowingAdapter(getBaseContext(),users);
                binding.rv.setHasFixedSize(true);
                binding.rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                binding.rv.setAdapter(adapter);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getUser(Friend friend) {
        FirebaseFirestore.getInstance().collection("Users").document(friend.getId_following())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = documentSnapshot.toObject(User.class);
                        users.add(user);
                        adapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

}