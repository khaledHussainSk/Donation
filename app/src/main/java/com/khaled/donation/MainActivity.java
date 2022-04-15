package com.khaled.donation;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.khaled.donation.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    public static MeowBottomNavigation bottomNavigation;
    public static Activity context;
    ActivityResultLauncher<String> arl;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        bottomNavigation = binding.bottomNavigation;
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.ic_home));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.ic_notifications));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.ic_add));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(4,R.drawable.ic_person));
        binding.bottomNavigation.add(new MeowBottomNavigation.Model(5,R.drawable.ic_menu));

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
                        fragment = new ProfileFragment();
                        break;
                    case 5:
                        fragment = new MenuFragment();
                        break;
                }
                //Load fragment
                loadFragment(fragment);
                
            }
        });

        //Set notification count
        binding.bottomNavigation.setCount(2,"10");
        //Set home fragment initially selected
        binding.bottomNavigation.show(1,true);
        binding.bottomNavigation.setOnClickMenuListener(new MeowBottomNavigation.ClickListener() {
            @Override
            public void onClickItem(MeowBottomNavigation.Model item) {
                Toast.makeText(getBaseContext(), "You Clicked "+item.getId(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.bottomNavigation.setOnReselectListener(new MeowBottomNavigation.ReselectListener() {
            @Override
            public void onReselectItem(MeowBottomNavigation.Model item) {
                Toast.makeText(getBaseContext(), "You Reselected"+item.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        arl = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri result) {
                        if (result != null){
                            String imagePost = String.valueOf(result);

                            binding.bottomNavigation.show(3,true);
                        }
                    }
                });

    }

    private void loadFragment(Fragment fragment) {
        // Replace fragment
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout,fragment)
                .commit();
    }

}