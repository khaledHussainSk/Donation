package com.khaled.donation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.khaled.donation.databinding.FragmentMenuBinding;

public class MenuFragment extends Fragment {
    SharedPreferences sp;
    SharedPreferences.Editor editt;
    FragmentMenuBinding binding;
    String currentUserId;
    User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(getLayoutInflater(), container, false);
        fixed();
        getUser();
        return binding.getRoot();

    }

    private void fixed(){
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editt = sp.edit();
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
        binding.constraintLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.bottomNavigation.show(4,true);
            }
        });
        binding.constraintLayoutContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ContentUsActivity.class);
                startActivity(intent);
            }
        });
        binding.constraintLayoutAppReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),AppReviewActivity.class);
                startActivity(intent);
            }
        });
        binding.constraintLayoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
        binding.constraintLayoutSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.confirmSignOut);
                builder.setCancelable(false);
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getActivity(),LoginActivity.class);
                        editt.putString(LoginActivity.ISCHECKED_KEY,null);
                        editt.apply();
                        startActivity(intent);
                        MainActivity.context.finishAffinity();
                    }
                });
                builder.show();
            }
        });
        binding.constraintLayoutAllFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),FavoriteActivity.class);
                startActivity(intent);
            }
        });
        binding.icSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),SearchActivity.class);
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