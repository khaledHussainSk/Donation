package com.khaled.donation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
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

        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MessangerActivity.class);
                startActivity(intent);
            }
        });

    }

    private void fixed() {
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(MainActivity.USER_KEY);
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