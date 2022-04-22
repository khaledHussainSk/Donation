package com.khaled.donation;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Adapters.RvJustDisplayPhotosPostAdapter;
import com.khaled.donation.Listeners.OnClickItemListener;
import com.khaled.donation.databinding.ActivityDisplayAllImagesPostBinding;

import java.util.ArrayList;

public class DisplayAllImagesPostActivity extends AppCompatActivity {
    ActivityDisplayAllImagesPostBinding binding;
    ArrayList<String> images;
    RvJustDisplayPhotosPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDisplayAllImagesPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        getAllPhotos();

    }

    private void fixed() {
        images = new ArrayList<>();
        Intent intent = getIntent();
        images = intent.getStringArrayListExtra(RvDisplayPostAdapter.IMAGES_POST_KEY);
    }

    private void getAllPhotos(){
        adapter = new RvJustDisplayPhotosPostAdapter(images, new OnClickItemListener() {
            @Override
            public void OnCLickListener() {
                finish();
            }
        });
        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        binding.rv.setAdapter(adapter);
    }

}