package com.khaled.donation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivitySignUpBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    Calendar calendar;
    Uri imageUri;
    String fullName,email,password,phoneNumber,address,confirmPassword,image;
    User user;
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getInfo();

            }
        });

    }

    private void fixed(){
        firebaseAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        calendar = Calendar.getInstance();
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });
        binding.ivCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SignUpActivity.this);

            }
        });
        binding.ivCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SignUpActivity.this);
            }
        });
    }

    private void getInfo(){
        fullName = binding.etFullName.getText().toString();
        email = binding.etEmail.getText().toString();
        password = binding.etPassword.getText().toString();
        confirmPassword = binding.etConfirmPassword.getText().toString();
        phoneNumber = binding.etPhoneNumber.getText().toString();
        address = binding.etAddress.getText().toString();

        if (TextUtils.isEmpty(fullName)){
            Toast.makeText(getBaseContext(), R.string.toast_nullName, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)){
            Toast.makeText(getBaseContext()
                    , R.string.toast_nullPhoneNumber, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(address)){
            Toast.makeText(getBaseContext()
                    , R.string.toast_nullAddress, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)){
            Toast.makeText(getBaseContext()
                    , R.string.toast_nullEmail, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(getBaseContext()
                    , R.string.toast_nullPassword, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(getBaseContext()
                    , R.string.toast_nullConfirmPassword, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!confirmPassword.equals(password)){
            Toast.makeText(getBaseContext()
                    , R.string.toast_passwordDoesNotMatch, Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri != null){
            binding.spinKit.setVisibility(View.VISIBLE);
            disableField();
            storage.getReference()
                    .child("profilesImages/"+Calendar.getInstance().getTime()).putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri) {
                                            image = String.valueOf(uri);
                                            createUser();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getBaseContext(), ""+ e.getMessage()
                            , Toast.LENGTH_SHORT).show();
                    binding.spinKit.setVisibility(View.GONE);
                    enableField();
                }
            });
        }else {
            binding.spinKit.setVisibility(View.VISIBLE);
            disableField();
            createUser();
        }

    }

    private void createUser(){
        firebaseAuth.createUserWithEmailAndPassword(binding.etEmail.getText().toString()
                ,binding.etPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            user = new User(fullName,phoneNumber,address,email,password
                            ,image,"Edit Bio",calendar.getTime(),0,0,0,1);
                            DocumentReference documentReference =
                                    FirebaseFirestore
                                            .getInstance()
                                            .collection("Users").document();
                            user.setIdUser(documentReference.getId());
                            documentReference
                                    .set(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        binding.spinKit.setVisibility(View.GONE);
                                        Intent intent = new Intent(SignUpActivity.this
                                                ,SuccessActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    binding.spinKit.setVisibility(View.GONE);
                                    enableField();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.spinKit.setVisibility(View.GONE);
                enableField();
                Toast.makeText(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableField(){
        binding.ivCam.setEnabled(false);
        binding.ivCircle.setEnabled(false);
        binding.etFullName.setEnabled(false);
        binding.etPhoneNumber.setEnabled(false);
        binding.etAddress.setEnabled(false);
        binding.etEmail.setEnabled(false);
        binding.etConfirmPassword.setEnabled(false);
        binding.etPassword.setEnabled(false);
        binding.btnSignUp.setEnabled(false);
        binding.icBack.setEnabled(false);
        binding.signIn.setEnabled(false);
    }
    private void enableField(){
        binding.ivCam.setEnabled(true);
        binding.ivCircle.setEnabled(true);
        binding.etFullName.setEnabled(true);
        binding.etPhoneNumber.setEnabled(true);
        binding.etAddress.setEnabled(true);
        binding.etEmail.setEnabled(true);
        binding.etConfirmPassword.setEnabled(true);
        binding.etPassword.setEnabled(true);
        binding.btnSignUp.setEnabled(true);
        binding.icBack.setEnabled(true);
        binding.signIn.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (result != null){
            binding.ivCam.setVisibility(View.GONE);
            imageUri = result.getUri();
            Glide.with(getBaseContext()).load(imageUri)
                    .placeholder(R.drawable.ic_user4).into(binding.ivCircle);
        }
    }

}