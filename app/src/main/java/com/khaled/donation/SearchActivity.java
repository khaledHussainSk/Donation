package com.khaled.donation;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivitySearchBinding;

import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    RecyclerSearchAdapter adapter;
    ArrayList<User> userArrayList;
    String search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        search();

    }


    private void fixed(){
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void search(){
        search = binding.etSearch.getText().toString();


        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    FirebaseFirestore.getInstance().collection("Users").orderBy("fullName").startAt(charSequence.toString().trim()).endAt(charSequence.toString().trim()+"\uf8ff").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    userArrayList = new ArrayList<>();

                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        User user = queryDocumentSnapshot.toObject(User.class);
                                        userArrayList.add(user);
                                        Toast.makeText(getApplicationContext(), "gg" + user.getFullName(), Toast.LENGTH_SHORT).show();
                                    }

                                    adapter = new RecyclerSearchAdapter(userArrayList);
                                    binding.rvSearch.setAdapter(adapter);
                                    binding.rvSearch.setHasFixedSize(true);
                                    binding.rvSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                }
                            });

                }else if (charSequence.toString().trim().length() == 0){
                    userArrayList.clear();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}