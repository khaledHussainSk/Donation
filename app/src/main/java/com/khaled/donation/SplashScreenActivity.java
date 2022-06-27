package com.khaled.donation;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.khaled.donation.databinding.ActivitySplashScreenBinding;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding;
    SharedPreferences sp;
    public static final int PERMISSIONS_REQ_CODE = 1;
    String isDone;
    String isChecked;
    String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fixed();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            //الصلاحية لم بتم الحصول عليها
            String[] permissions =  {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions,PERMISSIONS_REQ_CODE);
        }else {
            //الصلاحية تم الحصول عليها
            check();
        }

    }

    private void fixed() {
        sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        isDone = sp.getString(WelcomeLoginActivity.ONBOARDING_KEY,null);
        isChecked = sp.getString(LoginActivity.ISCHECKED_KEY,null);
        lang = sp.getString("My_lang",null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions
            , @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS_REQ_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    check();
                }else {
                    finish();
                    Toast.makeText(getBaseContext(), R.string.toast_permission_must_be_obtained, Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    private void check(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(250);
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
                        getBaseContext()
                                .getResources()
                                .updateConfiguration(configuration,getBaseContext()
                                        .getResources().getDisplayMetrics());
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}