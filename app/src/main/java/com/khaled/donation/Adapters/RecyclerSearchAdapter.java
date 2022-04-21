package com.khaled.donation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.Listeners.OnClickItemSearchListener;
import com.khaled.donation.Models.User;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomLayoutSearchBinding;

import java.util.ArrayList;

public class RecyclerSearchAdapter extends RecyclerView.Adapter<RecyclerSearchAdapter.SearchViewHolder> {

    ArrayList<User> userArrayList;
    OnClickItemSearchListener onClickItemSearchListener;
    CustomLayoutSearchBinding binding;
    View v;

    public RecyclerSearchAdapter(ArrayList<User> userArrayList,OnClickItemSearchListener onClickItemSearchListener) {
        this.userArrayList = userArrayList;
        this.onClickItemSearchListener = onClickItemSearchListener;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_search,parent,false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(v);
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        User user = userArrayList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        User user;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomLayoutSearchBinding.bind(itemView);
            v = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItemSearchListener.OnClickListener(user);
                }
            });

        }
        void bind(User user){
            this.user = user;
            binding.tvNameUser.setText(user.getFullName());
            Glide.with(v).load(user.getImageProfile())
                    .placeholder(R.drawable.ic_user4)
                    .into(binding.profileImage);
        }
    }

}
