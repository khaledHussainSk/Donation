package com.khaled.donation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.RvPostsProfileAdapter;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.FragmentProfileBinding;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    SharedPreferences sp;
    String currentUserId;
    User currentUser;
    ActivityResultLauncher<Intent> arl;
    NetworkInfo netInfo;
    ArrayList<String> images;
    RvPostsProfileAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater(),container,false);
        fixed();
        getUser();
        getPosts();
        
        arl = registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        getUser();
                    }
                });
        
        binding.btnProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else{
                    Intent intent = new Intent(getActivity(),ProfileDetailsActivity.class);
                    arl.launch(intent);
                }
            }
        });

        binding.postsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostsActivity.class);
                intent.putExtra(RvPostsProfileAdapter.ID_USER_KEY,RvPostsProfileAdapter.id_user);
                startActivity(intent);
            }
        });

        binding.follwersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowersActivity.class);
                startActivity(intent);
            }
        });

        binding.followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowingActivity.class);
                startActivity(intent);
            }
        });

        return binding.getRoot();
    }

    private void fixed(){
        images = new ArrayList<>();
        ConnectivityManager conMgr =  (ConnectivityManager)getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
        binding.shortBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else{
                    Intent intent = new Intent(getActivity(),ShortBioActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void getUser(){
        disableField();
        FirebaseFirestore.getInstance().collection("Users").document(currentUserId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            currentUser = documentSnapshot.toObject(User.class);
                            if (getActivity() != null){
                                Glide.with(getActivity()).load(currentUser.getImageProfile())
                                        .placeholder(R.drawable.ic_user4).into(binding.ivProfile);
                                binding.tvUsername.setText(currentUser.getFullName());
                                binding.tvFollowers.setText(String.valueOf(currentUser.getFollowers()));
                                binding.tvFollowing.setText(String.valueOf(currentUser.getFollowing()));
                                binding.tvPosts.setText(String.valueOf(currentUser.getPosts()));
                                binding.shortBio.setText(currentUser.getShortBio());
                                enableField();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableField(){
        binding.btnProfileDetails.setEnabled(false);
        binding.shortBio.setEnabled(false);
    }
    private void enableField(){
        binding.btnProfileDetails.setEnabled(true);
        binding.shortBio.setEnabled(true);
    }

    private void dialogInternet_error() {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
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
                    if (post.getPublisher().equals(currentUserId)){
                        images.add(post.getImages().get(0));
                    }
                }
                adapter = new RvPostsProfileAdapter(getActivity(),images);
                binding.rv.setHasFixedSize(true);
                binding.rv.setLayoutManager(new GridLayoutManager(getActivity(),3));
                binding.rv.setAdapter(adapter);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

}