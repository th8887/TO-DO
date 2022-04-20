package com.example.schedulenotification.Activities.CalendarActivities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.About;
import com.example.schedulenotification.Activities.CheckList;
import com.example.schedulenotification.Activities.CreateMission;
import com.example.schedulenotification.Activities.Information;
import com.example.schedulenotification.Activities.TimerForFocus;
import com.example.schedulenotification.Classes.Events.Event;
import com.example.schedulenotification.R;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class CreateEvent extends AppCompatActivity {

    EditText title, desEvent, locEvent;
    TextView startEvent, endEvent;
    Switch allDay;

    LocalDate ld;

    boolean a;
    /**
     * for each timer-
     * 1- timeEvent
     * 2-endEvent
     * hour,min-for timeEvent
     * hour2,min2- for endEvent
     *
     * @param - time1-> for the time in the time picker.
     */
    String time1, time2, date1, date2;
    int hour1, min1, year1, month1, day1,hour2,min2, year2, month2, day2;

    Toolbar tb;

    String [] months = {"January","Febuary","March","April","May","June"
            ,"July","August","September","October","November","December"};

    LocalDate fdate;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        title = (EditText) findViewById(R.id.t);
        desEvent = (EditText) findViewById(R.id.des);
        allDay = (Switch) findViewById(R.id.allDay);
        startEvent = (TextView) findViewById(R.id.startEvent);
        endEvent = (TextView) findViewById(R.id.endEvent);
        locEvent = (EditText) findViewById(R.id.locEvent);

        tb = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Intent gi= getIntent();
        Date currentTime = java.util.Calendar.getInstance().getTime();
        DateFormat timeFormat =  android.text.format.DateFormat.getTimeFormat(getApplicationContext());
        fdate = LocalDate.parse(gi.getStringExtra("date"));

        String s = gi.getStringExtra("date");
        int slash1 = s.indexOf("-");
        int year = Integer.parseInt(s.substring(0,slash1));
        s = s.substring(slash1+1);
        slash1 = s.indexOf("-");
        String month = months[Integer.parseInt(s.substring(0,slash1))-1]+ " ";
        int day = Integer.parseInt(s.substring(slash1+1));
        date1 = month + day+ ", " + year;
        time1 = timeFormat.format(currentTime);
        startEvent.setText(date1 + "--" + time1);
    }

    /**
     * creates a date and time picker for the beginning
     * @param view
     */
    public void start(View view) {
        createPickers(startEvent, 1);
    }

    /**
     * creates a date and time picker for the end of the event
     * @param view
     */
    public void end(View view) { createPickers(endEvent, 2); }

    /**
     * @param path-  the chosen TextView - if it's startEvent- 1
     *            if it's endEvent- 2
     * to get the right time and date to pass to the intent we need the path to each TextView in order to not write the same code twice.
     * @param t - the chosen text view in the activity(startEvent or endEvent).
     */
    public void createPickers(TextView t, int path){
        final Dialog dialog = new Dialog(CreateEvent.this);
        //We have added a title in the custom layout. So let's disable the default title.
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);
        //Mention the name of the layout of your custom dialog.
        dialog.setContentView(R.layout.date_time_pickers);

        Button set= dialog.findViewById(R.id.set);
        DatePicker dd = dialog.findViewById(R.id.dd);
        TimePicker tt = dialog.findViewById(R.id.tt);

        set.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                int minute = tt.getMinute();
                int hour = tt.getHour();
                int day = dd.getDayOfMonth();
                int month =  dd.getMonth();
                int year = dd.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, hour, minute);

                long time = calendar.getTimeInMillis();
                Date date = new Date(time);

                DateFormat dateFormat = android.text.format.DateFormat.getLongDateFormat(getApplicationContext());
                DateFormat timeFormat =  android.text.format.DateFormat.getTimeFormat(getApplicationContext());

                switch(path){
                    case 1:
                        time1 = timeFormat.format(time);
                        date1 = dateFormat.format(date);
                        break;
                    case 2:
                        time2 = timeFormat.format(time);
                        date2 = dateFormat.format(date);
                        break;
                }

                t.setText(dateFormat.format(date) + "--" + timeFormat.format(time));

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * saves the event in googleCalendar.
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveEvent(View view) {
        if (!title.getText().toString().isEmpty()&& !locEvent.getText().toString().isEmpty()) {
            if (allDay.isChecked())
                a=true;
            else
                a=false;

            String t = title.getText().toString();
            String l = locEvent.getText().toString();
            String des = desEvent.getText().toString();
            String eS = endEvent.getText().toString();
            String sS = startEvent.getText().toString();

            Event e;

            if(!desEvent.getText().toString().isEmpty()) {
                e = new Event(t, fdate, sS, eS, l, des, a);
            }
            else {
                e = new Event(t, fdate, sS, eS, l, a);
            }
            Event.eventList.add(e);

            spiltDates(day1, month1, year1, hour1, min1, date1,time1, 1);
            spiltDates(day2, month2, year2, hour2, min2, date2, time2, 2);

            Intent info = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.Events.TITLE, title.getText().toString())
                    .putExtra(CalendarContract.Events.DESCRIPTION, desEvent.getText().toString())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, locEvent.getText().toString());
            java.util.Calendar startTime = java.util.Calendar.getInstance();
            startTime.set(year1, month1-1, day1, hour1, min1);
            java.util.Calendar endTime = java.util.Calendar.getInstance();
            endTime.set(year2, month2-1, day2, hour2, min2);
            info.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                    startTime.getTimeInMillis());
            info.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                    endTime.getTimeInMillis());
            info.putExtra(CalendarContract.Events.ALL_DAY, a);
            startActivity(info);
            finish();
        }
        else
            Toast.makeText(this, "You must have a name", Toast.LENGTH_SHORT).show();
    }

    private void spiltDates(int day, int month, int year, int hour, int min, String date, String time, int path) {
        month = 0;
        while(!(month < 12 && date.startsWith(months[month]))){
            month++;
        }
        month= month + 1 ;
        date = date.substring(months[month-1].length()+1);

        int p = date.indexOf(", ");
        day = Integer.parseInt(date.substring(0,p));
        year = Integer.parseInt(date.substring(p+2));

        int dote = time.indexOf(":");
        hour = Integer.parseInt(time.substring(0,dote));
        min = Integer.parseInt(time.substring(dote+1));
         switch (path){
             case 1: day1 = day;
                     month1 = month;
                     year1 = year;
                     hour1 = hour;
                     min1 = min;
                     break;
             case 2: day2 = day;
                     month2 = month;
                     year2 = year;
                     hour2 = hour;
                     min2 = min;
                     break;
         }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent i;
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
            case R.id.ft:
                i= new Intent(this, TimerForFocus.class);
                startActivity(i);
                break;
            case R.id.ui:
                i= new Intent(this, Information.class);
                startActivity(i);
                break;
        }
        return true;
    }
}