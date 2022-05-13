package com.khaled.donation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.khaled.donation.Adapters.PagerAdapter;
import com.khaled.donation.Models.Post;
import com.khaled.donation.databinding.FragmentHomeBinding;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    ArrayList<Post> posts;
    SharedPreferences sp;
    String currentUserID;
    NetworkInfo netInfo;
    public static boolean isUploaded;
    public static ArrayList<Fragment> fragments;
    String[] category = {"الكل","ملابس وأزياء","أجهزة والكترونيات","سيارات ومركبات"
    ,"أثاث وديكور","عقارات وأملاك","حيوانات وطيور","أطعمة ومشروبات"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(getLayoutInflater(),container,false);

        fixed();

        fragments = new ArrayList<>();
        fragments.add(AllFragment.newInstance(category[0]));
        fragments.add(AllFragment.newInstance(category[1]));
        fragments.add(AllFragment.newInstance(category[2]));
        fragments.add(AllFragment.newInstance(category[3]));
        fragments.add(AllFragment.newInstance(category[4]));
        fragments.add(AllFragment.newInstance(category[5]));
        fragments.add(AllFragment.newInstance(category[6]));
        fragments.add(AllFragment.newInstance(category[7]));

        PagerAdapter adapter = new PagerAdapter(getActivity(),fragments);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout
                , binding.viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(category[position]);
            }
        }).attach();

        return binding.getRoot();

    }

    private void fixed() {
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        currentUserID = sp.getString(MainActivity.USER_ID_KEY,null);
        ConnectivityManager conMgr =  (ConnectivityManager)getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = conMgr.getActiveNetworkInfo();
        posts = new ArrayList<>();
    }
}