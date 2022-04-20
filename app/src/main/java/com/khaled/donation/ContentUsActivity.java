package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.khaled.donation.databinding.ActivityContentUsBinding;

public class ContentUsActivity extends AppCompatActivity {
    ActivityContentUsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContentUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();


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