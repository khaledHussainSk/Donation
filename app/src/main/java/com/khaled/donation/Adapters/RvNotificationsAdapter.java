package com.khaled.donation.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.khaled.donation.AddPhotoActivity;
import com.khaled.donation.Listeners.OnClickNotificationListener;
import com.khaled.donation.Models.Notifications;
import com.khaled.donation.Models.User;
import com.khaled.donation.PostDetailsActivity;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomNotificationsBinding;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class RvNotificationsAdapter extends RecyclerView.Adapter<RvNotificationsAdapter.NotificationsViewHolder> {

    ArrayList<Notifications> arrayList;
    Context context;
    String name;
    String image;
    NetworkInfo netInfo;
    String id;
    OnClickNotificationListener listener;
    OnClickNotificationListener listenerDelete;
    Notifications notifications;

    public RvNotificationsAdapter(ArrayList<Notifications> arrayList, OnClickNotificationListener listener, OnClickNotificationListener listenerDelete) {
        this.arrayList = arrayList;
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
        Notifications n = arrayList.get(position);

        notifications = n;

        FirebaseFirestore.getInstance().collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult() ){
                            User user = queryDocumentSnapshot.toObject(User.class);
                                if (user.getIdUser().equals(n.getId_notifications_owner())){
                                    name = user.getFullName();
                                    image = user.getImageProfile();
                                    holder.binding.tvName.setText(name);
                                    Glide.with(context).load(image).placeholder(R.drawable.ic_user4).into(holder.binding.profileImage);
                                    Toast.makeText(context, "تمت ", Toast.LENGTH_SHORT).show();
                                }
                        }
                    }
                });

        holder.binding.tvDate.setText(n.getDate_notifications());
        holder.binding.tvLike.setText(n.getNotifications_type());
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, PostDetailsActivity.class);
//                intent.putExtra(NOTIFICATION_KEY,n.getPost_id());
//                context.startActivity(intent);
//            }
//        });

//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//
//                PopupMenu popupMenu = new PopupMenu(context, holder.itemView);
//                popupMenu.inflate(R.menu.menu_post);
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.delete:
//                                if (netInfo != null) {
//                                    dialogInternet_error();
//                                } else {
//                                    androidx.appcompat.app.AlertDialog.Builder builder
//                                            = new AlertDialog.Builder(context);
//                                    builder.setTitle(R.string.delete);
//                                    builder.setMessage(R.string.delete_post);
//                                    builder.setPositiveButton(R.string.ok
//                                            , new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                                    FirebaseFirestore
//                                                            .getInstance()
//                                                            .collection("Notifications")
//                                                            .document(n.getId()).delete();
//
//                                                    Toast.makeText(context
//                                                            , R.string.toast_post_delete
//                                                            , Toast.LENGTH_SHORT).show();
//
//                                                }
//                                            });
//                                    builder.show();
//                                }
//                                return true;
//                        }
//                        return false;
//                    }
//                });
//                popupMenu.show();
//
//                return false;
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class NotificationsViewHolder extends RecyclerView.ViewHolder {
        CustomNotificationsBinding binding;

        public NotificationsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomNotificationsBinding.bind(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.OnClickListener(notifications,view);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listenerDelete.OnClickListener(notifications,view);
                    notifyDataSetChanged();
                    return false;
                }
            });
        }
    }

    private void dialogInternet_error() {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(R.string.internet_error)
                .setPositiveButton(R.string.ok, null).show();
    }
}
