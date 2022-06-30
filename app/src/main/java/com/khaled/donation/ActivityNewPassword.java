package com.khaled.donation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
//    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        arrayList = new ArrayList<>();
        email = binding.etEmailForget.getText().toString();

//        progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Search Email");

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressDialog.show();
//                Toast.makeText(getApplicationContext(), "email"+binding.etEmailForget.getText().toString(), Toast.LENGTH_SHORT).show();
                FirebaseFirestore.getInstance().collection("Users").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                    User user = queryDocumentSnapshot.toObject(User.class);
                                    if (user.getEmail().equals(binding.etEmailForget.getText().toString())) {
                                        arrayList.add(user);
                                    }
//                                    Toast.makeText(getApplicationContext(), ""+user.getFullName(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                if (arrayList.size()!=0){
//                    for (int i = 0 ; i < arrayList.size();i++){
//                        if (arrayList.get(i).getEmail().equals(binding.etEmailForget.getText().toString())){

                            Toasty.success(getApplicationContext(),"Yes").show();
//                            Toast.makeText(getApplicationContext(), "Yse", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),ActivityForgetPassword.class);
                            intent.putExtra(USEREMAIL,arrayList.get(0));
                            startActivity(intent);
//                        }else if (arrayList.size() == i){
//                            progressDialog.dismiss();
//
//                        }
//                    }
                }
//                else {
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "uyyy", Toast.LENGTH_SHORT).show();
//                }

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