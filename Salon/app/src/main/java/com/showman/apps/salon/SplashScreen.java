package com.showman.apps.salon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withTargetActivity(LogInActivity.class)
                .withBackgroundResource(R.drawable.salon_splahscreen_draw)
                .withSplashTimeOut(5000)
                .withFullScreen();

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
        getSupportActionBar().hide();
    }
}
