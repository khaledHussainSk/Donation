package com.khaled.donation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.khaled.donation.databinding.ActivityContentUsBinding;

public class ContentUsActivity extends AppCompatActivity {
    ActivityContentUsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContentUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();

        binding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail();
            }
        });

    }

    private void fixed() {
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    protected void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {"ahmdalbaba391@gmail.com"};
        String[] CC = {binding.edYourEmail.getText().toString()};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, binding.etSub.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, binding.etDesc.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Toast.makeText(getApplicationContext(), "Finished sending email...", Toast.LENGTH_SHORT).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}