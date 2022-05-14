package com.khaled.donation.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.Listeners.OnClickItemSearchListener;
import com.khaled.donation.MainActivity;
import com.khaled.donation.Models.User;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomFollwersFollowingRvBinding;

import java.util.ArrayList;

public class RvFollowersFollowingAdapter extends
        RecyclerView.Adapter<RvFollowersFollowingAdapter.RvFollowersFollowingAdapterHolder> {

    Context context;
    ArrayList<User> users;
    CustomFollwersFollowingRvBinding binding;
    SharedPreferences sp;
    String currentUserID;
    OnClickItemSearchListener listener;

    public RvFollowersFollowingAdapter(Context context, ArrayList<User> users
    ,OnClickItemSearchListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RvFollowersFollowingAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvFollowersFollowingAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_follwers_following_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvFollowersFollowingAdapterHolder holder, int position) {

        User user = users.get(position);
        holder.bind(user);

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class RvFollowersFollowingAdapterHolder extends RecyclerView.ViewHolder{
        User user;
        public RvFollowersFollowingAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomFollwersFollowingRvBinding.bind(itemView);
            sp = PreferenceManager.getDefaultSharedPreferences(context);
            currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickListener(user);
                }
            });

        }

        private void bind(User user){
            this.user = user;

            binding.tvNameUser.setText(user.getFullName());
            Glide.with(context)
                    .load(user.getImageProfile())
                    .placeholder(R.drawable.ic_user4)
                    .into(binding.profileImage);

        }

    }

}
