package com.khaled.donation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Listeners.OnClickNotificationListener;
import com.khaled.donation.Models.Notifications;
import com.khaled.donation.Models.User;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomNotificationsBinding;

import java.util.ArrayList;

public class RvNotificationsAdapter extends RecyclerView.Adapter<RvNotificationsAdapter.NotificationsViewHolder> {

    ArrayList<Notifications> notifications;
    Context context;
    OnClickNotificationListener listener;
    OnClickNotificationListener listenerDelete;

    public RvNotificationsAdapter(ArrayList<Notifications> notifications, OnClickNotificationListener listener, OnClickNotificationListener listenerDelete) {
        this.notifications = notifications;
        this.listener = listener;
        this.listenerDelete = listenerDelete;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new NotificationsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_notifications,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        Notifications notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {
        CustomNotificationsBinding binding;
        Notifications notification;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomNotificationsBinding.bind(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickListener(notification,view);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listenerDelete.OnClickListener(notification,view);
                    return false;
                }
            });
        }

        private void bind(Notifications notification){
            this.notification = notification;

            FirebaseFirestore.getInstance().collection("Users").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult() ){
                                User user = queryDocumentSnapshot.toObject(User.class);
                                if (user.getIdUser().equals(notification.getId_notifications_owner())){
                                    binding.tvName.setText(user.getFullName());
                                    Glide.with(context)
                                            .load(user.getImageProfile())
                                            .placeholder(R.drawable.ic_user4)
                                            .into(binding.profileImage);
                                }
                            }
                        }
                    });

            binding.tvDate.setText(notification.getDate_notifications());
            if (notification.getNotifications_type().equals("Like")){
                binding.tvDescription.setText(R.string.notification_like);
            }else if (notification.getNotifications_type().equals("Comment")){
                binding.tvDescription.setText(R.string.notification_comment);
            }else if (notification.getNotifications_type().equals("Follow")){
                binding.tvDescription.setText(R.string.notification_follow);
            }
        }

    }

}
