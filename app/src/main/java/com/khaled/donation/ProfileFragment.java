package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    SharedPreferences sp;
    String currentUserId;
    User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater(),container,false);
        fixed();
        getUser();
        binding.btnProfileDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ProfileDetailsActivity.class);
                startActivity(intent);
            }
        });
        return binding.getRoot();
    }

    private void fixed(){
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
        binding.shortBio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ShortBioActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getUser(){
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

}