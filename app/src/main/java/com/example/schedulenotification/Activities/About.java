package com.example.schedulenotification.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
import com.example.schedulenotification.Activities.CheckList;
import com.example.schedulenotification.Activities.CreateMission;
import com.example.schedulenotification.Activities.Information;
import com.example.schedulenotification.Activities.TimerForFocus;
import com.example.schedulenotification.R;

/**
 * An activity with information about the creator of the app and ways to contact her is case of problems.
 */
public class About extends AppCompatActivity {

    Toolbar tb;
    ImageView profile;

    int path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tb  = (Toolbar) findViewById(R.id.tb);
        profile = (ImageView) findViewById(R.id.profile);
        profile.setImageResource(R.drawable.profile);

        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent i = getIntent();
         path = i.getIntExtra("path",-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Intent i = getIntent();
        if(path == 1){
            menu.add("Sign-in");
        }
        else {
            menu.add("Create A Mission📝");
            menu.add("Check List📃");
            menu.add("Calendar📅");
            menu.add("Focus Timer⏱️");
            menu.add("User's Information🔎");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        if(path == 1){
            String oper = item.getTitle().toString();
            if(oper.equals("Sign-in")){
                i = new Intent(this, Authentication.class);
                startActivity(i);
            }
        }
        else {
            switch (item.getTitle().toString()) {
                case "Create A Mission📝":
                    i = new Intent(this, CreateMission.class);
                    startActivity(i);
                    break;
                case "Check List📃":
                    i = new Intent(this, CheckList.class);
                    startActivity(i);
                    break;
                case "Calendar📅":
                    i = new Intent(this, CalendarView.class);
                    startActivity(i);
                    break;
                case "Focus Timer⏱️":
                    i = new Intent(this, TimerForFocus.class);
                    startActivity(i);
                    break;
                case "User's Information🔎":
                    i = new Intent(this, Information.class);
                    startActivity(i);
                    break;
            }
        }
        return true;
    }
}