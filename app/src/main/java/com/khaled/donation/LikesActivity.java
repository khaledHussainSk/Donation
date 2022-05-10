package com.khaled.donation;

import android.app.Activity;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Adapters.RvLikesAdapter;
import com.khaled.donation.Models.Like;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityLikesBinding;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class LikesActivity extends AppCompatActivity {
    ActivityLikesBinding binding;
    SharedPreferences sp;
    String currentUserID;
    ArrayList<User> users;
    ArrayList<Like> likes;
    RvLikesAdapter adapter;
    Post post;
    public static Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLikesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        fixed();
        getLikes();


    }

    private void fixed() {
        context = this;
        users = new ArrayList<>();
        likes = new ArrayList<>();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(RvDisplayPostAdapter.POST_KEY);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getUser(Like like){
        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                User user = queryDocumentSnapshot.toObject(User.class);
                                if (user.getIdUser().equals(like.getId_who_gave_like())){
                                    users.add(user);
                                    likes.add(like);
                                }
                            }
                            adapter = new RvLikesAdapter(LikesActivity.this,users,likes);
                            binding.rv.setHasFixedSize(true);
                            binding.rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
                            binding.progressBar.setVisibility(View.GONE);
                            binding.rv.setAdapter(adapter);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLikes(){
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore
                .getInstance()
                .collection("Likes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                Like like = queryDocumentSnapshot.toObject(Like.class);
                                if (like.getId_post().equals(post.getPostId())){
                                    getUser(like);
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}