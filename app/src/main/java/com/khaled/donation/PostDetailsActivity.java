package com.khaled.donation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.khaled.donation.Adapters.RvDisplayPhotosPostAdapter;
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Adapters.RvDisplayVideosPostAdapter;
import com.khaled.donation.Listeners.OnClickItemImagePostListener;
import com.khaled.donation.Models.Comment;
import com.khaled.donation.Models.Favorite;
import com.khaled.donation.Models.Friend;
import com.khaled.donation.Models.Like;
import com.khaled.donation.Models.Notifications;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityPostDetailsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class PostDetailsActivity extends AppCompatActivity {
    ActivityPostDetailsBinding binding;
    RvDisplayPhotosPostAdapter adapter;
    RvDisplayVideosPostAdapter adapterVideo;
    String currntUserID;
    SharedPreferences sp;
    Post post;
    NetworkInfo netInfo;
    public static boolean isUploaded;
    int count_posts;
    User currentUser;
    int following;
    int followers;
    String id_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        getCurrentUser();

        binding.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else {
                    createFav();
                }
            }
        });

        binding.unsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else {
                    unFav();
                }
            }
        });

        binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostDetailsActivity.this, CommentsActivity.class);
                intent.putExtra(RvDisplayPostAdapter.POST_KEY,post);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        binding.icMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else {
                    showPopupMenu();
                }
            }
        });

    }

    private void fixed(){
        ConnectivityManager conMgr =  (ConnectivityManager)this
                .getSystemService(CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currntUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(RvDisplayPostAdapter.POST_KEY);
        if (post == null){
            Favorite favorite = (Favorite) intent.getSerializableExtra(FavoriteActivity.FAV_KEY);
            if (favorite == null){
                id_post = intent.getStringExtra(NotificationsFragment.NOTIFICATION_KEY);
                getPostNotification();
            }else {
                getPostFromFav(favorite);
            }
        }else {
            getPost();
            getUser();
            isFav();
        }

    }

    private void getPostFromFav(Favorite favorite) {
        FirebaseFirestore.getInstance().collection("Posts")
                .document(favorite.getId_post()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Post postFromFav = documentSnapshot.toObject(Post.class);
                        post = postFromFav;
                        getPost();
                        getUser();
                        isFav();
                    }
                });
    }

    private void getPostNotification() {
        Toast.makeText(getApplicationContext(), ""+id_post, Toast.LENGTH_SHORT).show();
        FirebaseFirestore.getInstance().collection("Posts")
                .document(id_post).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Post postFromNot = documentSnapshot.toObject(Post.class);
                        post = postFromNot;
                        getPost();
                        getUser();
                        isFav();
                    }
                });
    }

    private void getPost(){
        if (post.getPostType().equals("image")) {
            binding.rv.setVisibility(View.VISIBLE);
            binding.ivProf.setVisibility(View.GONE);
            binding.dollar.setVisibility(View.VISIBLE);
            binding.tvPrice.setVisibility(View.VISIBLE);
            binding.tvDonationLink.setVisibility(View.GONE);
            binding.tvLink.setVisibility(View.GONE);
            adapter = new RvDisplayPhotosPostAdapter(post.getImages()
                    , new OnClickItemImagePostListener() {
                @Override
                public void OnClickListener(ArrayList<String> images) {
                    Intent intent = new Intent(PostDetailsActivity.this
                            , DisplayAllImagesPostActivity.class);
                    intent.putExtra(RvDisplayPostAdapter.IMAGES_POST_KEY, images);
                    startActivity(intent);
                }
            });
            binding.rv.setHasFixedSize(true);
            LinearLayoutManager horizontalLayoutManagaer =
                    new LinearLayoutManager(this
                            , LinearLayoutManager.HORIZONTAL, false);
            binding.rv.setLayoutManager(horizontalLayoutManagaer);
            binding.rv.setAdapter(adapter);
        }else if (post.getPostType().equals("video")){
            binding.rv.setVisibility(View.VISIBLE);
            binding.ivProf.setVisibility(View.GONE);
            binding.dollar.setVisibility(View.VISIBLE);
            binding.tvPrice.setVisibility(View.VISIBLE);
            binding.tvDonationLink.setVisibility(View.GONE);
            binding.tvLink.setVisibility(View.GONE);
            adapterVideo = new RvDisplayVideosPostAdapter(post.getImages());
            binding.rv.setHasFixedSize(true);
            LinearLayoutManager horizontalLayoutManagaer =
                    new LinearLayoutManager(this
                            , LinearLayoutManager.HORIZONTAL, false);
            binding.rv.setLayoutManager(horizontalLayoutManagaer);
            binding.rv.setAdapter(adapterVideo);
        }else {
            FirebaseFirestore.getInstance().collection("Users")
                    .document(post.getPublisher()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            binding.rv.setVisibility(View.GONE);
                            binding.ivProf.setVisibility(View.VISIBLE);
                            Glide.with(PostDetailsActivity.this)
                                    .load(user.getImageProfile())
                                    .placeholder(R.drawable.ic_loading)
                                    .into(binding.ivProf);
                            binding.dollar.setVisibility(View.GONE);
                            binding.tvPrice.setVisibility(View.GONE);
                            binding.tvDonationLink.setVisibility(View.VISIBLE);
                            binding.tvLink.setVisibility(View.VISIBLE);
                            binding.tvDonationLink.setText(post.getDonation_link());
                            binding.tvDonationLink.setMovementMethod(LinkMovementMethod.getInstance());
                        }
                    });
        }

        binding.tvPrice.setText(String.valueOf(post.getPrice()));
        binding.tvTitle.setText(post.getTitle());
        if (post.getDescription().equals("")){
            binding.tvDescription.setText(R.string.noDescription);
        }else {
            binding.tvDescription.setText(post.getDescription());
        }
        getDate(post,binding.tvDate,binding.date);
        getPublisherInfo();
        if (post.getPublisher().equals(currntUserID)){
            binding.icMore.setVisibility(View.VISIBLE);
        }else{
            binding.icMore.setVisibility(View.GONE);
        }

    }

    private void getUser(){
        FirebaseFirestore.getInstance().collection("Users")
                .document(post.getPublisher()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = documentSnapshot.toObject(User.class);

                        followers = user.getFollowers();

                        isFollow(user);

                        binding.btnFollow.setOnClickListener(view -> {
                            if (netInfo == null){
                                dialogInternet_error();
                            }else {
                                follow(user);
                            }
                        });

                        if (user.getIdUser().equals(currntUserID)){
                            binding.btnFollow.setVisibility(View.GONE);
                            binding.cardMessage.setVisibility(View.GONE);
                        }else {
                            binding.btnFollow.setVisibility(View.VISIBLE);
                            binding.cardMessage.setVisibility(View.VISIBLE);
                        }

                        binding.tvAddress.setText(user.getAddress());

                        binding.ivProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openOtherProfile(user);
                            }
                        });

                        binding.tvUsername.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openOtherProfile(user);
                            }
                        });

                        binding.tvSeeProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                openOtherProfile(user);
                            }
                        });

                        binding.btnSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent i = new Intent(getApplicationContext(),ChatActivity.class);
                                i.putExtra("message",binding.etMessage.getText().toString());
                                i.putExtra("name",user.getFullName());
                                i.putExtra("uid",user.getIdUser());
                                startActivity(i);
                            }
                        });

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
                            if (task.getResult() != null){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                User user = documentSnapshot.toObject(User.class);
                                Glide.with(PostDetailsActivity.this).load(user.getImageProfile())
                                        .placeholder(R.drawable.ic_user4)
                                        .into(binding.ivProfile);
                                binding.tvUsername.setText(user.getFullName());
                            }
                        }
                    }
                });
    }

    private void openOtherProfile(User user){
        if (user.getIdUser().equals(currntUserID)){
            setResult(AllFragment.POST_DETAILS_REQ_CODE);
            finish();
        }else {
            Intent intent = new Intent(PostDetailsActivity.this
                    ,OtherProfileActivity.class);
            intent.putExtra(MainActivity.USER_KEY,user);
            startActivity(intent);
            finish();
        }
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
                            Toast.makeText(getBaseContext(), R.string.unSave, Toast.LENGTH_SHORT).show();
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

    private void dialogInternet_error() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
    }

    private void getCurrentUser(){
        FirebaseFirestore.getInstance().collection("Users")
                .document(currntUserID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        currentUser = documentSnapshot.toObject(User.class);
                        count_posts = currentUser.getPosts();
                        following = currentUser.getFollowing();
                    }
                });
    }

    private void deleteFav(Post post){
        FirebaseFirestore
                .getInstance()
                .collection("Favorite")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                Favorite favorite = queryDocumentSnapshot.toObject(Favorite.class);
                                if (post.getPostId().equals(favorite.getId_post())){
                                    FirebaseFirestore.getInstance().collection("Favorite")
                                            .document(favorite.getId()).delete();
                                }
                            }

                        }
                    }
                });
    }

    private void deleteComments(Post post) {
        FirebaseFirestore.getInstance().collection("Comments")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Comment comment = queryDocumentSnapshot.toObject(Comment.class);
                                if (comment.getId_post_().equals(post.getPostId())) {
                                    FirebaseFirestore.getInstance().collection("Comments")
                                            .document(comment.getId_comment()).delete();
                                }
                            }
                        }
                    }
                });
    }

    private void deleteLikes(Post post){
        FirebaseFirestore.getInstance().collection("Likes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                Like like = queryDocumentSnapshot.toObject(Like.class);
                                if (like.getId_post().equals(post.getPostId())){
                                    FirebaseFirestore.getInstance().collection("Likes")
                                            .document(like.getId_like()).delete();
                                }
                            }
                        }
                    }
                });
    }

    private void showPopupMenu(){
        PopupMenu popupMenu = new PopupMenu(PostDetailsActivity.this,binding.icMore);
        popupMenu.inflate(R.menu.menu_post);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        if (netInfo == null){
                            dialogInternet_error();
                        }else{
                            if (isUploaded == false){
                                if (post.getPostType().equals("image")){
                                    Intent intent = new Intent(PostDetailsActivity.this
                                            ,AddPhotoActivity.class);
                                    intent.putExtra(RvDisplayPostAdapter.POST_KEY, post);
                                    startActivity(intent);
                                }else if (post.getPostType().equals("video")){
                                    Intent intent = new Intent(PostDetailsActivity.this
                                            ,ActivityAddVideo.class);
                                    intent.putExtra(RvDisplayPostAdapter.POST_KEY, post);
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(PostDetailsActivity.this
                                            ,AddCharityCampaignActivity.class);
                                    intent.putExtra(RvDisplayPostAdapter.POST_KEY, post);
                                    startActivity(intent);
                                }

                            }else {
                                Toasty.info(PostDetailsActivity.this,R.string.toast_moment
                                        ,Toast.LENGTH_SHORT).show();
                            }
                        }
                        return true;
                    case R.id.delete:
                        if (netInfo == null){
                            dialogInternet_error();
                        }else{
                            androidx.appcompat.app.AlertDialog.Builder builder
                                    = new AlertDialog.Builder(PostDetailsActivity.this);
                            builder.setTitle(R.string.delete);
                            builder.setMessage(R.string.delete_post);
                            builder.setPositiveButton(R.string.ok
                                    , new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            count_posts = count_posts - 1;
                                            if (!post.getCategory().equals("حملات")){
                                                for (int a=0 ;a<post.getImages().size();a++){
                                                    FirebaseStorage
                                                            .getInstance()
                                                            .getReferenceFromUrl(post.getImages().get(a))
                                                            .delete();
                                                }
                                            }
                                            deleteLikes(post);
                                            deleteComments(post);
                                            deleteFav(post);
                                            FirebaseFirestore
                                                    .getInstance()
                                                    .collection("Posts")
                                                    .document(post.getPostId()).delete();
                                            FirebaseFirestore
                                                    .getInstance()
                                                    .collection("Users")
                                                    .document(post.getPublisher())
                                                    .update("posts", count_posts);
                                            Toast.makeText(getBaseContext()
                                                    , R.string.toast_post_delete
                                                    , Toast.LENGTH_SHORT).show();

                                        }
                                    });
                            builder.show();
                        }
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    void isFollow(User user){
        FirebaseFirestore.getInstance().collection("Friends").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            Friend friend = queryDocumentSnapshot.toObject(Friend.class);
                            if (friend.getId_follower().equals(user.getIdUser()) && friend
                                    .getId_following().equals(currntUserID)){
                                binding.btnFollow.setText(R.string.unfollow);
                                return;
                            }else{
                                binding.btnFollow.setText(R.string.follow);
                            }
                        }
                    }
                });
    }

    private void follow(User user){
        if (netInfo == null){
            dialogInternet_error();
        }else{
            if (binding.btnFollow.getText().toString()
                    .equals("Following") || binding.btnFollow.getText().toString()
                    .equals("يتابع")){
                //الغاء المتابعة
                binding.btnFollow.setEnabled(false);
                FirebaseFirestore.getInstance().collection("Friends").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                    Friend friend = queryDocumentSnapshot.toObject(Friend.class);
                                    if (friend.getId_follower().equals(user.getIdUser()) && friend
                                            .getId_following().equals(currntUserID)){
                                        FirebaseFirestore.getInstance().collection("Friends")
                                                .document(friend.getId())
                                                .delete();
                                        following -= 1;
                                        followers -= 1;
                                        FirebaseFirestore.getInstance().collection("Users")
                                                .document(currntUserID)
                                                .update("following",following);

                                        FirebaseFirestore.getInstance().collection("Users")
                                                .document(user.getIdUser())
                                                .update("followers",followers);
                                        getCurrentUser();
                                        getUser();
                                        binding.btnFollow.setText(R.string.follow);
                                        binding.btnFollow.setEnabled(true);
                                        return;
                                    }
                                }
                            }
                        });
            }else {
                //متابعة
                binding.btnFollow.setEnabled(false);
                Friend friend = new Friend(currntUserID,user.getIdUser()
                        ,Calendar.getInstance().getTime());
                DocumentReference documentReference =
                        FirebaseFirestore.getInstance().collection("Friends").document();
                friend.setId(documentReference.getId());
                documentReference.set(friend).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            following += 1;
                            followers += 1;

                            FirebaseFirestore.getInstance().collection("Users")
                                    .document(currntUserID)
                                    .update("following",following);

                            FirebaseFirestore.getInstance().collection("Users")
                                    .document(user.getIdUser())
                                    .update("followers",followers);

                            getUser();
                            getCurrentUser();
                            binding.btnFollow.setText(R.string.unfollow);
                            binding.btnFollow.setEnabled(true);

                            //إرسال إشعار المتابعة

                            Calendar now = Calendar.getInstance();
                            int year = now.get(Calendar.YEAR);
                            int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
                            int day = now.get(Calendar.DAY_OF_MONTH);
                            String hour = hour(Calendar.getInstance().getTime());
                            int minute = now.get(Calendar.MINUTE);
                            int second = now.get(Calendar.SECOND);

                            String date =year +"-"+ month + "-" + day +" " + hour +":" +minute +":"+ second;

                            Notifications notifications = new Notifications(user.getIdUser()
                                    ,"Follow",user.getIdUser(),currntUserID,date);
                            DocumentReference documentReferenceNOt = FirebaseFirestore.getInstance().collection("Notifications").document();
                            notifications.setId(documentReferenceNOt.getId());

                            documentReferenceNOt.set(notifications).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), "تم إرسال الأشعار", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "فشل إرسال الأشعار", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.btnFollow.setEnabled(true);
                        Toast.makeText(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

}