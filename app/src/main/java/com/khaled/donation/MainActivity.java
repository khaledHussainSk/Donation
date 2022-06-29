package com.khaled.donation;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.khaled.donation.Models.Notifications;
import com.khaled.donation.Models.User;
import com.khaled.donation.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String USER_ID_KEY = "USER_ID_KEY";
    public final static String USER_KEY = "USER_KEY";
    public static final String count_notifications = "count_notifications";
    ActivityMainBinding binding;
    public static MeowBottomNavigation bottomNavigation;
    public static Activity context;
    Fragment fragment;
    String email;
    User currentUser;
    SharedPreferences sp;
    SharedPreferences.Editor editt;
    ArrayList<Notifications> notifications;
    String currntUserID;
    int oldCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        currntUserID = sp.getString(MainActivity.USER_ID_KEY, null);

        fixed();
        getInfo();
        bottomNavigation = binding.bottomNavigation;
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_home));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_notifications));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_add));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.ic_menu));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(5,R.drawable.ic_person));

        binding.bottomNavigation.setOnShowListener(new MeowBottomNavigation.ShowListener() {
            @Override
            public void onShowItem(MeowBottomNavigation.Model item) {
                fragment = null;
                switch (item.getId()){
                    case 1 :
                        fragment = new HomeFragment();
                        break;
                    case 2 :
                        fragment = new NotificationsFragment();
                        break;
                    case 3:
                        fragment = new AddPostFragment();
                        break;
                    case 4:
                        fragment = new MenuFragment();
                        break;
                    case 5:
                        fragment = new ProfileFragment();
                        break;
                }
                //Load fragment
                loadFragment(fragment);
                
            }
        });

        notifications = new ArrayList<>();

        FirebaseFirestore.getInstance().collection("Notifications").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            Notifications notification = queryDocumentSnapshot.toObject(Notifications.class);
                            if (notification.getId_post_owner().equals(currntUserID)){
                                notifications.add(notification);
                            }
                        }

                        if (oldCount > 0){
                            if (notifications.size() > oldCount){
                                //اذا يوجد اشعارات جديدة
                                binding.bottomNavigation.setCount(2,notifications.size() - oldCount+"");
                            }
                        }else {
                            binding.bottomNavigation.setCount(2,notifications.size()+"");
                        }

                        binding.bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
                            @Override
                            public void onClickItem(MeowBottomNavigation.Model item) {
                                if (item.getId() == 2){
                                    binding.bottomNavigation.clearCount(2);
                                    editt.putInt(count_notifications,notifications.size());
                                    editt.apply();
                                }
                            }
                        });
                        binding.bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
                            @Override
                            public void onReselectItem(MeowBottomNavigation.Model item) {
//                Toast.makeText(getBaseContext(), "You Reselected"+item.getId(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

        binding.bottomNavigation.show(1,true);


    }

    private void loadFragment(Fragment fragment) {
        // Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,fragment)
                .commit();
    }

    private void fixed(){
        context = this;
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        editt = sp.edit();
        email = sp.getString(LoginActivity.EMAIL,null);
        oldCount = sp.getInt(count_notifications,-1);
    }

    private void getInfo(){
        FirebaseFirestore.getInstance().collection("Users").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()){
                            currentUser = queryDocumentSnapshot.toObject(User.class);
                            if (currentUser.getEmail().equals(email)){
                                editt.putString(USER_ID_KEY,currentUser.getIdUser());
                                editt.apply();
                            }
                        }
                    }
                });
    }
}