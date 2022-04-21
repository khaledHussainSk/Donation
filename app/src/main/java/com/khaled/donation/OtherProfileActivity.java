package com.khaled.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityOtherProfileBinding;

public class OtherProfileActivity extends AppCompatActivity {
    ActivityOtherProfileBinding binding;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();

    }

    private void fixed() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(SearchActivity.USER_KEY);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Glide.with(this).load(user.getImageProfile()).placeholder(R.drawable.ic_user4)
                .into(binding.ivProfile);
        binding.tvUsername.setText(user.getFullName());
        if (user.getShortBio().equals("Edit Bio")){
            binding.shortBio.setText(R.string.noBio);
        }else {
            binding.shortBio.setText(user.getShortBio());
        }
        binding.tvPosts.setText(String.valueOf(user.getPosts()));
        binding.tvFollowers.setText(String.valueOf(user.getPosts()));
        binding.tvFollowing.setText(String.valueOf(user.getPosts()));
    }

}