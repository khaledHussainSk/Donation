package com.khaled.donation;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
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
    ArrayList<Notifications> arrayList;
    String currntUserID;
    SharedPreferences sp;
    NetworkInfo netInfo;
    public static String NOTIFICATION_KEY = "notification_key";
    //    RvNotificationsAdapter adapter;
    Notifications notifications;
//    boolean bool = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(getLayoutInflater(), container, false);

        arrayList = new ArrayList<>();
//        MainActivity.count = "0";


        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currntUserID = sp.getString(MainActivity.USER_ID_KEY, null);

        FirebaseFirestore.getInstance().collection("Notifications").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            Notifications notifications = queryDocumentSnapshot.toObject(Notifications.class);
//                            arrayList.add(notifications);
                            if (notifications.getId_post_owner().equals(currntUserID)) {
                                    arrayList.add(notifications);
                                }
                        }

                        RvNotificationsAdapter adapter = new RvNotificationsAdapter(arrayList, new OnClickNotificationListener() {
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
                                                    builder.setMessage(R.string.delete_post);
                                                    builder.setPositiveButton(R.string.ok
                                                            , new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {

                                                                    FirebaseFirestore
                                                                            .getInstance()
                                                                            .collection("Notifications")
                                                                            .document(notifications.getId()).delete();

                                                                    Toast.makeText(getContext()
                                                                            , R.string.toast_post_delete
                                                                            , Toast.LENGTH_SHORT).show();
//                                                                FirebaseFirestore.getInstance().collection("Notifications").get()
//                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                                                arrayList.clear();
////                                                                                adapter.notifyDataSetChanged();
//                                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                                                                    Notifications notifications = queryDocumentSnapshot.toObject(Notifications.class);
//                                                                                    if (notifications.getId_post_owner().equals(currntUserID)) {
//                                                                                        arrayList.add(notifications);
//                                                                                    }
//                                                                                }
//                                                                            }
//                                                                        });
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
                        binding.rv.setAdapter(adapter);
                        binding.rv.setHasFixedSize(true);
                        binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                });


//        if (!bool) {
//        FirebaseFirestore.getInstance().collection("Notifications").get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
////                            bool = true;
//                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//                                Notifications notifications = queryDocumentSnapshot.toObject(Notifications.class);
//                                if (notifications.getId_post_owner().equals(currntUserID)) {
//                                    arrayList.add(notifications);
//                                }
////                            }
//
//                                MainActivity.bottomNavigation.setCount(2, arrayList.size() + "");
//                                RvNotificationsAdapter adapter = new RvNotificationsAdapter(arrayList, new OnClickNotificationListener() {
//                                    @Override
//                                    public void OnClickListener(Notifications notifications, View view) {
////                                    if (notifications.getNotifications_type().equals("Follow")) {
////
////                                        FirebaseFirestore.getInstance().collection("Users").get()
////                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
////                                                    @Override
////                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
////                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
////
////                                                            if (queryDocumentSnapshot.toObject(User.class).getIdUser().equals(notifications.getId_notifications_owner())) {
////                                                                User user = queryDocumentSnapshot.toObject(User.class);
////                                                                Intent intent = new Intent(getContext(),
////                                                                        OtherProfileActivity.class);
////                                                                intent.putExtra(MainActivity.USER_KEY, user);
////                                                                startActivity(intent);
////                                                            }
////                                                        }
////                                                    }
////                                                });
////
////
////                                    } else {
////                                        Intent intent = new Intent(getContext(), PostDetailsActivity.class);
////                                        intent.putExtra(NOTIFICATION_KEY, notifications.getPost_id());
////                                        startActivity(intent);
////                                    }
//
//                                    }
//                                }, new OnClickNotificationListener() {
//                                    @Override
//                                    public void OnClickListener(Notifications notifications, View view) {
////                                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
////                                    popupMenu.inflate(R.menu.menu_notification);
////                                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
////                                        @Override
////                                        public boolean onMenuItemClick(MenuItem item) {
////                                            if (item.getItemId() == R.id.delete) {
////                                                if (netInfo != null) {
////                                                    dialogInternet_error();
////                                                } else {
////                                                    AlertDialog.Builder builder
////                                                            = new AlertDialog.Builder(getContext());
////                                                    builder.setTitle(R.string.delete);
////                                                    builder.setMessage(R.string.delete_post);
////                                                    builder.setPositiveButton(R.string.ok
////                                                            , new DialogInterface.OnClickListener() {
////                                                                @Override
////                                                                public void onClick(DialogInterface dialogInterface, int i) {
////
////                                                                    FirebaseFirestore
////                                                                            .getInstance()
////                                                                            .collection("Notifications")
////                                                                            .document(notifications.getId()).delete();
////
////                                                                    Toast.makeText(getContext()
////                                                                            , R.string.toast_post_delete
////                                                                            , Toast.LENGTH_SHORT).show();
//////                                                                FirebaseFirestore.getInstance().collection("Notifications").get()
//////                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//////                                                                            @Override
//////                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//////                                                                                arrayList.clear();
//////                                                                                adapter.notifyDataSetChanged();
//////                                                                                for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
//////                                                                                    Notifications notifications = queryDocumentSnapshot.toObject(Notifications.class);
//////                                                                                    if (notifications.getId_post_owner().equals(currntUserID)) {
//////                                                                                        arrayList.add(notifications);
//////                                                                                    }
//////                                                                                }
//////                                                                            }
//////                                                                        });
////                                                                }
////                                                            });
////                                                    builder.show();
////                                                }
////                                                return true;
////                                            }
////                                            return false;
////                                        }
//                                    }
////                                    );
////                                    popupMenu.show();
////                                }
//                                });
//                                binding.rv.setAdapter(adapter);
//                                binding.rv.setHasFixedSize(true);
//                                binding.rv.setLayoutManager(new LinearLayoutManager(getContext()));
//                            }
//
//                    }
//                });
//    }
        if (arrayList.size() != 0) {
            binding.tvNot.setVisibility(View.VISIBLE);
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