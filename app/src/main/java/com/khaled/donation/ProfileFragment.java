package com.khaled.donation;

import android.content.Context;
import android.content.DialogInterface;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;
import com.khaled.donation.Adapters.RvPostsProfileAdapter;
import com.khaled.donation.Models.CertificateVerification;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.FragmentProfileBinding;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;

public class ProfileFragment extends Fragment {
    public static final String IMAGE_KEY = "IMAGE_KEY";
    FragmentProfileBinding binding;
    SharedPreferences sp;
    String currentUserId;
    User currentUser;
    ActivityResultLauncher<Intent> arl;
    NetworkInfo netInfo;
    ArrayList<String> images;
    RvPostsProfileAdapter adapter;
    ActivityResultLauncher<String> arlPhoto;
    FirebaseStorage storage;
    Uri imageUri;
    String image;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(getLayoutInflater(),container,false);
        fixed();
        getUser();
        certificate_verification();
        onARL();

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
        storage = FirebaseStorage.getInstance();
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
                                getPosts();
                                Glide.with(getActivity()).load(currentUser.getImageProfile())
                                        .placeholder(R.drawable.ic_user4).into(binding.ivProfile);
                                binding.tvUsername.setText(currentUser.getFullName());
                                binding.tvFollowers.setText(String.valueOf(currentUser.getFollowers()));
                                binding.tvFollowing.setText(String.valueOf(currentUser.getFollowing()));
                                binding.tvPosts.setText(String.valueOf(currentUser.getPosts()));
                                binding.shortBio.setText(currentUser.getShortBio());
                                enableField();
                                binding.ivProfile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(getActivity(),DisplayImageActivity.class);
                                        intent.putExtra(IMAGE_KEY,currentUser.getImageProfile());
                                        startActivity(intent);
                                    }
                                });
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
        binding.ivProfile.setEnabled(false);
        binding.followingLayout.setEnabled(false);
        binding.follwersLayout.setEnabled(false);
        binding.postsLayout.setEnabled(false);
    }
    private void enableField(){
        binding.btnProfileDetails.setEnabled(true);
        binding.shortBio.setEnabled(true);
        binding.ivProfile.setEnabled(true);
        binding.followingLayout.setEnabled(true);
        binding.follwersLayout.setEnabled(true);
        binding.postsLayout.setEnabled(true);
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
                        if (post.getCategory().equals("حملات")){
                            images.add(currentUser.getImageProfile());
                        }else {
                            images.add(post.getImages().get(0));
                        }
                    }
                }
                showRecycler();
            }
        });
    }

    private void showRecycler(){
        adapter = new RvPostsProfileAdapter(getActivity(),images,currentUserId);
        binding.rv.setHasFixedSize(true);
        binding.rv.setLayoutManager(new GridLayoutManager(getActivity(),3));
        binding.rv.setAdapter(adapter);
        if (images.size() == 0){
            if (currentUser.getValidity() == 2){
                //لم يتم التحقق بعد
                binding.btnCheck.setVisibility(View.VISIBLE);
                binding.ivVerified.setVisibility(View.VISIBLE);
            }else if (currentUser.getValidity() == 4){
                //قيد التحقق
                binding.btnCheck.setVisibility(View.GONE);
                binding.tvNoPosts.setVisibility(View.GONE);
                binding.tvCertificateProgress.setVisibility(View.VISIBLE);
            }
            else{
                binding.btnCheck.setVisibility(View.GONE);
                binding.tvNoPosts.setVisibility(View.VISIBLE);
            }
        }else {
            binding.btnCheck.setVisibility(View.GONE);
            binding.tvNoPosts.setVisibility(View.GONE);
        }
        if (currentUser.getValidity() == 3){
            binding.ivVerified.setVisibility(View.VISIBLE);
        }else {
            binding.ivVerified.setVisibility(View.GONE);
        }
        binding.progressBar.setVisibility(View.GONE);
    }

    private void certificate_verification(){
        binding.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arlPhoto.launch("image/*");
            }
        });
    }

    private void onARL(){
        arlPhoto = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null){
                            CropImage.activity(result)
                                    .setGuidelines(CropImageView.Guidelines.OFF)
                                    .setAutoZoomEnabled(false)
                                    .start(getContext(), ProfileFragment.this);
                            imageUri = result;
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.confirmSend);
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
                    uploadImage();
                }
            });
            builder.show();

        }

    }

    private void uploadImage(){
        storage.getReference().child("CertificateVerificationImages/"+currentUser.getJoined())
                .putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(@NonNull Uri uri) {
                                        image = String.valueOf(uri);
                                        create_certificate_verification();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(getActivity(), R.string.somethingWrong
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void create_certificate_verification(){
        CertificateVerification certificateVerification = new CertificateVerification(
                currentUserId,image,Calendar.getInstance().getTime());
        DocumentReference documentReference = FirebaseFirestore
                .getInstance().collection("Certificate_verification")
                .document();
        certificateVerification.setId(documentReference.getId());
        documentReference.set(certificateVerification)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseFirestore
                                .getInstance()
                                .collection("Users")
                                .document(currentUser.getIdUser())
                                .update("validity",4);
                        binding.btnCheck.setVisibility(View.GONE);
                        binding.tvCertificateProgress.setVisibility(View.VISIBLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setCancelable(false);
                        builder.setMessage(R.string.certificate_verification_message);
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builder.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toasty.error(getActivity(), R.string.somethingWrong
                        , Toast.LENGTH_SHORT).show();
            }
        });
    }

}