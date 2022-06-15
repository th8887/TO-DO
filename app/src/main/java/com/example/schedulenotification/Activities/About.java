package com.example.schedulenotification.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
import com.example.schedulenotification.R;

/**
 * The type About.
 *
 * @author Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version beta
 * @since 1 /10/2021 An activity with information about the creator of the app and ways to contact her is case of problems.
 */
public class About extends AppCompatActivity {


    Toolbar tb;

    ImageView profile;

    /**
     * The Path the picture gets after being uploaded.
     */
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
            getMenuInflater().inflate(R.menu.main, menu);
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
            switch(item.getItemId()){
                case R.id.i:
                    i = new Intent(this, About.class);
                    startActivity(i);
                    break;
                case R.id.cm:
                    i = new Intent(this, CreateMission.class);
                    startActivity(i);
                    break;
                case R.id.cl:
                    i= new Intent(this, CheckList.class);
                    startActivity(i);
                    break;
                case R.id.c:
                    i= new Intent(this, CalendarView.class);
                    startActivity(i);
                    break;
                case R.id.tblock:
                    i= new Intent(this, TimerBlock.class);
                    startActivity(i);
                    break;
                case R.id.ui:
                    i= new Intent(this, Information.class);
                    startActivity(i);
                    break;
            }
        }
        return true;
    }
}