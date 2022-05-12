package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.RvDisplayPhotosPostAdapter;
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Listeners.OnClickItemImagePostListener;
import com.khaled.donation.Models.Favorite;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityPostDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PostDetailsActivity extends AppCompatActivity {
    ActivityPostDetailsBinding binding;
    RvDisplayPhotosPostAdapter adapter;
    String currntUserID;
    SharedPreferences sp;
    Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        getPost();
        getUser();
        isFav();


        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFav();
            }
        });

        binding.unsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unFav();
            }
        });

    }

    private void fixed(){
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currntUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(RvDisplayPostAdapter.POST_KEY);

    }

    private void getPost(){
        adapter = new RvDisplayPhotosPostAdapter(post.getImages()
                , new OnClickItemImagePostListener() {
            @Override
            public void OnClickListener(ArrayList<String> images) {
                Intent intent = new Intent(PostDetailsActivity.this
                        , DisplayAllImagesPostActivity.class);
                intent.putExtra(RvDisplayPostAdapter.IMAGES_POST_KEY,images);
                startActivity(intent);
            }
        });
        binding.rv.setHasFixedSize(true);
        LinearLayoutManager horizontalLayoutManagaer =
                new LinearLayoutManager(this
                        , LinearLayoutManager.HORIZONTAL, false);
        binding.rv.setLayoutManager(horizontalLayoutManagaer);
        binding.rv.setAdapter(adapter);

        binding.tvPrice.setText(String.valueOf(post.getPrice()));
        binding.tvTitle.setText(post.getTitle());
        binding.tvDescription.setText(post.getDescription());
        getDate(post,binding.tvDate,binding.date);
        getPublisherInfo();

    }

    private void getUser(){
        FirebaseFirestore.getInstance().collection("Users")
                .document(post.getPublisher()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = documentSnapshot.toObject(User.class);
                        binding.tvAddress.setText(user.getAddress());
//                            iv_profile.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent intent = new Intent(context,OtherProfileActivity.class);
//                                    intent.putExtra(MainActivity.USER_KEY,user);
//                                    context.startActivity(intent);
//                                }
//                            });

//                            tv_username.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent intent = new Intent(context,OtherProfileActivity.class);
//                                    intent.putExtra(MainActivity.USER_KEY,user);
//                                    context.startActivity(intent);
//                                }
//                            });
                    }
                });
    }

    private void getPublisherInfo(){
        FirebaseFirestore.getInstance().collection("Users")
                .document(post.getPublisher()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            Glide.with(PostDetailsActivity.this).load(user.getImageProfile())
                                    .placeholder(R.drawable.ic_user4)
                                    .into(binding.ivProfile);
                            binding.tvUsername.setText(user.getFullName());
                        }
                    }
                });
    }

    private void createFav(){
        binding.save.setEnabled(false);
        binding.unsave.setEnabled(false);
        Favorite favorite = new Favorite(currntUserID
                ,post.getPostId()
                ,Calendar.getInstance().getTime());
        DocumentReference documentReference =
                FirebaseFirestore.getInstance().collection("Favorite").document();
        favorite.setId(documentReference.getId());
        documentReference.set(favorite);
        binding.save.setVisibility(View.GONE);
        binding.unsave.setVisibility(View.VISIBLE);
        binding.bookmark.setText(R.string.unsave);
        binding.save.setEnabled(true);
        binding.unsave.setEnabled(true);
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();

    }

    private void isFav(){
        FirebaseFirestore
                .getInstance()
                .collection("Favorite")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        binding.save.setEnabled(false);
                        binding.unsave.setEnabled(false);
                        Favorite favorite = queryDocumentSnapshot.toObject(Favorite.class);
                        if (favorite.getId_user().equals(currntUserID)
                                && favorite.getId_post().equals(post.getPostId())){
                            binding.unsave.setVisibility(View.VISIBLE);
                            binding.save.setVisibility(View.GONE);
                            binding.bookmark.setText(R.string.unsave);
                            binding.save.setEnabled(true);
                            binding.unsave.setEnabled(true);
                            return;
                        }else {
                            binding.bookmark.setText(R.string.save);
                            binding.unsave.setVisibility(View.GONE);
                            binding.save.setVisibility(View.VISIBLE);
                            binding.save.setEnabled(true);
                            binding.unsave.setEnabled(true);
                        }
                    }
                }
            }
        });
    }

    private void unFav(){
        binding.save.setEnabled(false);
        binding.unsave.setEnabled(false);
        FirebaseFirestore.getInstance().collection("Favorite")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        Favorite favorite = queryDocumentSnapshot.toObject(Favorite.class);
                        if (favorite.getId_post().equals(post.getPostId())
                                && currntUserID.equals(favorite.getId_user())){
                            FirebaseFirestore.getInstance().collection("Favorite")
                                    .document(favorite.getId()).delete();
                            binding.save.setEnabled(true);
                            binding.unsave.setEnabled(true);
                            binding.save.setVisibility(View.VISIBLE);
                            binding.unsave.setVisibility(View.GONE);
                            binding.bookmark.setText(R.string.save);
                            Toast.makeText(getBaseContext(), R.string.unsave, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }

    private String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd / hh:mm aa", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String previousYears(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String year(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String currentYear = simpleDateFormat.format(date);
        return currentYear;
    }

    private String month(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String day(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
        String currentDay = simpleDateFormat.format(date);
        return currentDay;
    }

    private String hour(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String minute(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("m", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String second(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("s", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private void getDate(Post post, TextView tv_date, TextView date){
        Date currentDate = Calendar.getInstance().getTime();
        //currentYear
        String currentYear = year(currentDate);
        //PostYear
        String postYear = year(post.getDatenews());

        //currentMonth
        String currentMonth = month(currentDate);
        //PostMonth
        String postMonth = month(post.getDatenews());

        //currentDay
        String currentDay = day(currentDate);
        //PostDay
        String postDay = day(post.getDatenews());

        //currentHour
        String currentHour = hour(currentDate);
        //PostHour
        String postHour = hour(post.getDatenews());

        //currentMinute
        String currentMinute = minute(currentDate);
        //postMinute
        String postMinute = minute(post.getDatenews());

        //currentSecond
        String currentSecond = second(currentDate);
        //postSecond
        String postSecond = second(post.getDatenews());

        if (currentYear.equals(postYear)){
            if (currentMonth.equals(postMonth)){
                if (currentDay.equals(postDay)){
                    if (currentHour.equals(postHour)){
                        if (currentMinute.equals(postMinute)){
                            int res = Integer.parseInt(currentSecond) - Integer.parseInt(postSecond);
                            if (res == 1){
                                date.setText(R.string.second_ago);
                            }else {
                                date.setText(R.string.seconds_ago);
                            }
                            tv_date.setText(String.valueOf(res));
                        }else {
                            int res = Integer.parseInt(currentMinute) - Integer.parseInt(postMinute);
                            if (res == 1){
                                date.setText(R.string.minute_ago);
                            }else {
                                date.setText(R.string.minutes_ago);
                            }
                            tv_date.setText(String.valueOf(res));
                        }
                    }else {
                        int res = Integer.parseInt(currentHour) - Integer.parseInt(postHour);
                        if (res == 1){
                            date.setText(R.string.hour_ago);
                        }else {
                            date.setText(R.string.hourss_ago);
                        }
                        tv_date.setText(String.valueOf(res));
                    }
                }else {
                    tv_date.setText(formatDate(post.getDatenews()));
                }
            }else {
                tv_date.setText(formatDate(post.getDatenews()));
            }
        }else {
            tv_date.setText(previousYears(post.getDatenews()));
        }
    }

}