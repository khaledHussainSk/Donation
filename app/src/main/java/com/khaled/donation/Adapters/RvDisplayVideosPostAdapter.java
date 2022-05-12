package com.khaled.donation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.Listeners.OnClickItemImagePostListener;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomDisplayImagesRvBinding;
import com.khaled.donation.databinding.CustomDisplayVideoRvPostBinding;
import com.khaled.donation.databinding.CustomJustDisplayImagesPostRvBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.ArrayList;

public class RvDisplayVideosPostAdapter
        extends RecyclerView.Adapter<RvDisplayVideosPostAdapter.RvDisplayPhotosPostAdapterHolder>{

    ArrayList<String> videos;
    OnClickItemImagePostListener listener;
    View v;
    CustomDisplayVideoRvPostBinding binding;
    Context context;


    public RvDisplayVideosPostAdapter(ArrayList<String> videos) {
        this.videos = videos;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<String> videos) {
        notifyDataSetChanged();
        this.videos = videos;
    }

    @NonNull
    @Override
    public RvDisplayPhotosPostAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new RvDisplayPhotosPostAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_display_video_rv_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDisplayPhotosPostAdapterHolder holder, int position) {

        String image = videos.get(position);
        holder.bind(image);

    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class RvDisplayPhotosPostAdapterHolder extends RecyclerView.ViewHolder{
        String video;
        public RvDisplayPhotosPostAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomDisplayVideoRvPostBinding.bind(itemView);
            v = itemView;

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    listener.OnClickListener(videos);
//                }
//            });

        }

        private void bind(String video){
            this.video = video;
            ImageView iv_no = binding.ivNo;
            TextView tv_sum = binding.tvSum;

            tv_sum.setText(String.valueOf(videos.size()));

            iv_no.setVisibility(View.GONE);
//            Glide.with(v).load(image).placeholder(R.drawable.ic_loading).into(binding.iv);
//            binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
//                @Override
//                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
//                    youTubePlayer.loadVideo(video,0);
//                }
//            });
            MediaController mediaController = new MediaController(context);
            mediaController.setAnchorView(binding.videoView);
            binding.videoView.setMediaController(mediaController);
            binding.videoView.setVideoPath(video);
            binding.videoView.requestFocus();
            binding.videoView.start();
        }

    }
}
