package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.khaled.donation.databinding.ActivityAppReviewBinding;

public class AppReviewActivity extends AppCompatActivity {
    ActivityAppReviewBinding binding;
    private ReviewInfo reviewInfo;
    private ReviewManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAppReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        activateReviewInfo();

    }
    void activateReviewInfo(){
        manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> mangerInfoTask = manager.requestReviewFlow();
        mangerInfoTask.addOnCompleteListener((task -> {
            if (task.isSuccessful()){
                reviewInfo = task.getResult();
            }else {
                Toast.makeText(this, "review failed to start", Toast.LENGTH_SHORT).show();
            }
        }));
    }

    void startReviewFlow(){
        if (reviewInfo != null){
            Task<Void> flow = manager.launchReviewFlow(this,reviewInfo);
            flow.addOnCompleteListener(task -> {
                Toast.makeText(this, "Rating is completed", Toast.LENGTH_SHORT).show();
            });
        }
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