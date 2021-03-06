package com.khaled.donation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Adapters.RvNotificationsAdapter;
import com.khaled.donation.Listeners.OnClickNotificationListener;
import com.khaled.donation.Models.Notifications;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.FragmentNotificationsBinding;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    FragmentNotificationsBinding binding;
    ArrayList<Notifications> notifications;
    String currntUserID;
    SharedPreferences sp;
    SharedPreferences.Editor editt;
    NetworkInfo netInfo;
    public static String NOTIFICATION_KEY = "notification_key";
    public static final String count_notifications = "count_notifications";
    RvNotificationsAdapter rvNotificationsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(getLayoutInflater(), container, false);

        notifications = new ArrayList<>();

        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currntUserID = sp.getString(MainActivity.USER_ID_KEY, null);
        editt = sp.edit();

        FirebaseFirestore.getInstance().collection("Notifications").orderBy("date_notifications", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Notifications notification = queryDocumentSnapshot.toObject(Notifications.class);
                            if (notification.getId_post_owner().equals(currntUserID)) {
                                notifications.add(notification);
                                }
                        }

                            rvNotificationsAdapter = new RvNotificationsAdapter(notifications
                                    , new OnClickNotificationListener() {
                            @Override
                            public void OnClickListener(Notifications notifications, View view) {
                                if (notifications.getNotifications_type().equals("Follow")) {
                                        FirebaseFirestore.getInstance().collection("Users").get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {

                                                            if (queryDocumentSnapshot.toObject(User.class).getIdUser().equals(notifications.getId_notifications_owner())) {
                                                                User user = queryDocumentSnapshot.toObject(User.class);
                                                                Intent intent = new Intent(getContext(),
                                                                        OtherProfileActivity.class);
                                                                intent.putExtra(MainActivity.USER_KEY, user);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                    }
                                                });


                                    } else {
                                        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
                                        intent.putExtra(NOTIFICATION_KEY, notifications.getPost_id());
                                        startActivity(intent);
                                    }
                            }
                        }, new OnClickNotificationListener() {
                            @Override
                            public void OnClickListener(Notifications notifications, View view) {
                                PopupMenu popupMenu = new PopupMenu(getContext(), view);
                                    popupMenu.inflate(R.menu.menu_notification);
                                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                        @Override
                                        public boolean onMenuItemClick(MenuItem item) {
                                            if (item.getItemId() == R.id.delete) {
                                                if (netInfo != null) {
                                                    dialogInternet_error();
                                                } else {
                                                    AlertDialog.Builder builder
                                                            = new AlertDialog.Builder(getContext());
                                                    builder.setTitle(R.string.delete);
                                                    builder.setMessage(R.string.delete_notification);
                                                    builder.setPositiveButton(R.string.ok
                                                            , new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                                    FirebaseFirestore
                                                                            .getInstance()
                                                                            .collection("Notifications")
                                                                            .document(notifications.getId()).delete();

                                                                    int value = sp.getInt(count_notifications+sp.getString(LoginActivity.EMAIL,null),-1);
                                                                    editt.putInt(count_notifications+sp.getString(LoginActivity.EMAIL,null),value - 1);
                                                                    editt.apply();
                                                                    Toast.makeText(getContext()
                                                                            , R.string.toast_notification_delete
                                                                            , Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                    builder.show();
                                                }
                                                return true;
                                            }
                                            return false;
                                        }
                                    }
                                    );
                                    popupMenu.show();
                            }
                        });
                        binding.rv.setAdapter(rvNotificationsAdapter);
                        binding.rv.setHasFixedSize(true);
                        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                });


        if (notifications.size() != 0) {
            binding.tvNot.setVisibility(View.VISIBLE);
        }else {
            binding.tvNot.setVisibility(View.GONE);
        }

        return binding.getRoot();
    }

    private void dialogInternet_error() {
        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage(R.string.internet_error)
                .setPositiveButton(R.string.ok, null).show();
    }
}