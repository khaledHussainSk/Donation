package com.khaled.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityShortBioBinding;

public class ShortBioActivity extends AppCompatActivity {
    ActivityShortBioBinding binding;
    SharedPreferences sp;
    String currentUserId;
    User currentUser;
    String shortBio;
    int caracterCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShortBioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        getUser();
        editBio();

    }

    private void fixed(){
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.etShortBio.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                String shortBio = binding.etShortBio.getText().toString();
                caracterCount = shortBio.length();
                binding.tvCharacterCount.setText(String.valueOf(caracterCount));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void getUser(){
        FirebaseFirestore.getInstance().collection("Users").document(currentUserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            currentUser = documentSnapshot.toObject(User.class);
                            if (getBaseContext() != null){
                                Glide.with(getBaseContext()).load(currentUser.getImageProfile())
                                        .placeholder(R.drawable.ic_user4).into(binding.ivProfile);
                                binding.tvUsername.setText(currentUser.getFullName());
                                if (!currentUser.getShortBio().equals("Edit Bio")){
                                    binding.etShortBio.setText(currentUser.getShortBio());
                                }
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editBio(){
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                shortBio = binding.etShortBio.getText().toString();
                
                if (TextUtils.isEmpty(shortBio)){
                    Toast.makeText(getBaseContext(), R.string.toast_nullShortBio, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (caracterCount >= 100){
                    Toast.makeText(getBaseContext(), R.string.toast_allowNumOfCahr, Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseFirestore.getInstance().collection("Users")
                        .document(currentUserId)
                        .update("shortBio",shortBio);

                finish();
            }
        });
    }

}