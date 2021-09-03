package com.albdgsldev.turismotenerife;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(MainActivity.this, ChooseSetActivity.class));
            }
        }, 2000);
    }
}