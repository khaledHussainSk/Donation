package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.khaled.donation.Adapters.RvDisplayCommentsAdapter;
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Listeners.ContextMenuCommentListener;
import com.khaled.donation.Models.Comment;
import com.khaled.donation.Models.Notifications;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityCommentsBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommentsActivity extends AppCompatActivity {
    ActivityCommentsBinding binding;
    public static final String COMENT_KEY = "COMENT_KEY";
    Post post;
    FirebaseFirestore store;
    SharedPreferences sp;
    String id_current_user;
    User current_user;
    RvDisplayCommentsAdapter adapter;
    Comment comment_;
    ActivityResultLauncher<Intent> arl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        getPublisherInfo(post);
        getCommentsWithIf();
        getUserInfo(id_current_user);

        arl = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result != null){
                            getComments(post);
                        }
                    }
                });

        binding.tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String comment = binding.etComment.getText().toString();

                if (TextUtils.isEmpty(comment) || comment.equals("")){
                    Toast.makeText(getBaseContext(), R.string.toast_empty_comment
                            , Toast.LENGTH_SHORT).show();
                    return;
                }

                addComment(post,comment);

            }
        });

    }

    private void fixed() {
        Intent intent = getIntent();
        post = (Post) intent.getSerializableExtra(RvDisplayPostAdapter.POST_KEY);
        store = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        id_current_user = sp.getString(MainActivity.USER_ID_KEY,null);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void getPublisherInfo(Post post){
        store.collection("Users").document(post.getPublisher())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    User user = documentSnapshot.toObject(User.class);
                    Glide.with(CommentsActivity.this).load(user.getImageProfile())
                            .placeholder(R.drawable.ic_user4).into(binding.ivPublisher);
                    binding.tvPublisher.setText(user.getFullName());
                    binding.tvDescription.setText(post.getTitle());
                    getDate(post,binding.tvDate,binding.date);
                }
            }
        });
    }

    private void getUserInfo(String id_user){
        store.collection("Users").document(id_user).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            current_user = documentSnapshot.toObject(User.class);
                            Glide.with(CommentsActivity.this).load(current_user.getImageProfile())
                                    .placeholder(R.drawable.ic_user4).into(binding.ivProfile);
                        }
                    }
                });
    }

    private void addComment(Post post,String comment_){
        binding.tvPost.setEnabled(false);
        Comment comment = new Comment(post.getPostId(),id_current_user,comment_
                , Calendar.getInstance().getTime());
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection("Comments")
                .document();
        comment.setId_comment(documentReference.getId());
        documentReference.set(comment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            FirebaseFirestore.getInstance().collection("Posts")
                                    .document(post.getPostId())
                                    .update("comments",post.getComments() + 1);
                            binding.etComment.setText("");
                            getComments(post);
                            getPost(post);
                            binding.tvPost.setEnabled(true);
                        }
                    }
                });

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1; // Note: zero based!
        int day = now.get(Calendar.DAY_OF_MONTH);
//        int hour = now.get(Calendar.HOUR);
        String hour = hour(Calendar.getInstance().getTime());
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);

        String date =year +"-"+ month + "-" + day +" " + hour +":" +minute +":"+ second;
//        String date =year +"-"+ month + "-" + day +" " + hour+":"+ second;

        Notifications notifications = new Notifications(post.getPostId()
                ,"Comment",post.getPublisher(),id_current_user,date);
        DocumentReference documentReferenceNOt = FirebaseFirestore.getInstance()
                .collection("Notifications").document();
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

    private void getComments(Post post){
        binding.progressBar.setVisibility(View.VISIBLE);
        store.collection("Comments").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            ArrayList<Comment> comments = new ArrayList<>();
                            comments.clear();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                Comment comment = queryDocumentSnapshot.toObject(Comment.class);
                                if (comment.getId_post_().equals(post.getPostId())){
                                    comments.add(comment);
                                }
                            }
                            adapter =
                                    new RvDisplayCommentsAdapter(getBaseContext(),comments
                                            , new ContextMenuCommentListener() {
                                @Override
                                public void contextMenuCommentListener(Comment comment, View view) {
                                    comment_ = comment;
                                    if (id_current_user.equals(comment_.getId_publisher())){
                                        registerForContextMenu(view);
                                    }
                                }
                            });
                            binding.rv.setHasFixedSize(true);
                            binding.rv.setLayoutManager(new LinearLayoutManager
                                    (CommentsActivity.this));
                            binding.progressBar.setVisibility(View.GONE);
                            binding.rv.setAdapter(adapter);
                            store.collection("Posts").document(post.getPostId()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()){
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                Post post1 = documentSnapshot.toObject(Post.class);
                                                if (post1.getComments() > 0){
                                                    binding.tvNoCommentsYet.setVisibility(View.GONE);
                                                    binding.tvStartTheConversation.setVisibility(View.GONE);
                                                }else {
                                                    binding.tvNoCommentsYet.setVisibility(View.VISIBLE);
                                                    binding.tvStartTheConversation.setVisibility(View.VISIBLE);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void deleteComment(Comment comment,Post post){
        binding.rv.setEnabled(false);
        store.collection("Comments").document(comment.getId_comment()).delete();
        store.collection("Posts").document(post.getPostId())
                .update("comments",post.getComments() - 1);
        getComments(post);
        getPost(post);
    }

    private void getPost(Post post_){
        store.collection("Posts").document(post_.getPostId()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Post post1 = documentSnapshot.toObject(Post.class);
                            post = post1;
                            binding.rv.setEnabled(true);
                        }
                    }
                });
    }

    private void getCommentsWithIf(){
        if (post.getComments() > 0){
            binding.tvNoCommentsYet.setVisibility(View.GONE);
            binding.tvStartTheConversation.setVisibility(View.GONE);
            getComments(post);
        }else {
            binding.tvNoCommentsYet.setVisibility(View.VISIBLE);
            binding.tvStartTheConversation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_comment,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_comment:
                deleteComment(comment_,post);
                return true;
            case R.id.edit_comment:
                Intent intent = new Intent(CommentsActivity.this
                        ,EditCommentActivity.class);
                intent.putExtra(COMENT_KEY,comment_);
                arl.launch(intent);
                return true;
        }
        return false;
    }

    private String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd hh:mm aa", Locale.ENGLISH);
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
