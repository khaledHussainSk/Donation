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
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityCommentsBinding;

import java.util.ArrayList;
import java.util.Calendar;

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
        getPublisherInfoWithIf();
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
//                    Toast.makeText(getBaseContext(), R.string.toast_empty_comment, Toast.LENGTH_SHORT).show();
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
                    Glide.with(getBaseContext()).load(user.getImageProfile())
                            .placeholder(R.drawable.ic_user4).into(binding.ivPublisher);
                    binding.tvPublisher.setText(user.getFullName());
                    binding.tvDescription.setText(post.getDescription());
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
                            Glide.with(getBaseContext()).load(current_user.getImageProfile())
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
                            binding.rv.setLayoutManager(new LinearLayoutManager(getBaseContext()));
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

    private void getPublisherInfoWithIf(){
        if (post.getDescription().equals("")){
            binding.postLayout.setVisibility(View.GONE);
        }else {
            binding.postLayout.setVisibility(View.VISIBLE);
            getPublisherInfo(post);
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
                Intent intent = new Intent(getBaseContext(),EditCommentActivity.class);
                intent.putExtra(COMENT_KEY,comment_);
                arl.launch(intent);
                return true;
        }
        return false;
    }
}