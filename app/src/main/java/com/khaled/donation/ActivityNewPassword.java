package com.khaled.donation;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityNewPasswordBinding;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class ActivityNewPassword extends AppCompatActivity {

    ActivityNewPasswordBinding binding;
    ArrayList<User> arrayList;
    String email;
    public static String USEREMAIL = "USEREMAIL";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        arrayList = new ArrayList<>();
        email = binding.etEmailForget.getText().toString();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.search_email);
        progressDialog.setCancelable(false);

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                FirebaseFirestore.getInstance().collection("Users").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                    User user = queryDocumentSnapshot.toObject(User.class);
                                    if (user.getEmail().equals(binding.etEmailForget.getText().toString())) {
                                        arrayList.add(user);
                                        if (arrayList.size()!=0){

                                            FirebaseAuth auth = FirebaseAuth.getInstance();

                                            auth.sendPasswordResetEmail(arrayList.get(0).getEmail())
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                progressDialog.dismiss();
                                                                AlertDialog.Builder builder
                                                                        = new AlertDialog.Builder(ActivityNewPassword.this);
                                                                builder.setMessage(R.string.forget_password_message);
                                                                builder.setPositiveButton(R.string.ok
                                                                        , new DialogInterface.OnClickListener() {
                                                                            @Override
                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                finish();
                                                                            }
                                                                        });
                                                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                    }
                                                                });
                                                                builder.show();
                                                            }else {
                                                                progressDialog.dismiss();
                                                                Toasty.error(getApplicationContext(),"Error").show();
                                                            }
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(), "Error"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        });

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