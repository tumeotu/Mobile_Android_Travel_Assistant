package com.ygaps.travelapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ygaps.travelapp.R;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity
{

    private static final String SHARED_PREFERENCES_NAME = "shared_preferences_login";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // setup language
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String lang = sharedPreferences.getString("language", "vi");
        Locale.setDefault(new Locale(lang));
        Configuration config = new Configuration();
        config.setLocale(Locale.getDefault());
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        getData();
    }

    public void getData()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");
        if (token.equals(""))
        {
            Intent dialogIntent = new Intent(SplashActivity.this, Login.class);
            startActivity(dialogIntent);
            finish();
        } else
        {
            Intent dialogIntent = new Intent(SplashActivity.this, ListTour.class);
            startActivity(dialogIntent);
            finish();
        }
    }

}
