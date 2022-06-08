package com.example.schedulenotification.Activities;

import static android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS;
import static com.example.schedulenotification.Classes.Listener.status;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.CalendarActivities.CalendarView;
import com.example.schedulenotification.R;
import com.example.schedulenotification.Adapters.rollAdapter;

import java.util.Locale;

public class TimerBlock extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView h, m, s;

    TextView cd, hourt, minutet, sect;

    Button reset, start, clear;

    Toolbar tb;
    /**
     * @param hour- gets the hour that was chosen in listView
     * @param minute - gets the minute thar was chosen in the listView
     * @param sec - gets the seconds that was chosen in the listView
     */

    long START_TIME_IN_MILLIS=0 , mTimeLeftInMillis, hour, minute, sec;

    private CountDownTimer mCountDownTimer;

    String [] hours= {"0","1", "2", "3", "4", "5", "6", "7", "8", "9","10","11",
            "12", "13", "14", "15" ,"16", "17", "18", "19", "20", "21", "22", "23", "24", "25",
            "26", "27", "28", "29" ,"30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
            "40", "41", "42", "43" ,"44", "45", "46", "47", "48", "49", "50", "51", "52", "53",
            "54", "55", "56", "57" ,"58", "59","60","61","62","63","64","65","66","67","68","69",
            "70","71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86",
            "87","88","89","90","91","92","93","94","95","96","97","98","99"};

    //minutes or sec, the same number from 0 to 59
    String [] mis = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "10", "11", "12", "13", "14", "15" , "16", "17", "18", "19", "20", "21", "22", "23",
            "24", "25", "26", "27", "28", "29" , "30", "31", "32", "33", "34", "35", "36", "37",
            "38", "39", "40", "41", "42", "43" , "44", "45", "46", "47", "48", "49", "50", "51",
            "52", "53", "54", "55", "56", "57" , "58", "59"};
    /**
     * if the time is not zero and the user wants to change it in the middle.
     */
    boolean b=false;

    private boolean mTimerRunning;

    rollAdapter<String> adph, adpm, adps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer_for_focus);

        h=(ListView) findViewById(R.id.h);
        m= (ListView) findViewById(R.id.m);
        s = (ListView) findViewById(R.id.s);
        cd=(TextView) findViewById(R.id.cd);
        reset= (Button) findViewById(R.id.reset);
        start=(Button) findViewById(R.id.start);
        clear= (Button) findViewById(R.id.clear);
        hourt = (TextView) findViewById(R.id.hourt);
        minutet = (TextView) findViewById(R.id.minutet);
        sect = (TextView) findViewById(R.id.sect);


        tb = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        h.setOnItemClickListener(this);
        h.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        m.setOnItemClickListener(this);
        m.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        s.setOnItemClickListener(this);
        s.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        adph = new rollAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,hours);
        h.setAdapter(adph);
        h.setSelectionFromTop(adph.MIDDLE, 0);

        adpm = new rollAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,mis);
        m.setAdapter(adpm);
        m.setSelectionFromTop(adpm.MIDDLE, 0);

        adps = new rollAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,mis);
        s.setAdapter(adps);
        s.setSelectionFromTop(adps.MIDDLE, 0);

        cd.setVisibility(View.VISIBLE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                h.setVisibility(View.GONE);
                m.setVisibility(View.GONE);
                s.setVisibility(View.GONE);

                cd.setVisibility(View.VISIBLE);

                if(cd.getText().toString().equals("00:00:00")||b){
                    START_TIME_IN_MILLIS = hour + minute + sec;
                    mTimeLeftInMillis = START_TIME_IN_MILLIS;
                }
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        //reset the time to the previous time that was chosen
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                START_TIME_IN_MILLIS = hour + minute + sec;
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
                resetTimer();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                START_TIME_IN_MILLIS=0;

                clear.setVisibility(View.INVISIBLE);
                reset.setVisibility(View.INVISIBLE);
                start.setVisibility(View.VISIBLE);

                cd.setText("00:00:00");
                hourt.setText("hour:");
                minutet.setText("minute:");
                sect.setText("seconds:");
                b=false;
            }
        });

        updateCountDownText();
        status = true;

        openNotificationAccess();

    }

    /**
     * asks the user for permission for the notifications in the app
     */
    private void openNotificationAccess() {
        SharedPreferences pref = this.getSharedPreferences("PERMISSION_NOTIF",MODE_PRIVATE);
        Boolean firstTime = pref.getBoolean("firstTime",true);
        if(firstTime){
            startActivity(new Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS));
            pref.edit().putBoolean("firstTime",false).apply();
        }
    }
    /**
     * @des starts the timer and the countdown, when the timer is done the user gets
     * a notification and the spinners are visible again.
     */
    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                //final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
                Toast.makeText(TimerBlock.this, "Done!", Toast.LENGTH_SHORT).show();
                playSound(R.raw.achieve);

                mTimerRunning = false;

                START_TIME_IN_MILLIS=0;

                start.setText("Start");
                start.setVisibility(View.VISIBLE);
                reset.setVisibility(View.VISIBLE);
                clear.setVisibility(View.VISIBLE);

                h.setVisibility(View.VISIBLE);
                m.setVisibility(View.VISIBLE);
                s.setVisibility(View.VISIBLE);

                b=false;
            }
        }.start();

        mTimerRunning = true;
        start.setText("pause");

        reset.setVisibility(View.INVISIBLE);
        clear.setVisibility(View.INVISIBLE);
    }

    /**
     * playes a sound when the time is done
     * @param resId
     */
    private void playSound(int resId){
        MediaPlayer mp = MediaPlayer.create(TimerBlock.this, resId);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        });
        mp.start();
    }

    /**
     * @des shows the countdown in the TextView and converting the time from milliseconds to
     * seconds, minutes and hours.
     */
    private void updateCountDownText() {
        int minutes = (int) ((mTimeLeftInMillis%3600000) / 1000) / 60;
        int seconds = (int) ((mTimeLeftInMillis%3600000) / 1000) % 60;
        seconds++;
        int hour= (int) (mTimeLeftInMillis / 3600000);

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d:%02d", hour, minutes, seconds);

        cd.setText(timeLeftFormatted);
    }


    /**
     * @des stops the timer and shows the spinners
     * again so the user can reset or change the amount of time they want.
     */
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        start.setText("Start");

        reset.setVisibility(View.VISIBLE);
        clear.setVisibility(View.VISIBLE);

        h.setVisibility(View.VISIBLE);
        m.setVisibility(View.VISIBLE);
        s.setVisibility(View.VISIBLE);
    }


    /**
     * turns the chosen time to milliseconds and if the user wants to change the time
     * when the timer is running the user adds the new time to the previous one.
     * @param par
     * @param view
     * @param pos
     */

    @Override
    public void onItemClick(AdapterView<?> par, View view, int pos, long id) {
        switch(par.getId()){
            case R.id.m:
                minutet.setText("minutes:" + adpm.getItem(pos));
                if(!cd.getText().toString().equals("00:00:00")){
                    b=true;
                }
                minute = (long) (Integer.parseInt(adpm.getItem(pos))*60000);
                break;
            case R.id.h:
                hourt.setText("hours:" + adph.getItem(pos));
                if(!cd.getText().toString().equals("00:00:00")) {
                    b = true;
                }
                hour = (long) (Integer.parseInt(adph.getItem(pos))*3600000);
                break;
            case R.id.s:
                sect.setText("seconds:" + adps.getItem(pos));
                if(!cd.getText().toString().equals("00:00:00")) {
                    b = true;
                }
                sec = (long) (Integer.parseInt(adps.getItem(pos))*1000);
                break;
        }

    }


    /**
     * @des reset the time to the time the user chose in the first place.
     */
    private void resetTimer() {
        updateCountDownText();

        reset.setVisibility(View.INVISIBLE);
        clear.setVisibility(View.INVISIBLE);

        start.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.info, menu);

        menu.add("Create A mission📝");
        menu.add("Check List📃");
        menu.add("Calendar📅");
        menu.add("User's Information🔎");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i;
        switch(item.getTitle().toString()){
            case "INFO":
                i = new Intent(this, About.class);
                startActivity(i);
                finish();
                break;
            case "Create A mission📝":
                i= new Intent(this, CreateMission.class);
                startActivity(i);
                break;
            case "Check List📃":
                i= new Intent(this, CheckList.class);
                startActivity(i);
                break;
            case "Calendar📅":
                i= new Intent(this, CalendarView.class);
                startActivity(i);
                break;
            case "User's Information🔎":
                i= new Intent(this, Information.class);
                startActivity(i);
                break;
        }
        return true;
    }
}