package com.khaled.donation.Adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.khaled.donation.Listeners.GetFavorite;
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
    GetFavorite getFavorite;

    public RvFavoriteAdapter(Context context, ArrayList<Favorite> favorites,
    GetFavorite getFavorite) {
        this.context = context;
        this.favorites = favorites;
        this.getFavorite = getFavorite;
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
    public int getItemViewType(int position) {
        return position;
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFavorite.OnClickListener(favorite);
                }
            });
        }

        private void bine(Favorite favorite){
            this.favorite = favorite;

            ImageView iv_post = binding.ivPost;
            ImageView unsave = binding.ivUnsave;
            TextView tv_date = binding.tvDate;
            TextView tv_category = binding.tvCategory;
            TextView tv_title = binding.tvTitle;

            getInfo(favorite,tv_date,iv_post,tv_title,tv_category);

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
            ,TextView tv_date,ImageView iv_post,TextView tv_title,TextView tv_category){
        tv_date.setText(formatDate(favorite.getDate()));
        getPost(favorite,iv_post,tv_title,tv_category);
    }

    private void getPost(Favorite favorite
            ,ImageView iv_post,TextView tv_title,TextView tv_gategory){
        FirebaseFirestore.getInstance().collection("Posts")
                .document(favorite.getId_post()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Post post = documentSnapshot.toObject(Post.class);
                        if (post.getCategory().equals("حملات")){
                            FirebaseFirestore.getInstance().collection("Users")
                                    .document(post.getPublisher()).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            User user = documentSnapshot.toObject(User.class);
                                            Glide.with(context)
                                                    .load(user.getImageProfile())
                                                    .placeholder(R.drawable.ic_loading)
                                                    .into(iv_post);
                                        }
                                    });
                        }else {
                            Glide.with(context)
                                    .load(post.getImages().get(0))
                                    .placeholder(R.drawable.ic_loading)
                                    .into(iv_post);
                        }
                        tv_title.setText(post.getTitle());
                        if (post.getCategory().equals("أطعمة ومشروبات")){
                            setCategory(R.drawable.shape_tv_category_foods
                                    ,"#00B6FF",post,tv_gategory);
                            return;
                        }else if (post.getCategory().equals("أجهزة والكترونيات")){
                            setCategory(R.drawable.shape_tv_category_appliances_and_electronics
                                    ,"#FFBC00",post,tv_gategory);
                            return;
                        }else if (post.getCategory().equals("سيارات ومركبات")){
                            setCategory(R.drawable.shape_tv_category_cars_and_vehicles
                                    ,"#009688",post,tv_gategory);
                            return;
                        }else if ((post.getCategory().equals("أثاث وديكور"))){
                            setCategory(R.drawable.shape_tv_category_furniture_and_decoration
                                    ,"#4CAF50",post,tv_gategory);
                            return;
                        }else if (post.getCategory().equals("عقارات وأملاك")){
                            setCategory(R.drawable.shape_tv_category_real_estate_and_property
                                    ,"#673AB7",post,tv_gategory);
                            return;
                        }else if (post.getCategory().equals("حيوانات وطيور")){
                            setCategory(R.drawable.shape_tv_category_animals_and_birds
                                    ,"#9C27B0",post,tv_gategory);
                            return;
                        }else if (post.getCategory().equals("أجهزة والكترونيات")){
                            setCategory(R.drawable.shape_tv_category_computers_and_the_internet
                                    ,"#009688",post,tv_gategory);
                            return;
                        }
                        else {
                            tv_gategory.setText(post.getCategory());
                            return;
                        }
                    }
                });
    }

    private void setCategory(int background,String textColor,Post post,TextView tv_category){
        tv_category.setBackgroundResource(background);
        tv_category.setTextColor(Color.parseColor(textColor));
        tv_category.setText(post.getCategory());
    }

    private void getPublisher(Post post){
        FirebaseFirestore.getInstance().collection("Users")
                .document(post.getPublisher()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = documentSnapshot.toObject(User.class);
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
