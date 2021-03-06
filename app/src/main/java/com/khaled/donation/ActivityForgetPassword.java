package com.khaled.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityForgetPasswordBinding;

import es.dmoral.toasty.Toasty;

public class ActivityForgetPassword extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    User user;
    String password;
    String confirm_password;
    FirebaseUser firebaseUser;
    AuthCredential credential;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        password = binding.etPassword.getText().toString();
        confirm_password = binding.etConfirmPassword.getText().toString();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra(ActivityNewPassword.USEREMAIL);

        Toast.makeText(getApplicationContext(), "email"+user.getEmail(), Toast.LENGTH_SHORT).show();

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "ffff"+password, Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(binding.etPassword.getText().toString())){
//                    Toast.makeText(getApplicationContext(), R.string.toast_newPassword, Toast.LENGTH_SHORT).show();
                    binding.etPassword.setError("Please enter a new password");
                    return;
                }
                if (TextUtils.isEmpty(binding.etConfirmPassword.getText().toString())){
//                    Toast.makeText(getApplicationContext(), R.string.Please_Enter_Your_Confirm_Password, Toast.LENGTH_SHORT).show();
                    binding.etConfirmPassword.setError("Please Enter Your Confirm Password");
                    return;
                }
                if (!binding.etConfirmPassword.getText().toString().equals(binding.etPassword.getText().toString())){
//                    Toast.makeText(getApplicationContext(), R.string.toast_notIdentical, Toast.LENGTH_SHORT).show();
                    binding.etConfirmPassword.setError("The password is not identical");
                    return;
                }



//                FirebaseAuth auth = FirebaseAuth.getInstance();
//
//                auth.sendPasswordResetEmail(user.getEmail())
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()){
//                                    Toasty.success(getApplicationContext(),"???? ?????????? ???????? ????????????").show();
//                                }else {
//                                    Toasty.error(getApplicationContext(),"Error").show();
//                                }
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "rr"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//
//                FirebaseFirestore.getInstance().collection("Users")
//                        .document(user.getIdUser()).update("password",binding.etConfirmPassword.getText().toString());
//                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
//                startActivity(i);
//                finish();
            }
        });

        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}