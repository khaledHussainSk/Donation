package com.khaled.donation.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.khaled.donation.Listeners.OnClickItemImageListener;
import com.khaled.donation.Listeners.OnClickNoListener;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomDisplayImagesRvBinding;
import com.khaled.donation.databinding.CustomDisplayVideoRvBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.ArrayList;

public class RvDisplayVideoAdapter extends
        RecyclerView.Adapter<RvDisplayVideoAdapter.RvDisplayImageAdapterHolder> {

    ArrayList<String> videos;
    OnClickItemImageListener listener;
    OnClickNoListener listenerNo;
    Context context;

    public RvDisplayVideoAdapter(ArrayList<String> videos
            , OnClickNoListener listenerNo, Context context) {
        this.videos = videos;
        this.listenerNo = listenerNo;
        this.context = context;
    }

    public ArrayList<String> getVideo() {
        return videos;
    }

    public void setVideo(ArrayList<String> videos) {
        this.videos = videos;
    }

    @NonNull
    @Override
    public RvDisplayImageAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new RvDisplayImageAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_display_video_rv, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDisplayImageAdapterHolder holder, int position) {
        String video = videos.get(position);
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class RvDisplayImageAdapterHolder extends RecyclerView.ViewHolder {
        CustomDisplayVideoRvBinding binding;
        String video;
        View v;

        public RvDisplayImageAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomDisplayVideoRvBinding.bind(itemView);
            v = itemView;

            binding.ivNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerNo.OnClickListener(video);
                }
            });

        }

        void bind(String video) {
            this.video = video;

            CardView card = binding.card;
            card.setVisibility(View.GONE);

//            binding.videoView.setVideoPath(video);
//            binding.videoView.start();

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
