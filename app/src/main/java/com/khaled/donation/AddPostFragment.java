package com.khaled.donation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.FragmentAddPostBinding;

import es.dmoral.toasty.Toasty;

public class AddPostFragment extends Fragment {

    public static final String IMAGE_STRING_KEY = "IMAGE_STRING_KEY";
    String imageString;
    SharedPreferences sp;
    FragmentAddPostBinding binding;
    User currentUser;
    String currentUserID;
    int validity;
    NetworkInfo netInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddPostBinding.inflate(getLayoutInflater(),container,false);
        fixed();
        move();
        getUser();

        return binding.getRoot();
    }

    private void dialogInternet_error() {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
    }

    private void fixed() {
        ConnectivityManager conMgr =  (ConnectivityManager)getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
    }

    private void move(){
        ActivityResultLauncher<String> arlPhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null){
                            imageString = String.valueOf(result);
                            Intent intent = new Intent(getActivity(),AddPhotoActivity.class);
                            intent.putExtra(IMAGE_STRING_KEY,imageString);
                            startActivity(intent);
                        }
                    }
                });
        binding.btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else{
                    if (validity == 1 || validity == 3 /* demo */ || validity == 2){
                        if (HomeFragment.isUploaded == false){
                            arlPhoto.launch("image/*");
                        }else {
                            Toasty.info(getActivity(),R.string.toast_waitUploaded
                                    ,Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toasty.info(getActivity(),R.string.toast_notAllwoed
                                ,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.btnAddVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        binding.btnAddCharityCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else{
                    if (validity == 3 || /* demo */ validity == 2){
                        Intent intent = new Intent(getActivity(),AddCharityCampaignActivity.class);
                        startActivity(intent);
                    }else {
                        Toasty.info(getActivity(),R.string.toast_notAllwoed
                                ,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void getUser(){
        disableField();
        FirebaseFirestore.getInstance().collection("Users").document(currentUserID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            currentUser = documentSnapshot.toObject(User.class);
                            if (getActivity() != null){
                                validity = currentUser.getValidity();
                                if (validity == 2 || validity == 3){
                                    binding.btnAddCharityCampaign.setVisibility(View.VISIBLE);
                                    binding.tvOr.setVisibility(View.VISIBLE);
                                }
                                enableField();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void disableField(){
        binding.btnAddPhoto.setEnabled(false);
        binding.btnAddVideo.setEnabled(false);
        binding.btnAddCharityCampaign.setEnabled(false);
    }
    private void enableField(){
        binding.btnAddCharityCampaign.setEnabled(true);
        binding.btnAddPhoto.setEnabled(true);
        binding.btnAddVideo.setEnabled(true);
    }

}