package com.khaled.donation.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.LikesActivity;
import com.khaled.donation.MainActivity;
import com.khaled.donation.Models.Like;
import com.khaled.donation.Models.User;
import com.khaled.donation.OtherProfileActivity;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomLikesRvBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RvLikesAdapter extends RecyclerView.Adapter<RvLikesAdapter.RvLikesAdapterHolder> {

    Context context;
    ArrayList<User> users;
    ArrayList<Like> likes;
    CustomLikesRvBinding binding;
    View v;
    SharedPreferences sp;
    String currentUserID;

    public RvLikesAdapter(Context context,ArrayList<User> users,ArrayList<Like> likes) {
        this.context = context;
        this.users = users;
        this.likes = likes;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setUsers(ArrayList<User> users) {
        notifyDataSetChanged();
        this.users = users;
    }

    @NonNull
    @Override
    public RvLikesAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvLikesAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_likes_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvLikesAdapterHolder holder, int position) {

        User user = users.get(position);
        Like like = likes.get(position);
        holder.bind(user,like);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class RvLikesAdapterHolder extends RecyclerView.ViewHolder{
        User user;
        Like like;
        public RvLikesAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomLikesRvBinding.bind(itemView);
            v = itemView;
            sp = PreferenceManager.getDefaultSharedPreferences(context);
            currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);

        }

        private void bind(User user,Like like){
            this.user = user;
            this.like = like;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!user.getIdUser().equals(currentUserID)){
                        Intent intent = new Intent(context,OtherProfileActivity.class);
                        intent.putExtra(MainActivity.USER_KEY,user);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        LikesActivity.context.finish();
                    }else {
//                        LikesActivity.context.finish();
//                        MainActivity.bottomNavigation.show(5,true);
                    }
                }
            });

            Glide.with(context)
                    .load(user.getImageProfile())
                    .placeholder(R.drawable.ic_user4)
                    .into(binding.profileImage);
            binding.tvNameUser.setText(user.getFullName());
            binding.tvDate.setText(formatDate(like.getDate()));

        }

    }

    private String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd / hh:mm aa", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }
}
