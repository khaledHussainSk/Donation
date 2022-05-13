package com.khaled.donation.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.khaled.donation.ChatActivity;
import com.khaled.donation.MainActivity;
import com.khaled.donation.Models.Message;
import com.khaled.donation.R;
import com.khaled.donation.databinding.ItemReceiveBinding;
import com.khaled.donation.databinding.ItemSentBinding;

import java.util.ArrayList;
import java.util.Date;

public class MessagesAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Message> messages;

    final int ITEM_SENT = 1;
    final int ITEM_RECEIVE = 2;
    String recId;
    String imagee;
    FirebaseRemoteConfig remoteConfig;
    SharedPreferences sp;
    String currentUserId;
    NetworkInfo netInfo;

    public MessagesAdapter( ArrayList<Message> messages,Context context,String imagee,String recId) {
        remoteConfig = FirebaseRemoteConfig.getInstance();
        this.messages = messages;
        this.imagee = imagee;
        this.recId = recId;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
         sp = PreferenceManager.getDefaultSharedPreferences(context);
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
//        if (messages.get(position).getSenderId() != null){
//            if(messages.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid())) {
            if(messages.get(position).getSenderId().equals(currentUserId)) {
                return ITEM_SENT;
            } else {
                return ITEM_RECEIVE;
            }
//        }
//        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == ITEM_SENT) {
//            context = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent, parent, false);
            return new SentViewHolder(view);
        }
        else {
//            context = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receive, parent, false);
            return new ReceiverViewHolder(view);
        }
    }
@Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    Message message = messages.get(position);


    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            if (netInfo == null){
                dialogInternet_error();
            }else {
                new AlertDialog.Builder(ChatActivity.context)
                        .setTitle("Delete")
                        .setMessage("Are you sure you want to delete this message")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderRoom = currentUserId + recId;
                                database.getReference().child("chats").child(senderRoom)
                                        .child(message.getMessageId())
                                        .setValue(null);
                                notifyDataSetChanged();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
            }

            return false;
        }
    });

            if (holder.getClass() == SentViewHolder.class) {
                ((SentViewHolder) holder).binding.message.setText(message.getMessage());

                String longV = message.getTimestamp() + "";
                long millisecond = Long.parseLong(longV);
                String dateString = DateFormat.format("MM/dd hh:mm a", new Date(millisecond)).toString();
                ((SentViewHolder) holder).binding.senderTime.setText(dateString);
            } else {
                ((ReceiverViewHolder) holder).binding.message.setText(message.getMessage());
                Glide.with(context).load(imagee).into(((ReceiverViewHolder) holder).binding.circleImageView);

                String longV = message.getTimestamp() + "";
                long millisecond = Long.parseLong(longV);
                String dateString = DateFormat.format("MM/dd hh:mm a", new Date(millisecond)).toString();
                ((ReceiverViewHolder) holder).binding.senderTime.setText(dateString);
            }

        }


    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class SentViewHolder extends RecyclerView.ViewHolder {

        ItemSentBinding binding;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSentBinding.bind(itemView);
        }
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        ItemReceiveBinding binding;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemReceiveBinding.bind(itemView);
            ConnectivityManager conMgr =  (ConnectivityManager)context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            netInfo = conMgr.getActiveNetworkInfo();

        }
    }

    private void dialogInternet_error() {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setCancelable(false)
                .setMessage(context.getResources().getString(R.string.internet_error))
                .setPositiveButton(R.string.ok, null).show();
    }

}
