package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityProfileDetailsBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class ProfileDetailsActivity extends AppCompatActivity {
    ActivityProfileDetailsBinding binding;
    SharedPreferences sp;
    String currentUserId;
    User currentUser;
    Uri imageUri;
    String fullName,phoneNumber,address,image;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        getUser();
        save();
    }

    private void fixed(){
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        storage = FirebaseStorage.getInstance();
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileDetailsActivity.this);
            }
        });
    }

    private void getUser(){
        disableField();
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
                                binding.etFullName.setText(currentUser.getFullName());
                                binding.etPhoneNumber.setText(currentUser.getPhoneNumber());
                                binding.etAddress.setText(currentUser.getAddress());
                                image = currentUser.getImageProfile();
                                enableField();
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

    private void save(){
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullName = binding.etFullName.getText().toString();
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

                if (image == null){
                    if (imageUri == null){
                        editUser();
                    }else {
                        uploadImage();
                    }
                }else if (image.equals(currentUser.getImageProfile())){
                    if (imageUri == null){
                        editUser();
                    }
                }else {
                    uploadImage();
                }

            }
        });
    }

    private void disableField(){
        binding.ivProfile.setEnabled(false);
        binding.etFullName.setEnabled(false);
        binding.etPhoneNumber.setEnabled(false);
        binding.etAddress.setEnabled(false);
        binding.btnSave.setEnabled(false);
        binding.icBack.setEnabled(false);
    }
    private void enableField(){
        binding.ivProfile.setEnabled(true);
        binding.etFullName.setEnabled(true);
        binding.etPhoneNumber.setEnabled(true);
        binding.etAddress.setEnabled(true);
        binding.btnSave.setEnabled(true);
        binding.icBack.setEnabled(true);
    }

    private void editUser(){
        binding.spinKit.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("Users")
                .document(currentUserId)
                .update("imageProfile",image);

        FirebaseFirestore.getInstance().collection("Users")
                .document(currentUserId)
                .update("fullName",fullName);

        FirebaseFirestore.getInstance().collection("Users")
                .document(currentUserId)
                .update("phoneNumber",phoneNumber);

        FirebaseFirestore.getInstance().collection("Users")
                .document(currentUserId)
                .update("address",address);

        enableField();
        binding.spinKit.setVisibility(View.GONE);
        finish();

    }
    private void uploadImage(){
        binding.spinKit.setVisibility(View.VISIBLE);
        disableField();
        storage.getReference().child("profilesImages/"+currentUser.getJoined())
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(@NonNull Uri uri) {
                                        image = String.valueOf(uri);
                                        editUser();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), ""+ e.getMessage()
                        , Toast.LENGTH_SHORT).show();
                enableField();
                binding.spinKit.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (result != null){
            imageUri = result.getUri();
            image = String.valueOf(imageUri) ;
            Glide.with(getBaseContext()).load(imageUri)
                    .placeholder(R.drawable.ic_user4).into(binding.ivProfile);
        }
    }

}