package com.khaled.donation.Adapters;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.Favorite;
import com.khaled.donation.Models.Post;
import com.khaled.donation.Models.User;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomFavoriteBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RvFavoriteAdapter extends RecyclerView.Adapter<RvFavoriteAdapter.RvFavoriteAdapterHolder> {

    Context context;
    ArrayList<Favorite> favorites;
    CustomFavoriteBinding binding;
    NetworkInfo netInfo;

    public RvFavoriteAdapter(Context context, ArrayList<Favorite> favorites) {
        this.context = context;
        this.favorites = favorites;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public ArrayList<Favorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<Favorite> favorites) {
        this.favorites = favorites;
    }

    public CustomFavoriteBinding getBinding() {
        return binding;
    }

    public void setBinding(CustomFavoriteBinding binding) {
        this.binding = binding;
    }

    @NonNull
    @Override
    public RvFavoriteAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvFavoriteAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_favorite,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvFavoriteAdapterHolder holder, int position) {

        Favorite favorite = favorites.get(position);
        holder.bine(favorite);

    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    class RvFavoriteAdapterHolder extends RecyclerView.ViewHolder{
        Favorite favorite;
        public RvFavoriteAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomFavoriteBinding.bind(itemView);
            ConnectivityManager conMgr =  (ConnectivityManager)context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = conMgr.getActiveNetworkInfo();

        }

        private void bine(Favorite favorite){
            this.favorite = favorite;

            ImageView iv_post = binding.ivPost;
            ImageView unsave = binding.ivUnsave;
            TextView tv_date = binding.tvDate;
            TextView tv_username = binding.username;
            TextView tv_description = binding.description;

            getInfo(favorite,tv_date,iv_post,tv_description,tv_username);

            unsave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (netInfo == null){
                        dialogInternet_error();
                    }else{
                        unsave(favorite);
                    }
                }
            });
        }

    }

    private void unsave(Favorite favorite){
        FirebaseFirestore.getInstance().collection("Favorite")
                .document(favorite.getId()).delete();
        Toast.makeText(context, R.string.unsave, Toast.LENGTH_SHORT).show();
    }

    private void getInfo(Favorite favorite
            ,TextView tv_date,ImageView iv_post,TextView tv_description,TextView tv_username){
        tv_date.setText(formatDate(favorite.getDate()));
        getPost(favorite,iv_post,tv_description,tv_username);
    }

    private void getPost(Favorite favorite
            ,ImageView iv_post,TextView tv_description,TextView tv_username){
        FirebaseFirestore.getInstance().collection("Posts")
                .document(favorite.getId_post()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Post post = documentSnapshot.toObject(Post.class);
                        Glide.with(context)
                                .load(post.getImages().get(0))
                                .placeholder(R.drawable.ic_loading)
                                .into(iv_post);
                        if (post.getDescription().equals("")){
                            tv_description.setText(R.string.noDescription);
                        }else {
                            tv_description.setText(post.getDescription());
                        }
                        getPublisher(post,tv_username);
                    }
                });
    }

    private void getPublisher(Post post,TextView tv_username){
        FirebaseFirestore.getInstance().collection("Users")
                .document(post.getPublisher()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = documentSnapshot.toObject(User.class);
                        tv_username.setText(user.getFullName());
                    }
                });
    }

    private String formatDate(Date date){
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MMM dd / hh:mm aa", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private void dialogInternet_error() {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(context.getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
    }

}
