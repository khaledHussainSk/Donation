package com.khaled.donation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.RvFavoriteAdapter;
import com.khaled.donation.Models.Favorite;
import com.khaled.donation.databinding.ActivityFavoriteBinding;

import java.util.ArrayList;

public class FavoriteActivity extends AppCompatActivity {
    ActivityFavoriteBinding binding;
    ArrayList<Favorite> favorites;
    public static RvFavoriteAdapter adapter;
    String currentUserID;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        getFavorites();

    }

    private void fixed() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        favorites = new ArrayList<>();
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getFavorites(){
        FirebaseFirestore
                .getInstance()
                .collection("Favorite")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                                Favorite favorite = queryDocumentSnapshot.toObject(Favorite.class);
                                if (favorite.getId_user().equals(currentUserID)){
                                    favorites.add(favorite);
                                }
                            }
                            binding.tvAllFavorite.setText(String.valueOf(favorites.size()));
                            adapter = new RvFavoriteAdapter(FavoriteActivity.this,favorites);
                            binding.rv.setHasFixedSize(true);
                            binding.rv.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));
                            binding.rv.setAdapter(adapter);
                        }
                    }
                });
    }


}