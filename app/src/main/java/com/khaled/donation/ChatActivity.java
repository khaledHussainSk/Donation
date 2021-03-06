package com.khaled.donation;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khaled.donation.Adapters.MessagesAdapter;
import com.khaled.donation.Models.Message;
import com.khaled.donation.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    FirebaseDatabase database;
    MessagesAdapter adapter;
    Message message;
    FirebaseAuth auth;
    SharedPreferences sp ;
    String currentUserId;
    public static Activity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = ChatActivity.this;
        fixed();
        sendMessage();

        Intent intent = getIntent();
        binding.messageBox.setText(intent.getStringExtra("message"));
        if (intent.getStringExtra("message") != null){

            String profile = getIntent().getStringExtra("image");
            String name = getIntent().getStringExtra("name");
            String revID = getIntent().getStringExtra("uid");

            binding.tvName.setText(name);

            final ArrayList<Message> messages = new ArrayList<>();

//        auth = FirebaseAuth.getInstance();

            final String senderId = currentUserId;
//        String recieverId = getIntent().getStringExtra("uid");

            final String senderRoom = senderId + revID;
            final String receiverRoom = revID + senderId;


            database.getReference().child("chats")
                    .child(senderRoom)
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            messages.clear();
                            for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                                message = snapshot1.getValue(Message.class);
                                message.setMessageId(snapshot1.getKey());

                                messages.add(message);
//                            Toast.makeText(getApplicationContext(), "ss : "+messages.size(), Toast.LENGTH_SHORT).show();
                                if (!message.getMessageId().isEmpty()){
                                    adapter = new MessagesAdapter(messages,getApplicationContext(),profile,revID);
                                    binding.recyclerView.setAdapter(adapter);
                                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    String message = binding.messageBox.getText().toString();
                    if (message.isEmpty() || message.equals("")) {

                    } else {
                        final Message messagem = new Message(message,senderId);
                        messagem.setTimestamp(new Date().getTime());
                        binding.messageBox.setText("");

                        database.getReference().child("chats")
                                .child(senderRoom)
                                .push()
                                .setValue(messagem).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats")
                                        .child(receiverRoom)
                                        .push()
                                        .setValue(messagem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });
                            }
                        });
                    }

            final Handler handler = new Handler();
            binding.messageBox.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    database.getReference().child("presence").child(senderId).setValue("typing...");
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(userStoppedTyping,1000);
                }

                Runnable userStoppedTyping = new Runnable() {
                    @Override
                    public void run() {
                        database.getReference().child("presence").child(senderId).setValue("Online");
                    }
                };
            });
        }

    }

    private void fixed() {
        database = FirebaseDatabase.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currentUserId = sp.getString(MainActivity.USER_ID_KEY,null);
    }

    public void sendMessage(){

        String profile = getIntent().getStringExtra("image");
        String name = getIntent().getStringExtra("name");
        String revID = getIntent().getStringExtra("uid");

        binding.tvName.setText(name);

        final ArrayList<Message> messages = new ArrayList<>();

//        auth = FirebaseAuth.getInstance();

        final String senderId = currentUserId;
//        String recieverId = getIntent().getStringExtra("uid");

        final String senderRoom = senderId + revID;
        final String receiverRoom = revID + senderId;


        database.getReference().child("chats")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messages.clear();
                        for(DataSnapshot snapshot1 : snapshot.getChildren()) {
                            message = snapshot1.getValue(Message.class);
                            message.setMessageId(snapshot1.getKey());

                            messages.add(message);
//                            Toast.makeText(getApplicationContext(), "ss : "+messages.size(), Toast.LENGTH_SHORT).show();
                            if (!message.getMessageId().isEmpty()){
                                adapter = new MessagesAdapter(messages,getApplicationContext(),profile,revID);
                                binding.recyclerView.setAdapter(adapter);
                                binding.recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String message = binding.messageBox.getText().toString();
                if (message.isEmpty() || message.equals("")) {

                } else {
                    final Message messagem = new Message(message,senderId);
                    messagem.setTimestamp(new Date().getTime());
                    binding.messageBox.setText("");

                    database.getReference().child("chats")
                            .child(senderRoom)
                            .push()
                            .setValue(messagem).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            database.getReference().child("chats")
                                    .child(receiverRoom)
                                    .push()
                                    .setValue(messagem).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                }
                            });
                        }
                    });
                }
            }
        });

        final Handler handler = new Handler();
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                database.getReference().child("presence").child(senderId).setValue("typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userStoppedTyping,1000);
            }

            Runnable userStoppedTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence").child(senderId).setValue("Online");
                }
            };
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        String currentId = FirebaseAuth.getInstance().getUid();
        database.getReference().child("presence").child(currentId).setValue("Offline");
    }
}