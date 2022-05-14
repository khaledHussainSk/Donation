package com.khaled.donation.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.Listeners.OnClickItemListener;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomJustDisplayImagesPostRvBinding;

import java.util.ArrayList;

public class RvJustDisplayPhotosPostAdapter
        extends RecyclerView.Adapter<RvJustDisplayPhotosPostAdapter.RvJustDisplayPhotosPostAdapterHolder> {

    ArrayList<String> images;
    OnClickItemListener listener;
    CustomJustDisplayImagesPostRvBinding binding;
    View v;

    public RvJustDisplayPhotosPostAdapter(ArrayList<String> images, OnClickItemListener listener) {
        this.images = images;
        this.listener = listener;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RvJustDisplayPhotosPostAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvJustDisplayPhotosPostAdapterHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_just_display_images_post_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvJustDisplayPhotosPostAdapterHolder holder, int position) {

        String image = images.get(position);
        holder.bind(image);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class RvJustDisplayPhotosPostAdapterHolder extends RecyclerView.ViewHolder{
        String image;
        public RvJustDisplayPhotosPostAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomJustDisplayImagesPostRvBinding.bind(itemView);
            v = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnCLickListener();
                }
            });

        }

        private void bind(String image){
            this.image = image;
            Glide.with(v).load(image).placeholder(R.drawable.ic_loading).into(binding.iv);
        }

    }
}

