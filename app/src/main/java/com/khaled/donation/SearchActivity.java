package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.RecyclerSearchAdapter;
import com.khaled.donation.Listeners.OnClickItemSearchListener;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivitySearchBinding;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    ActivitySearchBinding binding;
    RecyclerSearchAdapter adapter;
    ArrayList<User> userArrayList;
    String search;
    public final static String USER_KEY = "USER_KEY";
    String currentUserId;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fixed();
        search();

    }


    private void fixed(){
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
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
                    FirebaseFirestore.getInstance()
                            .collection("Users")
                            .orderBy("fullName")
                            .startAt(charSequence.toString().trim())
                            .endAt(charSequence.toString().trim()+"\uf8ff").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    userArrayList = new ArrayList<>();

                                    for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                        User user = queryDocumentSnapshot.toObject(User.class);
                                        if (!user.getIdUser().equals(currentUserId)){
                                            userArrayList.add(user);
                                        }
                                    }

                                    adapter = new RecyclerSearchAdapter(userArrayList
                                            , new OnClickItemSearchListener() {
                                        @Override
                                        public void OnClickListener(User user) {
                                            Intent intent = new Intent(SearchActivity.this,
                                                    OtherProfileActivity.class);
                                            intent.putExtra(USER_KEY,user);
                                            startActivity(intent);
                                        }
                                    });
                                    binding.rvSearch.setAdapter(adapter);
                                    binding.rvSearch.setHasFixedSize(true);
                                    binding.rvSearch
                                            .setLayoutManager
                                                    (new LinearLayoutManager
                                                            (getApplicationContext()));
                                }
                            });

                }else{
                    userArrayList.clear();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}