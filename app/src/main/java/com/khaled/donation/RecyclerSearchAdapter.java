package com.khaled.donation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.CustomLayoutSearchBinding;

import java.util.ArrayList;

public class RecyclerSearchAdapter extends RecyclerView.Adapter<RecyclerSearchAdapter.SearchViewHolder> {

    ArrayList<User> userArrayList;
    Context context;

    public RecyclerSearchAdapter(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_layout_search,parent,false);
        SearchViewHolder searchViewHolder = new SearchViewHolder(v);
        context = parent.getContext();
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        User u = userArrayList.get(position);

        holder.binding.tvNameUser.setText(u.getFullName());
        Glide.with(context).load(u.getImageProfile()).into(holder.binding.profileImage);
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    class SearchViewHolder extends RecyclerView.ViewHolder {
        CustomLayoutSearchBinding binding;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomLayoutSearchBinding.bind(itemView);
        }
    }
}
