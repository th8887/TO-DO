package com.example.schedulenotification;

import static com.example.schedulenotification.refFB.reAuth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.schedulenotification.Activities.Authentication;
import com.example.schedulenotification.Activities.Information;

/**
 * A screen with the apps logo that is shown for three seconds before the app starts.
 */
public class EntryScreen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences settings = getSharedPreferences("PREFS_NAME", MODE_PRIVATE);
                Boolean isChecked = settings.getBoolean("stayConnect", false);
                if (isChecked){
                    //will go straight to the information activity.
                    Intent hs = new Intent(EntryScreen.this, Information.class);
                    startActivity(hs);
                    finish();
                }
                else{
                    //will go to the Authentication activity.
                    Intent hs = new Intent(EntryScreen.this, Authentication.class);
                    startActivity(hs);
                    finish();
                }
            }
        },SPLASH_TIME_OUT);

    }
}