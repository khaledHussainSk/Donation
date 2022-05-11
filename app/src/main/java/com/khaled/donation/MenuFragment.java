package com.khaled.donation;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.FragmentMenuBinding;

import java.util.Locale;

public class MenuFragment extends Fragment {
    SharedPreferences sp;
    FragmentMenuBinding binding;
    String currentUserId;
    User currentUser;
    NetworkInfo netInfo;
    SharedPreferences.Editor edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMenuBinding.inflate(getLayoutInflater(), container, false);

        fixed();
        getUser();
        language();

        return binding.getRoot();
    }

    private void fixed(){
        ConnectivityManager conMgr =  (ConnectivityManager)getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        edit = sp.edit();
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
        binding.constraintLayoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.bottomNavigation.show(5,true);
            }
        });

        binding.constraintLayoutContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else {
                    Intent intent = new Intent(getActivity(),ContentUsActivity.class);
                    startActivity(intent);
                }

            }
        });
        binding.constraintLayoutAppReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else {
                    Intent intent = new Intent(getActivity(),AppReviewActivity.class);
                    startActivity(intent);
                }
            }
        });
        binding.constraintLayoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netInfo == null){
                    dialogInternet_error();
                }else {
                    Intent intent = new Intent(getActivity(),ChangePasswordActivity.class);
                    startActivity(intent);
                }

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
                        edit.putString(LoginActivity.ISCHECKED_KEY,null);
                        edit.apply();
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

    private void dialogInternet_error() {
        new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setMessage(getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
    }

    private void language(){
        binding.constraintLayoutLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChangeLanguageDialog();
            }
        });
    }

    private void showChangeLanguageDialog(){
        final String[] listItem = {"العربية","English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.chooseLanguage);
        builder.setSingleChoiceItems(listItem, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    setLocal("ar");
                    getActivity().recreate();
                }else if (i == 1){
                    setLocal("en");
                    getActivity().recreate();
                }

                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void setLocal(String lang) {
        Locale locale = new Locale(lang);
        locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getActivity().getResources().updateConfiguration(configuration,getActivity()
                .getResources().getDisplayMetrics());
        if (lang.equals("ar")){
            edit.putString("My_lang","ar");
        }else if (lang.equals("en")){
            edit.putString("My_lang",null);
        }
        edit.apply();
    }

}