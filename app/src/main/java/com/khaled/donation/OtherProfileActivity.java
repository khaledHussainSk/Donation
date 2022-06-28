package com.khaled.donation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.RvPostsProfileAdapter;
import com.khaled.donation.Models.Friend;
import com.khaled.donation.Models.Notifications;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityOtherProfileBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class OtherProfileActivity extends AppCompatActivity {
    ActivityOtherProfileBinding binding;
    String id_current_user;
    User current_user;
    SharedPreferences sp;
    User user;
    int following;
    int followers;
    ArrayList<String> images;
    RvPostsProfileAdapter adapter;
    NetworkInfo netInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtherProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        getCurrentUser();
        follow();
        unFollow();
        getPosts();
        refreshFollowers();

        binding.postsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), PostsActivity.class);
                intent.putExtra(RvPostsProfileAdapter.ID_USER_KEY,RvPostsProfileAdapter.id_user);
                startActivity(intent);
            }
        });

        binding.followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), FollowersActivity.class);
                intent.putExtra(RvPostsProfileAdapter.ID_USER_KEY,RvPostsProfileAdapter.id_user);
                startActivity(intent);
            }
        });

        binding.followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), FollowingActivity.class);
                intent.putExtra(RvPostsProfileAdapter.ID_USER_KEY,RvPostsProfileAdapter.id_user);
                startActivity(intent);
            }
        });


    }

    private void fixed() {
        images = new ArrayList<>();
        ConnectivityManager conMgr =  (ConnectivityManager)getBaseContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        id_current_user = sp.getString(MainActivity.USER_ID_KEY,null);
        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(MainActivity.USER_KEY);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Glide.with(this).load(user.getImageProfile()).placeholder(R.drawable.ic_user4)
                .into(binding.ivProfile);
        binding.tvUsername.setText(user.getFullName());
        if (user.getShortBio().equals("Edit Bio")){
            binding.shortBio.setText(R.string.noBio);
        }else {
            binding.shortBio.setText(user.getShortBio());
        }
        binding.tvPosts.setText(String.valueOf(user.getPosts()));
        binding.tvFollowers.setText(String.valueOf(user.getFollowers()));
        binding.tvFollowing.setText(String.valueOf(user.getFollowing()));
        binding.btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ChatActivity.class);
                i.putExtra("name",user.getFullName());
                i.putExtra("uid",user.getIdUser());
                startActivity(i);
            }
        });
    }

    private void follow(){
        if (netInfo == null){
            dialogInternet_error();
        }else{
            binding.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.btnFollow.setEnabled(false);
                    Friend friend = new Friend(id_current_user,user
                            .getIdUser(),Calendar.getInstance().getTime());
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
                                        .document(id_current_user)
                                        .update("following",following);

                                FirebaseFirestore.getInstance().collection("Users")
                                        .document(user.getIdUser())
                                        .update("followers",followers);

                                getCurrentUser();
                                refreshFollowers();
                                binding.btnFollow.setVisibility(View.GONE);
                                binding.btnUnfollow.setVisibility(View.VISIBLE);
                                binding.btnUnfollow.setEnabled(true);

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.btnFollow.setEnabled(true);
                            Toast.makeText(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    Notifications notifications = new Notifications(user.getIdUser()
                            ,"Follow",user.getIdUser(),id_current_user,date);
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
            });
        }
    }

    private void getCurrentUser(){
        FirebaseFirestore.getInstance().collection("Users").document(id_current_user).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        current_user = documentSnapshot.toObject(User.class);
                        following = current_user.getFollowing();
                        isFollow();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void isFollow(){
        FirebaseFirestore.getInstance().collection("Friends").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            Friend friend = queryDocumentSnapshot.toObject(Friend.class);
                            if (friend.getId_follower().equals(user.getIdUser()) && friend
                                    .getId_following().equals(id_current_user)){
                                binding.btnUnfollow.setVisibility(View.VISIBLE);
                                binding.btnFollow.setVisibility(View.GONE);
                                return;
                            }else{
                                binding.btnUnfollow.setVisibility(View.GONE);
                                binding.btnFollow.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }

    void unFollow(){
        if (netInfo == null){
            dialogInternet_error();
        }else{
            binding.btnUnfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    binding.btnUnfollow.setEnabled(false);
                    FirebaseFirestore.getInstance().collection("Friends").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                        Friend friend = queryDocumentSnapshot.toObject(Friend.class);
                                        if (friend.getId_follower().equals(user.getIdUser()) && friend
                                                .getId_following().equals(id_current_user)){
                                            FirebaseFirestore.getInstance().collection("Friends")
                                                    .document(friend.getId())
                                                    .delete();
                                            following -= 1;
                                            followers -= 1;
                                            FirebaseFirestore.getInstance().collection("Users")
                                                    .document(id_current_user)
                                                    .update("following",following);

                                            FirebaseFirestore.getInstance().collection("Users")
                                                    .document(user.getIdUser())
                                                    .update("followers",followers);
                                            getCurrentUser();
                                            refreshFollowers();
                                            binding.btnFollow.setVisibility(View.VISIBLE);
                                            binding.btnUnfollow.setVisibility(View.GONE);
                                            binding.btnFollow.setEnabled(true);
                                            return;
                                        }
                                    }
                                }
                            });
                }
            });
        }
    }

    void refreshFollowers(){
        FirebaseFirestore.getInstance().collection("Users")
                .document(user.getIdUser()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = documentSnapshot.toObject(User.class);
                        followers = user.getFollowers();
                        binding.tvFollowers.setText(String.valueOf(user.getFollowers()));
                        binding.tvFollowing.setText(String.valueOf(user.getFollowing()));
                        binding.tvPosts.setText(String.valueOf(user.getPosts()));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPosts(){
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore.getInstance().collection("Posts")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                images.clear();
                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                    Post post = queryDocumentSnapshot.toObject(Post.class);
                    if (post.getPublisher().equals(user.getIdUser())){
                        images.add(post.getImages().get(0));
                    }
                }
                adapter = new RvPostsProfileAdapter(OtherProfileActivity.this,images
                        ,user.getIdUser());
                binding.rv.setHasFixedSize(true);
                binding.rv.setLayoutManager(new GridLayoutManager(OtherProfileActivity.this
                        ,3));
                binding.rv.setAdapter(adapter);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void dialogInternet_error() {
        new AlertDialog.Builder(OtherProfileActivity.this)
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
    }

    private String hour(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

}