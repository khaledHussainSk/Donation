package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.khaled.donation.databinding.ActivityAddPhotoBinding;

public class AddPhotoActivity extends AppCompatActivity {
    ActivityAddPhotoBinding binding;
    String imageString;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();

        Intent intent = getIntent();
        imageString = intent.getStringExtra(AddPostFragment.IMAGE_STRING_KEY);
        Glide.with(this).load(imageString).into(binding.ivPost);


    }

    private void fixed() {
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}