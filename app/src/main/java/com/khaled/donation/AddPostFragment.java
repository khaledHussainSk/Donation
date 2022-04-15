package com.khaled.donation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.khaled.donation.databinding.FragmentAddPostBinding;

public class AddPostFragment extends Fragment {

    public static final String IMAGE_STRING_KEY = "IMAGE_STRING_KEY";
    String imageString;

    public AddPostFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddPostBinding binding;
        // Inflate the layout for this fragment
        binding = FragmentAddPostBinding.inflate(getLayoutInflater(),container,false);

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
//                            Glide.with(getActivity()).load(imageUri).into(binding.ivPost);
                        }
                    }
                });

        binding.btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arlPhoto.launch("image/*");
            }
        });

        return binding.getRoot();
    }



}