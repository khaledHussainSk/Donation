package com.khaled.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.khaled.donation.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    public static final String ISCHECKED_KEY = "ISCHECKED_KEY";
    ActivityLoginBinding binding;
    FirebaseAuth firebaseAuth;
    String email;
    String password;
    public static final String EMAIL = "EMAIL";
    SharedPreferences sp;
    SharedPreferences.Editor editt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();


        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = binding.etEmail.getText().toString();
                password = binding.etPassword.getText().toString();

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getBaseContext()
                            , R.string.toast_nullEmail_login, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(getBaseContext()
                            , R.string.toast_nullPassword_login, Toast.LENGTH_SHORT).show();
                    return;
                }

                binding.spinKit.setVisibility(View.VISIBLE);
                disableFeild();

                getInfo();

            }
        });

    }

    private void fixed(){
        firebaseAuth = FirebaseAuth.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editt = sp.edit();
        binding.singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrarActivity.class);
                startActivity(intent);
            }
        });
        binding.rememberMeCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    editt.putString(ISCHECKED_KEY,"YES");
                    editt.apply();
                }else if (!compoundButton.isChecked()){
                    editt.putString(ISCHECKED_KEY,null);
                    editt.apply();
                }
            }
        });
    }

    private void getInfo(){
        firebaseAuth.signInWithEmailAndPassword(binding.etEmail.getText().toString()
                ,binding.etPassword.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            binding.spinKit.setVisibility(View.GONE);
                            Intent intent = new Intent(getBaseContext(),MainActivity.class);
                            intent.putExtra("email",email);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.spinKit.setVisibility(View.GONE);
                Toast.makeText(getBaseContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                enalbeFeild();
            }
        });
    }

    void disableFeild(){

        binding.etEmail.setEnabled(false);
        binding.etPassword.setEnabled(false);
        binding.btnSignIn.setEnabled(false);

    }
    void enalbeFeild(){

        binding.etEmail.setEnabled(true);
        binding.etPassword.setEnabled(true);
        binding.btnSignIn.setEnabled(true);

    }

}