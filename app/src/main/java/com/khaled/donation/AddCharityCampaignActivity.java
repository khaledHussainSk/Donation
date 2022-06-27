package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityAddCharityCampaignBinding;

import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class AddCharityCampaignActivity extends AppCompatActivity {
    ActivityAddCharityCampaignBinding binding;
    FirebaseStorage storage;
    SharedPreferences sp;
    String currentUserID;
    String description;
    String title;
    String donation_link;
    int posts;
    Post post;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCharityCampaignBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        get_intent();
        addPost();

    }

    private void fixed() {
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        storage = FirebaseStorage.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
    }

    private void get_intent(){
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(RvDisplayPostAdapter.POST_KEY);

        if (post == null){
            //عملية اضافة
            getUser();
        }else{
            //عملية تعديل
            binding.etTitle.setText(post.getTitle());
            binding.etDescription.setText(post.getDescription());
            binding.etDonationLink.setText(post.getDonation_link());
            binding.btnPost.setText(R.string.edit);
        }
    }

    private void addPost(){

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                title = binding.etTitle.getText().toString();
                description = binding.etDescription.getText().toString();
                donation_link = binding.etDonationLink.getText().toString();

                if (TextUtils.isEmpty(title)){
                    Toast.makeText(getBaseContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(description)){
                    description = "";
                }

                if (TextUtils.isEmpty(donation_link)){
                    Toast.makeText(getBaseContext(), "Please enter the donation link", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (post == null){
                    //عملية اضافة
                    createPost();
                }else {
                    //عملية تعديل
                    updatePost();
                }

            }
        });
    }

    private void updatePost() {
        FirebaseFirestore.getInstance().collection("Posts")
                .document(post.getPostId())
                .update("title",title);

        FirebaseFirestore.getInstance().collection("Posts")
                .document(post.getPostId())
                .update("description",description);

        FirebaseFirestore.getInstance().collection("Posts")
                .document(post.getPostId())
                .update("donation_link",donation_link);

        Toast.makeText(this
                , R.string.post_has_been_modified
                , Toast.LENGTH_SHORT).show();

        finish();
    }

    private void createPost(){
        HomeFragment.isUploaded = true;
        disableField();
        finish();
        post = new Post(title,description,donation_link,currentUserID
                ,Calendar.getInstance().getTime()
                ,0,0,"حملات","campaign");
        DocumentReference documentReference = FirebaseFirestore
                .getInstance().collection("Posts")
                .document();
        post.setPostId(documentReference.getId());
        documentReference.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getBaseContext()
                        , R.string.toast_post_published, Toast.LENGTH_SHORT).show();
                posts = posts + 1;
                FirebaseFirestore.getInstance().collection("Users")
                        .document(currentUserID).update("posts",posts);
                HomeFragment.isUploaded = false;
                enableField();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                HomeFragment.isUploaded = false;
                Toasty.error(getBaseContext(), R.string.toast_post_failed, Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void disableField(){
        binding.et.setEnabled(false);
        binding.btnPost.setEnabled(false);
    }
    private void enableField(){
        binding.et.setEnabled(true);
        binding.btnPost.setEnabled(true);
    }

    private void getUser(){
        disableField();
        FirebaseFirestore.getInstance().collection("Users").document(currentUserID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            currentUser = documentSnapshot.toObject(User.class);
                            if (getBaseContext() != null){
                                posts = currentUser.getPosts();
                                enableField();
                            }
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), ""+e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}