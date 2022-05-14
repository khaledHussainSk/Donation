package com.khaled.donation.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.Listeners.OnClickItemImagePostListener;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomDisplayImagesRvBinding;

import java.util.ArrayList;

public class RvDisplayPhotosPostAdapter
        extends RecyclerView.Adapter<RvDisplayPhotosPostAdapter.RvDisplayPhotosPostAdapterHolder>{

    ArrayList<String> images;
    OnClickItemImagePostListener listener;
    View v;
    CustomDisplayImagesRvBinding binding;


    public RvDisplayPhotosPostAdapter(ArrayList<String> images, OnClickItemImagePostListener listener) {
        this.images = images;
        this.listener = listener;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        notifyDataSetChanged();
        this.images = images;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public RvDisplayPhotosPostAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvDisplayPhotosPostAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_display_images_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDisplayPhotosPostAdapterHolder holder, int position) {

        String image = images.get(position);
        holder.bind(image);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    class RvDisplayPhotosPostAdapterHolder extends RecyclerView.ViewHolder{
        String image;
        public RvDisplayPhotosPostAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomDisplayImagesRvBinding.bind(itemView);
            v = itemView;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickListener(images);
                }
            });

        }

        private void bind(String image){
            this.image = image;
            ImageView iv_no = binding.ivNo;
            TextView tv_sum = binding.tvSum;

            tv_sum.setText(String.valueOf(images.size()));

            iv_no.setVisibility(View.GONE);
            Glide.with(v).load(image).placeholder(R.drawable.ic_loading).into(binding.iv);

        }

    }
}
