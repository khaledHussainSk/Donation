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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.LikesActivity;
import com.khaled.donation.Listeners.GetPost;
import com.khaled.donation.Listeners.OnClickMenuPostListener;
import com.khaled.donation.MainActivity;
import com.khaled.donation.Models.Like;
import com.khaled.donation.Models.Notifications;
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
    GetPost getPost;
    Context context;
    ArrayList<Post> posts;
    OnClickMenuPostListener listener;
    String currntUserID;
    SharedPreferences sp;
    View v;
    CustomPhotoPostBinding binding;
    int sum;
    NetworkInfo netInfo;
    public static final String POST_KEY = "POST_KEY";

    public RvDisplayPostAdapter(Context context, ArrayList<Post> posts, OnClickMenuPostListener listener
    ,GetPost getPost) {
        this.context = context;
        this.posts = posts;
        this.listener = listener;
        this.getPost = getPost;
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

    @Override
    public int getItemViewType(int position) {
        return position;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPost.getPostListener(post);
                }
            });

        }

        private void bind(Post post){
            this.post = post;

            fixed(post);

            ImageView ic_like = binding.icLike;
            ImageView ic_liked = binding.icLiked;
            ImageView iv_post = binding.ivPost;
            TextView tv_likes = binding.tvLikes;
            TextView tv_address = binding.tvAddress;
            TextView tv_title = binding.tvTitle;
            TextView tv_price = binding.tvPrice;
            TextView tv_date = binding.tvDate;
            TextView date = binding.date;


            transfers(post,tv_likes);
            getDate(post,tv_date,date);
            if (!post.getCategory().equals("حملات")){
                Glide.with(context).load(post.getImages().get(0))
                        .placeholder(R.drawable.ic_loading)
                        .into(iv_post);
            }
            tv_title.setText(post.getTitle());
            tv_price.setText(String.valueOf(post.getPrice()));

            sum = post.getLikes();

            islike(post,ic_like,ic_liked,tv_likes);
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

            FirebaseFirestore.getInstance().collection("Users")
                    .document(post.getPublisher()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            tv_address.setText(user.getAddress());
                            if (post.getCategory().equals("حملات")){
                                Glide.with(context).load(user.getImageProfile())
                                        .placeholder(R.drawable.ic_loading)
                                        .into(iv_post);

                            }
                        }
                    });
        }

    }

    private void fixed(Post post){
        ConnectivityManager conMgr =  (ConnectivityManager)context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
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

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
        String hour = hour(Calendar.getInstance().getTime());
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        String date =year +"-"+ month + "-" + day +" " + hour +":" +minute +":"+ second;

        Notifications notifications = new Notifications(post.getPostId()
                ,"Like",post.getPublisher(),currntUserID,date);
        DocumentReference documentReferenceNOt = FirebaseFirestore.getInstance()
                .collection("Notifications").document();
        notifications.setId(documentReferenceNOt.getId());

        documentReferenceNOt.set(notifications).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, e.getMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void islike(Post post,ImageView ic_like
            ,ImageView ic_liked,TextView tv_likes){
        tv_likes.setText(String.valueOf(post.getLikes()));
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

    private void transfers(Post post, TextView tv_likes){

            tv_likes.setOnClickListener(new View.OnClickListener() {
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
