package com.khaled.donation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.MainActivity;
import com.khaled.donation.Models.User;
import com.khaled.donation.OtherProfileActivity;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomLikesRvBinding;

import java.util.ArrayList;

public class RvLikesAdapter extends RecyclerView.Adapter<RvLikesAdapter.RvLikesAdapterHolder> {

    Context context;
    ArrayList<User> users;
    CustomLikesRvBinding binding;
    View v;
    SharedPreferences sp;
    String currentUserID;

    public RvLikesAdapter(Context context,ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

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
        holder.bind(user);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class RvLikesAdapterHolder extends RecyclerView.ViewHolder{
        User user;
        public RvLikesAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomLikesRvBinding.bind(itemView);
            v = itemView;
            sp = PreferenceManager.getDefaultSharedPreferences(context);
            currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);

        }

        private void bind(User user){
            this.user = user;

            Button btn_follow = binding.btnFollow;
            if (!currentUserID.equals(user.getIdUser())){
                btn_follow.setVisibility(View.VISIBLE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!user.getIdUser().equals(currentUserID)){
                        Intent intent = new Intent(context,OtherProfileActivity.class);
                        intent.putExtra(MainActivity.USER_KEY,user);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
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

        }

    }
}
