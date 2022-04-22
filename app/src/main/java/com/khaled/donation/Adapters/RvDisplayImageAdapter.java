package com.khaled.donation.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.Listeners.OnClickItemImageListener;
import com.khaled.donation.Listeners.OnClickNoListener;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomDisplayImagesRvBinding;

import java.util.ArrayList;

public class RvDisplayImageAdapter extends
        RecyclerView.Adapter<RvDisplayImageAdapter.RvDisplayImageAdapterHolder>{

    ArrayList<Uri> images;
    OnClickItemImageListener listener;
    OnClickNoListener listenerNo;

    public RvDisplayImageAdapter(ArrayList<Uri> images, OnClickItemImageListener listener, OnClickNoListener listenerNo) {
        this.images = images;
        this.listener = listener;
        this.listenerNo = listenerNo;
    }

    public ArrayList<Uri> getImages() {
        return images;
    }

    public void setImages(ArrayList<Uri> images) {
        notifyDataSetChanged();
        this.images = images;
    }

    @NonNull
    @Override
    public RvDisplayImageAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvDisplayImageAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_display_images_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDisplayImageAdapterHolder holder, int position) {
        Uri image = images.get(position);
        holder.bind(image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class RvDisplayImageAdapterHolder extends RecyclerView.ViewHolder{
        CustomDisplayImagesRvBinding binding;
        Uri image;
        View v;
        public RvDisplayImageAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomDisplayImagesRvBinding.bind(itemView);
            v = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickListener(image);
                }
            });

            binding.ivNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerNo.OnClickListener(image);
                }
            });

        }

        void bind(Uri image){
            this.image = image;

            CardView card = binding.card;
            card.setVisibility(View.GONE);
            Glide.with(v).load(image).placeholder(R.drawable.ic_loading).into(binding.iv);
        }

    }

}
