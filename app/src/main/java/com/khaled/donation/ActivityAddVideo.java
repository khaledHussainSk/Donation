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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Adapters.RvDisplayVideoAdapter;
import com.khaled.donation.Listeners.OnClickNoListener;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityAddVideoBinding;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

//import com.khaled.donation.Adapters.RvDisplayVideoAdapter;

public class ActivityAddVideo extends AppCompatActivity {
    public static final String VIDEO_KEY = "VIDEO_KEY";
    public static final String VIDEO_STRING_KEY_INTENT = "VIDEO_STRING_KEY_INTENT";
    ActivityAddVideoBinding binding;
    String videoString;
    String videoIntent;
    ArrayList<String> Videos;
    ArrayList<String> copyOfVideos;
    RvDisplayVideoAdapter adapter;
    FirebaseStorage storage;
    SharedPreferences sp;
    String currentUserID;
    String description;
    String category;
    String title;
    double price;
    User currentUser;
    int posts;
    Post post;
    int sizeOfVideo = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddVideoBinding.inflate(getLayoutInflater());
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
        Videos = new ArrayList<>();
        copyOfVideos = new ArrayList<>();
        adapter = new RvDisplayVideoAdapter(Videos,new OnClickNoListener() {
            @Override
            public void OnClickListener(String image) {
                cancel(image);
            }
        },this);
        LinearLayoutManager horizontalLayoutManagaer =
                new LinearLayoutManager(ActivityAddVideo.this
                        , LinearLayoutManager.HORIZONTAL, false);
        binding.rv.setLayoutManager(horizontalLayoutManagaer);
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(RvDisplayPostAdapter.POST_KEY);

        if (post == null){
            //عملية اضافة
            getUser();
            videoString = intent.getStringExtra(AddPostFragment.VIDEO_STRING_KEY);
            Videos.add(videoString);
        }else {
            //عملية تعديل
            binding.btnPost.setText(R.string.update);
            Videos = post.getImages();
            adapter = new RvDisplayVideoAdapter(Videos, new OnClickNoListener() {
                @Override
                public void OnClickListener(String image) {
                    cancel(image);
                }
            },getApplicationContext());
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
        categories.add("ملابس وأزياء");
        categories.add("أجهزة والكترونيات");
        categories.add("سيارات ومركبات");
        categories.add("أثاث وديكور");
        categories.add("عقارات وأملاك");
        categories.add("حيوانات وطيور");
        categories.add("أطعمة ومشروبات");

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
        ActivityResultLauncher<String> arlVideo = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {

                        binding.move.setVisibility(View.VISIBLE);
                        Videos.add(String.valueOf(result));
                        adapter.setVideo(Videos);
                        adapter.notifyDataSetChanged();
                        numbersOfVideos();

//                        videoIntent = String.valueOf(result);
//
//                        Intent intent = new Intent(getApplicationContext(),ActivityAddVideo.class);
//                        intent.putExtra(VIDEO_STRING_KEY_INTENT,videoIntent);
//                        startActivity(intent);
                    }
                }
        );

        binding.btnAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arlVideo.launch("video/*");
//                Intent i = new Intent(getApplicationContext(),ActivityAddVideo.class);
//                startActivity(i);
            }
        });
    }

    private void numbersOfVideos(){
        if (Videos.size() > 1){
            binding.move.setVisibility(View.VISIBLE);
            binding.btnPost.setEnabled(true);
            binding.btnAddMore.setText(R.string.addMore);
        }else if (Videos.size() == 0){
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
                    //عملية اضافة
                    uploadVideos();
                }else {
                    //عملية تعديل
                    updateImage();
                }

            }
        });
    }

    private void updateImage() {
        finish();
        copyOfVideos.clear();
        sizeOfVideo = Videos.size() - 1;
        for (int i = 0; i < Videos.size(); i++){
            int finalI = i;
            if (Videos.get(i).contains("https://firebasestorage.googleapis.com")){
                copyOfVideos.add(Videos.get(i));
                if (sizeOfVideo == finalI){
                    update();
                }
            }else {
                FirebaseStorage
                        .getInstance()
                        .getReference()
                        .child("PostsVideos/"+i+post.getDatenews())
                        .putFile(Uri.parse(Videos.get(i)))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(@NonNull Uri uri) {
                                                String image = String.valueOf(uri);
                                                copyOfVideos.add(image);
                                                if (sizeOfVideo == finalI){
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
        post = new Post(title,description,currentUserID, copyOfVideos
                , Calendar.getInstance().getTime()
                ,0,0,price,category,"video");
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
                Toasty.error(getBaseContext(), R.string.toast_post_failed, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void uploadVideos(){
        HomeFragment.isUploaded = true;
        disableField();
        finish();
        copyOfVideos.clear();
        sizeOfVideo = Videos.size() - 1;
        for (int i = 0; i < Videos.size(); i++){
            int finalI = i;
            storage.getReference().child("PostsVideos/"+i+Calendar.getInstance().getTime())
                    .putFile(Uri.parse(Videos.get(i)))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(@NonNull Uri uri) {
                                            String image = String.valueOf(uri);
                                            copyOfVideos.add(image);
                                            if (sizeOfVideo == finalI){
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
        Videos.remove(image);
        numbersOfVideos();
        adapter.notifyDataSetChanged();
    }
    private void showVideo(String image){
        Intent intent = new Intent(getBaseContext(), DisplayImageActivity.class);
        intent.putExtra(VIDEO_KEY, image);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (result != null){
            binding.move.setVisibility(View.VISIBLE);
            Videos.add(String.valueOf(result.getUri()));
            adapter.setVideo(Videos);
            adapter.notifyDataSetChanged();
            numbersOfVideos();
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
                .update("videos", copyOfVideos);
        Toast.makeText(ActivityAddVideo.this
                , R.string.post_has_been_modified
                , Toast.LENGTH_SHORT).show();
    }

}