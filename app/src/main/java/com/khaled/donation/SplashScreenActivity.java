package com.khaled.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import com.khaled.donation.databinding.ActivitySplashScreenBinding;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        String isDone = sp.getString(WelcomeLoginActivity.ONBOARDING_KEY,null);
        String isChecked = sp.getString(LoginActivity.ISCHECKED_KEY,null);
        String lang = sp.getString("My_lang",null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    if (isDone == null){
                        Intent intent = new Intent(getBaseContext(),WelcomeScreenActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        if (isChecked == null){
                            Intent intent = new Intent(getBaseContext(),LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Intent intent = new Intent(getBaseContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    if (lang == null){
                        //لغة انجليزية
                        Locale locale = new Locale("en");
                        locale.setDefault(locale);
                        Configuration configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext()
                                .getResources().getDisplayMetrics());
                    }else {
                        //لغة عربية
                        Locale locale = new Locale("ar");
                        locale.setDefault(locale);
                        Configuration configuration = new Configuration();
                        configuration.locale = locale;
                        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext()
                                .getResources().getDisplayMetrics());
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}