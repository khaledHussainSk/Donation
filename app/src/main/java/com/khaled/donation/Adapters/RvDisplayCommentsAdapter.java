package com.khaled.donation.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.khaled.donation.Listeners.ContextMenuCommentListener;
import com.khaled.donation.Models.Comment;
import com.khaled.donation.Models.User;
import com.khaled.donation.R;
import com.khaled.donation.databinding.CustomCommentsRvBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RvDisplayCommentsAdapter
        extends RecyclerView.Adapter<RvDisplayCommentsAdapter.RvDisplayCommentsAdapterHolder> {

    Context context;
    ArrayList<Comment> comments;
    ContextMenuCommentListener listener;
    CustomCommentsRvBinding binding;

    public RvDisplayCommentsAdapter(Context context, ArrayList<Comment> comments
            ,ContextMenuCommentListener listener) {
        this.context = context;
        this.comments = comments;
        this.listener = listener;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public RvDisplayCommentsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RvDisplayCommentsAdapterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout
                .custom_comments_rv,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvDisplayCommentsAdapterHolder holder, int position) {

        Comment comment = comments.get(position);
        holder.bind(comment);

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    class RvDisplayCommentsAdapterHolder extends RecyclerView.ViewHolder{
        Comment comment;
        public RvDisplayCommentsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            binding = CustomCommentsRvBinding.bind(itemView);

        }

        private void bind(Comment comment){
            this.comment = comment;

            ImageView iv_profile = binding.ivProfile;
            TextView tv_username = binding.tvUsername;
            TextView tv_comment = binding.tvComment;
            TextView tv_date = binding.tvDate;
            TextView date = binding.date;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.contextMenuCommentListener(comment,itemView);
                }
            });

            tv_comment.setText(comment.getComment());

            getInfo(comment,iv_profile,tv_username);

            getDate(comment,tv_date,date);

        }

    }

    private void getInfo(Comment comment,ImageView iv_profile,TextView tv_username){
        FirebaseFirestore
                .getInstance()
                .collection("Users")
                .document(comment.getId_publisher())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            User user = documentSnapshot.toObject(User.class);
                            Glide.with(context)
                                    .load(user.getImageProfile())
                                    .placeholder(R.drawable.ic_user4)
                                    .into(iv_profile);
                            tv_username.setText(user.getFullName());
                        }
                    }
                });
    }

    private String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd hh:mm aa", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String previousYears(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String year(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
        String currentYear = simpleDateFormat.format(date);
        return currentYear;
    }

    private String month(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String day(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
        String currentDay = simpleDateFormat.format(date);
        return currentDay;
    }

    private String hour(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String minute(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("m", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private String second(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("s", Locale.ENGLISH);
        String dateString = simpleDateFormat.format(date);
        return dateString;
    }

    private void getDate(Comment comment, TextView tv_date,TextView date){
        Date currentDate = Calendar.getInstance().getTime();
        //currentYear
        String currentYear = year(currentDate);
        //CommentYear
        String commentYear = year(comment.getDate());

        //currentMonth
        String currentMonth = month(currentDate);
        //CommentMonth
        String commentMonth = month(comment.getDate());

        //currentDay
        String currentDay = day(currentDate);
        //CommentDay
        String commentDay = day(comment.getDate());

        //currentHour
        String currentHour = hour(currentDate);
        //CommentHour
        String commentHour = hour(comment.getDate());

        //currentMinute
        String currentMinute = minute(currentDate);
        //CommentMinute
        String commentMinute = minute(comment.getDate());

        //currentSecond
        String currentSecond = second(currentDate);
        //CommentSecond
        String commentSecond = second(comment.getDate());


        if (currentYear.equals(commentYear)){
            if (currentMonth.equals(commentMonth)){
                if (currentDay.equals(commentDay)){
                    if (currentHour.equals(commentHour)){
                        if (currentMinute.equals(commentMinute)){
                            int res = Integer.parseInt(currentSecond) - Integer.parseInt(commentSecond);
                            if (res == 1){
//                                date.setText(R.string.second_ago);
                            }else {
//                                date.setText(R.string.seconds_ago);
                            }
                            tv_date.setText(String.valueOf(res));
                        }else {
                            int res = Integer.parseInt(currentMinute) - Integer.parseInt(commentMinute);
                            if (res == 1){
//                                date.setText(R.string.minute_ago);
                            }else {
//                                date.setText(R.string.minutes_ago);
                            }
                            tv_date.setText(String.valueOf(res));
                        }
                    }else {
                        int res = Integer.parseInt(currentHour) - Integer.parseInt(commentHour);
                        if (res == 1){
//                            date.setText(R.string.hour_ago);
                        }else {
//                            date.setText(R.string.hourss_ago);
                        }
                        tv_date.setText(String.valueOf(res));
                    }
                }else {
                    tv_date.setText(formatDate(comment.getDate()));
                }
            }else {
                tv_date.setText(formatDate(comment.getDate()));
            }
        }else {
            tv_date.setText(previousYears(comment.getDate()));
        }
    }

}
