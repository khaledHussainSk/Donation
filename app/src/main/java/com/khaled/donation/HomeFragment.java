package com.khaled.donation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.khaled.donation.Adapters.RvDisplayPostAdapter;
import com.khaled.donation.Listeners.OnClickMenuPostListener;
import com.khaled.donation.Models.Comment;
import com.khaled.donation.Models.Like;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.FragmentHomeBinding;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    RvDisplayPostAdapter adapter;
    ArrayList<Post> posts;
    SharedPreferences sp;
    String currentUserID;
    User currentUser;
    int count_posts;
    NetworkInfo netInfo;
    public static boolean isUploaded;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(),container,false);

        fixed();
        getPosts();
        getCountPosts();

        return binding.getRoot();
    }

    private void fixed() {
        ConnectivityManager conMgr =  (ConnectivityManager)getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        posts = new ArrayList<>();
    }

    private void getPosts(){
        binding.spinKit.setVisibility(View.VISIBLE);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        FirebaseFirestore.getInstance().collection("Posts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                posts.clear();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    Post post = queryDocumentSnapshot.toObject(Post.class);
                    posts.add(post);
                }
                adapter = new RvDisplayPostAdapter(getActivity(), posts, new OnClickMenuPostListener() {
                    @Override
                    public void OnClickMenuPostListener(Post post, ImageView iv_menuPost) {
                        PopupMenu popupMenu = new PopupMenu(getActivity(), iv_menuPost);
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
                                                Intent intent = new Intent(getActivity()
                                                        ,AddPhotoActivity.class);
                                                intent.putExtra(RvDisplayPostAdapter.POST_KEY, post);
                                                startActivity(intent);
                                            }else {
                                                Toasty.info(getActivity(),R.string.toast_moment
                                                        ,Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        return true;
                                    case R.id.delete:
                                        if (netInfo == null){
                                            dialogInternet_error();
                                        }else{
                    androidx.appcompat.app.AlertDialog.Builder builder
                            = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.delete);
                    builder.setMessage(R.string.delete_post);
                    builder.setPositiveButton(R.string.ok
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    count_posts = count_posts - 1;
                                    for (int a=0 ;a<post.getImages().size();a++){
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
                                    Toast.makeText(getActivity()
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
                });
                binding.rv.setHasFixedSize(true);
                binding.rv.setLayoutManager(new LinearLayoutManager(getActivity()));
                binding.rv.setAdapter(adapter);
                binding.spinKit.setVisibility(View.GONE);
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
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
    }

}