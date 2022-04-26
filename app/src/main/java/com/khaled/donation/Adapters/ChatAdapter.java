package com.khaled.donation.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khaled.donation.ChatActivity;
import com.khaled.donation.Models.User;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomChatBinding;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    ArrayList<User> userArrayList;
    Context context;

    public ChatAdapter(ArrayList<User> userArrayList) {
        this.userArrayList = userArrayList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_chat, parent, false);
        context = parent.getContext();
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        User user = userArrayList.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid() + user.getIdUser())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren()){
                            for (DataSnapshot snapshot1 : snapshot.getChildren()){
                                holder.binding.lastMsg.setText(snapshot1.child("message")
                                        .getValue().toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        holder.binding.username.setText(user.getFullName());

        Glide.with(context).load(user.getImageProfile())
                .placeholder(R.drawable.ic_user4)
                .into(holder.binding.profile);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("name", user.getFullName());
                intent.putExtra("image", user.getImageProfile());
                intent.putExtra("uid", user.getIdUser());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        CustomChatBinding binding;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomChatBinding.bind(itemView);
        }
    }
}
