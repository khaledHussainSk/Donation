package com.khaled.donation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.PostsActivity;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomProfileImageBinding;

import java.util.ArrayList;

public class RvPostsProfileAdapter extends RecyclerView.Adapter<RvPostsProfileAdapter.RvPostsProfileAdapterHolder>{

    public static final String ID_USER_KEY = "ID_USER_KEY";
    public static final String POSITION_KEY = "POSITION_KEY";
    Context context;
    ArrayList<String> images;
    CustomProfileImageBinding binding;
    View v;
    public static String id_user;

    public RvPostsProfileAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
    }

    public RvPostsProfileAdapter(Context context, ArrayList<String> images, String id_user) {
        this.context = context;
        this.images = images;
        this.id_user = id_user;
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
        holder.bind(image,position);

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class RvPostsProfileAdapterHolder extends RecyclerView.ViewHolder{
        String image;
        public RvPostsProfileAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomProfileImageBinding.bind(itemView);
            v = itemView;
        }

        private void bind(String image,int position){
            this.image = image;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostsActivity.class);
                    if (id_user != null){
                        intent.putExtra(ID_USER_KEY,id_user);
                    }
                    intent.putExtra(POSITION_KEY,position);
                    context.startActivity(intent);
                }
            });

            Glide.with(v).load(image).placeholder(R.drawable.ic_loading).into(binding.iv);

        }

    }
}
