package com.khaled.donation.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomProfileImageBinding;

import java.util.ArrayList;

public class RvPostsProfileAdapter extends RecyclerView.Adapter<RvPostsProfileAdapter.RvPostsProfileAdapterHolder>{

    ArrayList<String> images;
    CustomProfileImageBinding binding;
    View v;

    public RvPostsProfileAdapter(ArrayList<String> images) {
        this.images = images;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public RvPostsProfileAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvPostsProfileAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_profile_image,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvPostsProfileAdapterHolder holder, int position) {

        String image = images.get(position);
        holder.bind(image);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class RvPostsProfileAdapterHolder extends RecyclerView.ViewHolder{
        String image;
        public RvPostsProfileAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomProfileImageBinding.bind(itemView);
            v = itemView;
        }

        private void bind(String image){
            this.image = image;

            Glide.with(v).load(image).placeholder(R.drawable.ic_loading).into(binding.iv);

        }

    }
}
