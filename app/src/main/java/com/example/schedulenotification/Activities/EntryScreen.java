package com.example.schedulenotification.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.example.schedulenotification.R;
/**
 * @author		Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version	beta
 * @since		1/10/2021
 * A screen with the apps logo that is shown for three seconds before the app starts.
 */

public class EntryScreen extends AppCompatActivity {
    /**
     * the time that the entry screen will stay open (1000 = 1 sec)
     */
    private static int SPLASH_TIME_OUT = 2000;

    /**
     * creates the entry screen with the logo and then closes it, and takes the user to the right activity page
     * (A new user/ didn't save his information-> Authentication page
     * An old user who saved his information in the app-> Information page)
     * @param savedInstanceState
     */
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