package com.medeveloper.ayaz.boltconnect;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(new SessionManager(Splash.this).isDeviceSetUpCompleted()) {
                    startActivity(new Intent(Splash.this, BoltConnectionInterface.class));
                    finish();
                }
                else {
                    startActivity(new Intent(Splash.this, BoltRegistration.class));
                    finish();
                }

            }
        },1500);

    }
}
