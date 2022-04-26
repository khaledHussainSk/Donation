package com.khaled.donation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.CommentsActivity;
import com.khaled.donation.DisplayAllImagesPostActivity;
import com.khaled.donation.LikesActivity;
import com.khaled.donation.Listeners.GetPost;
import com.khaled.donation.Listeners.OnClickItemImagePostListener;
import com.khaled.donation.Listeners.OnClickMenuPostListener;
import com.khaled.donation.MainActivity;
import com.khaled.donation.Models.Favorite;
import com.khaled.donation.Models.Like;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomPhotoPostBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RvDisplayPostAdapter
        extends RecyclerView.Adapter<RvDisplayPostAdapter.RvDisplayPostAdapterHolder> {
    public static final String IMAGES_POST_KEY = "IMAGES_POST_KEY";
    Context context;
    ArrayList<Post> posts;
    OnClickMenuPostListener listener;

    String currntUserID;
    SharedPreferences sp;
    View v;
    RvDisplayPhotosPostAdapter adapter;
    CustomPhotoPostBinding binding;
    int sum;
    NetworkInfo netInfo;
    public static final String POST_KEY = "POST_KEY";

    public RvDisplayPostAdapter(Context context, ArrayList<Post> posts, OnClickMenuPostListener listener) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        notifyDataSetChanged();
        this.posts = posts;
    }

    @NonNull
    @Override
    public RvDisplayPostAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvDisplayPostAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_photo_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDisplayPostAdapterHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class RvDisplayPostAdapterHolder extends RecyclerView.ViewHolder{
        Post post;
        public RvDisplayPostAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomPhotoPostBinding.bind(itemView);
            v = itemView;
            sp = PreferenceManager.getDefaultSharedPreferences(context);
            currntUserID = sp.getString(MainActivity.USER_ID_KEY,null);
            ConnectivityManager conMgr =  (ConnectivityManager)context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = conMgr.getActiveNetworkInfo();

        }

        private void bind(Post post){
            this.post = post;

            fixed(post);

            ImageView iv_menuPost = binding.ivMenuPost;
            ImageView save = binding.save;
            ImageView unsave = binding.unsave;
            ImageView ic_like = binding.icLike;
            ImageView ic_liked = binding.icLiked;
            TextView tv_likes = binding.tvLikes;
            TextView likes = binding.likes;
            ImageView comments = binding.comment;
            TextView tv_comments = binding.tvComments;
            TextView tv_date = binding.tvDate;
            TextView date = binding.date;
            ImageView iv_profile = binding.ivProfile;
            TextView tv_username = binding.tvUsername;
            TextView tv_publisher = binding.tvPublisher;
            RecyclerView rv = binding.rv;

            isFav(save,unsave,post);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (netInfo == null){
                        dialogInternet_error();
                    }else{
                        createFav(post,save,unsave);
                    }
                }
            });
            unsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (netInfo == null){
                        dialogInternet_error();
                    }else{
                        unFav(post,save,unsave);
                    }
                }
            });

            transfers(post,tv_comments,tv_likes,likes);
            getDate(post,tv_date,date);

            iv_menuPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickMenuPostListener(post,iv_menuPost);
                }
            });

            if (post.getPublisher().equals(currntUserID)){
                binding.ivMenuPost.setVisibility(View.VISIBLE);
            }else{
                binding.ivMenuPost.setVisibility(View.GONE);
            }

            comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, CommentsActivity.class);
                    intent.putExtra(POST_KEY,post);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });

            tv_comments.setText("View All "+post.getComments()+" Comments");
            sum = post.getLikes();

            islike(post,ic_like,ic_liked,tv_likes,likes);
            ic_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (netInfo == null){
                        dialogInternet_error();
                    }else{
                        giveLike(post,tv_likes,ic_like,ic_liked);
                    }
                }
            });
            ic_liked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (netInfo == null){
                        dialogInternet_error();
                    }else{
                        cancelLike(post,tv_likes,ic_like,ic_liked);
                    }
                }
            });

            if (post.getDescription().equals("")){
                binding.tvDescription.setVisibility(View.GONE);
            }else {
                binding.tvDescription.setVisibility(View.VISIBLE);
                binding.tvDescription.setText(post.getDescription());
            }

            publisherInfo(post.getPublisher(),iv_profile,tv_username,tv_publisher);

            adapter = new RvDisplayPhotosPostAdapter(post.getImages(), new OnClickItemImagePostListener() {
                @Override
                public void OnClickListener(ArrayList<String> images) {
                    Intent intent = new Intent(context, DisplayAllImagesPostActivity.class);
                    intent.putExtra(IMAGES_POST_KEY,images);
                    context.startActivity(intent);
                }
            });
            rv.setHasFixedSize(true);
            LinearLayoutManager horizontalLayoutManagaer =
                    new LinearLayoutManager(context
                            , LinearLayoutManager.HORIZONTAL, false);
            rv.setLayoutManager(horizontalLayoutManagaer);
            rv.setAdapter(adapter);



        }

    }

    private void fixed(Post post){
        ConnectivityManager conMgr =  (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
    }

    private void publisherInfo(String publisher_id,ImageView iv_profile,
                               TextView tv_username,TextView tv_publisher){
        FirebaseFirestore.getInstance().collection("Users")
                .document(publisher_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            Glide.with(v).load(user.getImageProfile())
                                    .placeholder(R.drawable.ic_user4)
                                    .into(iv_profile);
                            tv_username.setText(user.getFullName());
                            tv_publisher.setText(user.getFullName());
                        }
                    }
                });
    }

    private void createFav(Post post,ImageView save,ImageView unsave){
        save.setEnabled(false);
        unsave.setEnabled(false);
        save.setVisibility(View.GONE);
        unsave.setVisibility(View.VISIBLE);
        Favorite favorite = new Favorite(currntUserID
                ,post.getPostId()
                ,Calendar.getInstance().getTime());
        DocumentReference documentReference =
                FirebaseFirestore.getInstance().collection("Favorite").document();
        favorite.setId(documentReference.getId());
        documentReference.set(favorite);
        save.setEnabled(true);
        unsave.setEnabled(true);
        Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show();

    }

    private void giveLike(Post post,TextView tv_likes,ImageView ic_like,ImageView ic_liked){
        ic_liked.setEnabled(false);
        ic_like.setEnabled(false);
        ic_liked.setVisibility(View.VISIBLE);
        ic_like.setVisibility(View.GONE);
        Like like = new Like(post.getPostId(),currntUserID,post.getPublisher()
                , Calendar.getInstance().getTime());
        DocumentReference documentReference =
                FirebaseFirestore.getInstance().collection("Likes").document();
        like.setId_like(documentReference.getId());
        documentReference.set(like);

        FirebaseFirestore.getInstance().collection("Posts").document(post.getPostId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                Post post1 = documentSnapshot.toObject(Post.class);
                FirebaseFirestore.getInstance().collection("Posts")
                        .document(post1.getPostId())
                        .update("likes",post1.getLikes() + 1);
                getPost1(post1, new GetPost() {
                    @Override
                    public void getPostListener(Post post) {
                        ic_liked.setEnabled(true);
                        ic_like.setEnabled(true);
                        tv_likes.setText(String.valueOf(post.getLikes()));
                    }
                });
            }
        });
    }

    private void isFav(ImageView save,ImageView unsave,Post post){
        FirebaseFirestore
                .getInstance()
                .collection("Favorite")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        Favorite favorite = queryDocumentSnapshot.toObject(Favorite.class);
                        if (favorite.getId_user().equals(currntUserID)
                                && favorite.getId_post().equals(post.getPostId())){
                            unsave.setVisibility(View.VISIBLE);
                            save.setVisibility(View.GONE);
                        }else {
                            unsave.setVisibility(View.GONE);
                            save.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    private void islike(Post post,ImageView ic_like
            ,ImageView ic_liked,TextView tv_likes,TextView likes){
        tv_likes.setText(String.valueOf(post.getLikes()));
        likes.setText(R.string.tv_likes);
        if (post.getLikes() > 0){
            FirebaseFirestore.getInstance().collection("Likes")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            Like like = queryDocumentSnapshot.toObject(Like.class);
                            if (like.getId_post().equals(post.getPostId())){
                                if (like.getId_who_gave_like().equals(currntUserID)){
                                    ic_liked.setVisibility(View.VISIBLE);
                                    ic_like.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    private void unFav(Post post,ImageView save,ImageView unsave){
        save.setEnabled(false);
        unsave.setEnabled(false);
        unsave.setVisibility(View.GONE);
        save.setVisibility(View.VISIBLE);
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
                            save.setEnabled(true);
                            unsave.setEnabled(true);
                            save.setVisibility(View.VISIBLE);
                            unsave.setVisibility(View.GONE);
                            Toast.makeText(context, R.string.unsave, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }

    private void cancelLike(Post post,TextView tv_likes,ImageView ic_like,ImageView ic_liked){
        ic_liked.setEnabled(false);
        ic_like.setEnabled(false);
        ic_liked.setVisibility(View.GONE);
        ic_like.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("Likes")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                        Like like = queryDocumentSnapshot.toObject(Like.class);
                        if (like.getId_post().equals(post.getPostId())){
                            if (like.getId_who_gave_like().equals(currntUserID)){
                                FirebaseFirestore.getInstance().collection("Posts")
                                        .document(post.getPostId()).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                Post post1 = documentSnapshot.toObject(Post.class);
                                                FirebaseFirestore.getInstance().collection("Posts")
                                                        .document(post1.getPostId())
                                                        .update("likes",post1.getLikes() - 1);
                                                getPost2(post1,like, new GetPost() {
                                                    @Override
                                                    public void getPostListener(Post post) {
                                                        ic_liked.setEnabled(true);
                                                        ic_like.setEnabled(true);
                                                        tv_likes.setText(String.valueOf(post.getLikes()));
                                                    }
                                                });
                                            }
                                        });
                            }
                        }
                    }
                }
            }
        });
    }
    private void getPost1(Post post,GetPost getPost){
        FirebaseFirestore.getInstance().collection("Posts")
                .document(post.getPostId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Post post = documentSnapshot.toObject(Post.class);
                        getPost.getPostListener(post);
                    }
                });
    }
    private void getPost2(Post post,Like like,GetPost getPost){
        FirebaseFirestore.getInstance().collection("Likes")
                .document(like.getId_like()).delete();

        FirebaseFirestore.getInstance().collection("Posts")
                .document(post.getPostId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Post post = documentSnapshot.toObject(Post.class);
                        getPost.getPostListener(post);
                    }
                });
    }

    private void transfers(Post post,TextView tv_comments, TextView tv_likes, TextView likes){

        tv_comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra(POST_KEY,post);
                context.startActivity(intent);
            }
        });

            tv_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, LikesActivity.class);
                    intent.putExtra(POST_KEY,post);
                    context.startActivity(intent);
                }
            });
            likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, LikesActivity.class);
                    intent.putExtra(POST_KEY,post);
                    context.startActivity(intent);
                }
            });
    }

    private void dialogInternet_error() {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(context.getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
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
