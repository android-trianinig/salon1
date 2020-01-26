package com.training.apps.makeup.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.training.apps.makeup.R;

import gr.net.maroulis.library.EasySplashScreen;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EasySplashScreen config = new EasySplashScreen(SplashScreen.this)
                .withTargetActivity(LogInActivity.class)
                .withBackgroundResource(R.drawable.salon_splahscreen_draw)
                .withSplashTimeOut(500)
                .withFullScreen();

        View easySplashScreen = config.create();
        setContentView(easySplashScreen);
        getSupportActionBar().hide();
    }
}
