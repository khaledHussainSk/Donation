package com.khaled.donation;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Models.Comment;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityEditCommentBinding;

public class EditCommentActivity extends AppCompatActivity {
    ActivityEditCommentBinding binding;
    Comment comment;
    FirebaseFirestore store;
    String commentString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();
        getInfo();
        updateComment();

    }

    private void fixed() {
        Intent intent = getIntent();
        comment = (Comment) intent.getSerializableExtra(CommentsActivity.COMENT_KEY);
        store = FirebaseFirestore.getInstance();
        binding.icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getInfo(){
        store.collection("Users").document(comment.getId_publisher()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            Glide.with(getBaseContext())
                                    .load(user.getImageProfile())
                                    .placeholder(R.drawable.ic_user4)
                                    .into(binding.ivProfile);
                            binding.etComment.setText(comment.getComment());
                        }
                    }
                });
    }

    private void updateComment(){
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                commentString = binding.etComment.getText().toString();

                if (TextUtils.isEmpty(commentString) || commentString.equals("")){
                    Toast.makeText(getBaseContext(), R.string.toast_empty_comment, Toast.LENGTH_SHORT).show();
                    return;
                }

                store.collection("Comments").document(comment.getId_comment())
                        .update("comment",commentString);
                finish();
            }
        });
    }

}