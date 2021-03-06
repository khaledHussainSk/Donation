package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    String title;
    String description;
    String category;
    double price;
    User currentUser;
    int posts;
    Post post;
    int sizeOfImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        addMore();
        addPost();
        spinner();
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
            //?????????? ??????????
            getUser();
            imageString = intent.getStringExtra(AddPostFragment.IMAGE_STRING_KEY);
            images.add(imageString);
        }else {
            //?????????? ??????????
            binding.btnPost.setText(R.string.update);
            images = post.getImages();
            binding.etTitle.setText(post.getTitle());
            binding.etPrice.setText(String.valueOf(post.getPrice()));
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

    private void spinner(){
        ArrayList<String> categories = new ArrayList<>();
        categories.add("?????????? ????????????");
        categories.add("?????????? ??????????????????????");
        categories.add("???????????? ??????????????");
        categories.add("???????? ????????????");
        categories.add("???????????? ????????????");
        categories.add("?????????????? ??????????");
        categories.add("?????????? ????????????????");

        ArrayAdapter adapter = new ArrayAdapter(this
                , android.R.layout.simple_expandable_list_item_1,categories);

        binding.spCategory.setAdapter(adapter);

        binding.spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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

                title = binding.etTitle.getText().toString();
                description = binding.etDescription.getText().toString();
                String priceSt = binding.etPrice.getText().toString();

                if (TextUtils.isEmpty(title)){
                    Toast.makeText(getBaseContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(priceSt)){
                    Toast.makeText(getBaseContext(), "Please enter a price", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(description)){
                    description = "";
                }

                price = Double.parseDouble(priceSt);

                if (post == null){
                    //?????????? ??????????
                    uploadImages();
                }else {
                    //?????????? ??????????
                    updateImage();
                }

            }
        });
    }

    private void updateImage() {
        finish();
        copyOfimages.clear();
        sizeOfImage = images.size() - 1;
        for (int i=0; i < images.size();i++){
            int finalI = i;
            if (images.get(i).contains("https://firebasestorage.googleapis.com")){
                copyOfimages.add(images.get(i));
                if (sizeOfImage == finalI){
                    update();
                }
            }else {
                FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child("PostsImages/"+i+post.getDatenews())
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
                                                 update();
                                                }
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toasty.error(getBaseContext()
                                , R.string.toast_post_failed+e.getMessage()
                                , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

    }

    private void createPost(){
        post = new Post(title,description,currentUserID,copyOfimages
                ,Calendar.getInstance().getTime()
                ,0,0,price,category,"image");
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
                    Toasty.error(getBaseContext(), R.string.toast_post_failed
                            , Toast.LENGTH_SHORT).show();
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

    private void update(){
        FirebaseFirestore
                .getInstance()
                .collection("Posts")
                .document(post.getPostId())
                .update("description",description);
        FirebaseFirestore
                .getInstance()
                .collection("Posts")
                .document(post.getPostId())
                .update("category",category);
        FirebaseFirestore
                .getInstance()
                .collection("Posts")
                .document(post.getPostId())
                .update("price",price);
        FirebaseFirestore
                .getInstance()
                .collection("Posts")
                .document(post.getPostId())
                .update("title",title);
        FirebaseFirestore
                .getInstance()
                .collection("Posts")
                .document(post.getPostId())
                .update("images",copyOfimages);
        Toast.makeText(AddPhotoActivity.this
                , R.string.post_has_been_modified
                , Toast.LENGTH_SHORT).show();
    }

}