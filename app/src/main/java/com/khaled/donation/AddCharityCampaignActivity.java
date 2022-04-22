package com.khaled.donation;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.khaled.donation.databinding.ActivityAddCharityCampaignBinding;

public class AddCharityCampaignActivity extends AppCompatActivity {
    ActivityAddCharityCampaignBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCharityCampaignBinding.inflate(getLayoutInflater());
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