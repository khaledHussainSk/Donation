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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.khaled.donation.Adapters.RvDisplayImageAdapter;
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Listeners.OnClickItemImageListener;
import com.khaled.donation.Listeners.OnClickNoListener;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityAddPhotoBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class AddPhotoActivity extends AppCompatActivity {
    public static final String IMAGE_KEY = "IMAGE_KEY";
    ActivityAddPhotoBinding binding;
    String imageString;
    ArrayList<String> images;
    ArrayList<String> copyOfimages;
    RvDisplayImageAdapter adapter;
    FirebaseStorage storage;
    SharedPreferences sp;
    String currentUserID;
    String description;
    User currentUser;
    int posts;
    Post post;
    Uri imageUri = null;
    int sizeOfImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        addMore();
        addPost();
    }

    private void fixed() {
        storage = FirebaseStorage.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        images = new ArrayList<>();
        copyOfimages = new ArrayList<>();
        adapter = new RvDisplayImageAdapter(images, new OnClickItemImageListener() {
            @Override
            public void OnClickListener(String image) {
                showImage(image);
            }
        }
        , new OnClickNoListener() {
            @Override
            public void OnClickListener(String image) {
                cancel(image);
            }
        });
        LinearLayoutManager horizontalLayoutManagaer =
                new LinearLayoutManager(AddPhotoActivity.this
                        , LinearLayoutManager.HORIZONTAL, false);
        binding.rv.setLayoutManager(horizontalLayoutManagaer);
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(RvDisplayPostAdapter.POST_KEY);

        if (post == null){
            //عملية اضافة
            getUser();
            imageString = intent.getStringExtra(AddPostFragment.IMAGE_STRING_KEY);
            images.add(imageString);
        }else {
            //عملية تعديل
            binding.btnPost.setText(R.string.update);
            images = post.getImages();
            adapter = new RvDisplayImageAdapter(images, new OnClickItemImageListener() {
                @Override
                public void OnClickListener(String image) {
                    showImage(image);
                }
            }, new OnClickNoListener() {
                @Override
                public void OnClickListener(String image) {
                    cancel(image);
                }
            });
            binding.etDescription.setText(post.getDescription());
        }
        binding.rv.setHasFixedSize(true);
        binding.rv.setAdapter(adapter);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addMore(){
        binding.btnAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddPhotoActivity.this);
            }
        });
    }

    private void numbersOfPhotos(){
        if (images.size() > 1){
            binding.move.setVisibility(View.VISIBLE);
            binding.btnPost.setEnabled(true);
            binding.btnAddMore.setText(R.string.addMore);
        }else if (images.size() == 0){
            binding.move.setVisibility(View.GONE);
            binding.btnPost.setEnabled(false);
            binding.btnAddMore.setText(R.string.addPhoto);
        }else {
            binding.move.setVisibility(View.GONE);
            binding.btnPost.setEnabled(true);
            binding.btnAddMore.setText(R.string.addMore);
        }
    }

    private void addPost(){

        binding.btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                description = binding.etDescription.getText().toString();

                if (TextUtils.isEmpty(description)){
                    description = "";
                }

                if (post == null){
                    //عملية اضافة
                    uploadImages();
                }else {
                    //عملية تعديل
//                    updateImage();
                }

            }
        });
    }

    private void updateImage() {
        finish();
        FirebaseFirestore.getInstance().collection("Posts")
                .document(post.getPostId())
                .update("description",description);

        copyOfimages.clear();
        sizeOfImage = images.size() - 1;
        for (int i=0; i < images.size();i++){
            int finalI = i;
            FirebaseStorage
                    .getInstance()
                    .getReference()
                    .child("PostsImages/"+i+Calendar.getInstance().getTime())
                    .putFile(Uri.parse(images.get(i)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri) {
                                            String image = String.valueOf(uri);
                                            copyOfimages.add(image);
                                            if (sizeOfImage == finalI){
                                                Toast.makeText(getBaseContext(), "ok", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toasty.error(getBaseContext(), R.string.toast_post_failed, Toast.LENGTH_SHORT).show();
                }
            });

//            storage.getReference().child("PostsImages/"+i+post.getDatenews())
//                    .putFile(Uri.parse(images.get(i)))
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
//                            taskSnapshot.getStorage().getDownloadUrl()
//                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(@NonNull Uri uri) {
//                                            HomeFragment.isUploaded = true;
//                                            String image = String.valueOf(uri);
//                                            copyOfimages.add(image);
//                                            if (sizeOfImage == finalI){
//                                                FirebaseFirestore
//                                                        .getInstance().collection("Posts")
//                                                        .document(post.getPostId())
//                                                        .update("images",copyOfimages);
//                                                HomeFragment.isUploaded = false;
//                                            }
//                                        }
//                                    });
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
////                    Toasty.error(getBaseContext(), R.string.toast_post_failed, Toast.LENGTH_SHORT).show();
//                }
//            });
        }

    }

    private void createPost(){
        post = new Post(description,currentUserID,copyOfimages,Calendar.getInstance().getTime()
                ,0,0);
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Posts")
                .document();
        post.setPostId(documentReference.getId());
        documentReference.set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
//                Toasty.success(getBaseContext(), R.string.toast_post_published, Toast.LENGTH_SHORT).show();
                posts = posts + 1;
                FirebaseFirestore.getInstance().collection("Users")
                        .document(currentUserID).update("posts",posts);
                HomeFragment.isUploaded = false;
                enableField();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toasty.error(getBaseContext(), R.string.toast_post_failed, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadImages(){
        HomeFragment.isUploaded = true;
        disableField();
        finish();
        copyOfimages.clear();
        sizeOfImage = images.size() - 1;
        for (int i=0; i < images.size();i++){
            int finalI = i;
            storage.getReference().child("PostsImages/"+i+Calendar.getInstance().getTime())
                    .putFile(Uri.parse(images.get(i)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri) {
                                            String image = String.valueOf(uri);
                                            copyOfimages.add(image);
                                            if (sizeOfImage == finalI){
                                                createPost();
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
//                    Toasty.error(getBaseContext(), R.string.toast_post_failed, Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    private void disableField(){
        binding.rv.setEnabled(false);
        binding.et.setEnabled(false);
        binding.btnPost.setEnabled(false);
        binding.btnAddMore.setEnabled(false);
    }
    private void enableField(){
        binding.rv.setEnabled(true);
        binding.et.setEnabled(true);
        binding.btnPost.setEnabled(true);
        binding.btnAddMore.setEnabled(true);
    }
    private void cancel(String image){
        images.remove(image);
        numbersOfPhotos();
        adapter.notifyDataSetChanged();
    }
    private void showImage(String image){
        Intent intent = new Intent(getBaseContext(), DisplayImageActivity.class);
        intent.putExtra(IMAGE_KEY, image);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (result != null){
            binding.move.setVisibility(View.VISIBLE);
            images.add(String.valueOf(result.getUri()));
            adapter.setImages(images);
            adapter.notifyDataSetChanged();
            numbersOfPhotos();
        }
    }

}