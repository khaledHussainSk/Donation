package com.khaled.donation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityChangePasswordBinding;

import es.dmoral.toasty.Toasty;

public class ChangePasswordActivity extends AppCompatActivity {
    ActivityChangePasswordBinding binding;
    SharedPreferences sp;
    User currentUser;
    String currentUserId;
    String currentPass;
    String oldPass;
    String newPass;
    String confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        getUser();
        save();
    }

    private void fixed() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void save(){
        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                oldPass = binding.etOldPassword.getText().toString();
                newPass = binding.etPassword.getText().toString();
                confirmPass = binding.etConfirmPassword.getText().toString();

                if (TextUtils.isEmpty(oldPass)){
                    Toasty.error(getBaseContext(), R.string.toast_oldPassword, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(newPass)){
                    Toasty.error(getBaseContext(), R.string.toast_newPassword, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!oldPass.equals(currentPass)){
                    Toasty.error(getBaseContext(), R.string.toast_incorrect, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmPass)){
                    Toasty.error(getBaseContext(), R.string.toast_nullConfirmPassword, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPass.equals(confirmPass)){
                    Toasty.error(getBaseContext(), R.string.toast_notIdentical, Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseFirestore.getInstance().collection("Users")
                        .document(currentUserId)
                        .update("password",newPass);
                Toasty.success(ChangePasswordActivity.this,
                        R.string.toast_passwordChangedSuccessfully,Toast.LENGTH_LONG).show();
                finish();

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
                            if (getBaseContext() != null){
                                currentPass = currentUser.getPassword();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), ""+e, Toast.LENGTH_SHORT).show();
            }
        });
    }

}