package com.khaled.donation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Adapters.RvPostsProfileAdapter;
import com.khaled.donation.Listeners.GetPost;
import com.khaled.donation.Listeners.OnClickMenuPostListener;
import com.khaled.donation.Models.Comment;
import com.khaled.donation.Models.Like;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityPostsBinding;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class PostsActivity extends AppCompatActivity {
    ActivityPostsBinding binding;
    ArrayList<Post> posts;
    RvDisplayPostAdapter adapter;
    SharedPreferences sp;
    String currentUserID;
    User currentUser;
    int count_posts;
    NetworkInfo netInfo;
    public static boolean isUploaded;
    String id_user;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        getPosts();
        getCountPosts();

    }

    private void fixed() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        Intent intent = getIntent();
        position = intent.getIntExtra(RvPostsProfileAdapter.POSITION_KEY,0);
        id_user = intent.getStringExtra(RvPostsProfileAdapter.ID_USER_KEY);
        if (id_user != null){
            currentUserID = id_user;
        }
        posts = new ArrayList<>();
        ConnectivityManager conMgr =  (ConnectivityManager)getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getPosts(){
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("Posts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                posts.clear();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    Post post = queryDocumentSnapshot.toObject(Post.class);
                    if (post.getPublisher().equals(currentUserID)){
                        posts.add(post);
                    }
                }
                adapter = new RvDisplayPostAdapter(PostsActivity.this
                        , posts, new OnClickMenuPostListener() {
                    @Override
                    public void OnClickMenuPostListener(Post post, ImageView iv_menuPost) {
                        PopupMenu popupMenu = new PopupMenu(PostsActivity.this, iv_menuPost);
                        popupMenu.inflate(R.menu.menu_post);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.edit:
                                        if (netInfo == null) {
                                            dialogInternet_error();
                                        } else {
                                            if (isUploaded == false) {
                                                Intent intent = new Intent(getBaseContext()
                                                        , AddPhotoActivity.class);
                                                intent.putExtra(RvDisplayPostAdapter.POST_KEY, post);
                                                startActivity(intent);
                                            } else {
                                                Toasty.info(getBaseContext(), R.string.toast_moment
                                                        , Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        return true;
                                    case R.id.delete:
                                        if (netInfo == null) {
                                            dialogInternet_error();
                                        } else {
                                            androidx.appcompat.app.AlertDialog.Builder builder
                                                    = new AlertDialog.Builder(PostsActivity.this);
                                            builder.setTitle(R.string.delete);
                                            builder.setMessage(R.string.delete_post);
                                            builder.setPositiveButton(R.string.ok
                                                    , new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            count_posts = count_posts - 1;
                                                            for (int a = 0; a < post.getImages().size(); a++) {
                                                                FirebaseStorage
                                                                        .getInstance()
                                                                        .getReferenceFromUrl(post.getImages().get(a))
                                                                        .delete();
                                                            }
                                                            deleteLikes(post);
                                                            deleteComments(post);
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
                }, new GetPost() {
                    @Override
                    public void getPostListener(Post post) {
                        Intent intent = new Intent(getBaseContext(), PostDetailsActivity.class);
                        intent.putExtra(RvDisplayPostAdapter.POST_KEY,post);
                        finish();
                        startActivityForResult(intent,AllFragment.POST_DETAILS_REQ_CODE);
                    }
                });
                RecyclerView.SmoothScroller smoothScroller =
                        new LinearSmoothScroller(PostsActivity.this){
                    @Override
                    protected int getVerticalSnapPreference() {
                        return LinearSmoothScroller.SNAP_TO_ANY;
                    }
                };
                smoothScroller.setTargetPosition(position);
                binding.rv.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager
                        = new LinearLayoutManager(PostsActivity.this);
                binding.rv.setLayoutManager(linearLayoutManager);
                binding.rv.setAdapter(adapter);
                linearLayoutManager.startSmoothScroll(smoothScroller);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getCountPosts(){
        FirebaseFirestore.getInstance().collection("Users")
                .document(currentUserID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        currentUser = documentSnapshot.toObject(User.class);
                        count_posts = currentUser.getPosts();
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

    private void dialogInternet_error() {
        new AlertDialog.Builder(getBaseContext())
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AllFragment.POST_DETAILS_REQ_CODE){
            finish();
        }
    }

}