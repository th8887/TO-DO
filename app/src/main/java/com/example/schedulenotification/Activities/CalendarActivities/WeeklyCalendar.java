package com.example.schedulenotification.Activities.CalendarActivities;

import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.daysInWeekArray;
import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.monthYearFromDate;
import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.selectedDate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


import com.example.schedulenotification.Activities.About;
import com.example.schedulenotification.Activities.CheckList;
import com.example.schedulenotification.Activities.CreateMission;
import com.example.schedulenotification.Activities.Information;
import com.example.schedulenotification.Activities.TimerBlock;
import com.example.schedulenotification.CalendarHelpers.CalendarAdapter;
import com.example.schedulenotification.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * The type Weekly calendar.
 *
 * @author Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version beta
 * @since 1 /10/2021 Shows a Weekly view with the chosen date.
 */
public class WeeklyCalendar extends AppCompatActivity implements CalendarAdapter.OnItemClickListener, AdapterView.OnItemClickListener{


    TextView title;
    RecyclerView dates;
    ListView eventsList;
    /**
     * An array for the events in the chosen day
     */
    public static ArrayList<String> dailyEvents;
    /**
     * position form the dailyEvents array
     */
    int pos;

    Toolbar tb;

    /**
     * if an event was added, then the added event wont be added again in the weeklyCalendar activity
     */
    public static boolean added = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_calendar);

        title = (TextView) findViewById(R.id.monthYear);
        dates = (RecyclerView) findViewById(R.id.weekDays);
        eventsList = (ListView) findViewById(R.id.eventsList);

        tb = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        eventsList.setOnItemClickListener(this);
        eventsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        setWeekView();

    }

    /**
     * after going back to the app from google calendar, the user will see the current week
     * he chose with the updated array of events.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
    }

    /**
     * gets the events that were created in the calendar, and in the app and shows then in the listView
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setEventAdapter() {

        String[] projection = new String[] { CalendarContract.Events.CALENDAR_ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY, CalendarContract.Events.EVENT_LOCATION };

        Calendar startTime = Calendar.getInstance();


        startTime.set(selectedDate.getYear(), selectedDate.getMonthValue()-1, selectedDate.getDayOfMonth(),0,0,0);

        Calendar endTime= Calendar.getInstance();

        endTime.set(selectedDate.getYear(), selectedDate.getMonthValue()-1, selectedDate.getDayOfMonth(),23,59,59);

        String selection = "(( " + CalendarContract.Events.DTSTART + " >= " + startTime.getTimeInMillis() + " ) AND ( " + CalendarContract.Events.DTSTART + " <= " + endTime.getTimeInMillis() + " ) AND ( deleted != 1 ))";
        Cursor cursor = getApplicationContext().getContentResolver().query(CalendarContract.Events.CONTENT_URI, projection, selection, null, null);

        List<String> events = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<String>();


        if ((cursor!=null)&&(cursor.getCount()>0)&&(cursor.moveToFirst())) {
            do {
                events.add(cursor.getString(1));

                if(Integer.parseInt(cursor.getString(5)) == 1){
                    dates.add("Title: " + cursor.getString(1) +
                            "\nDescription: " + cursor.getString(2) +
                            "\nBegin: " + DateFormat.getDateInstance(DateFormat.FULL).format(new Date(Long.parseLong(cursor.getString(3))))+
                            "\nEnd: " +  DateFormat.getDateInstance(DateFormat.FULL).format(new Date(Long.parseLong(cursor.getString(4)))) +
                            "\nLocation:" + cursor.getString(6) +
                            "\nAll Day: True");
                }
                else{
                    dates.add("Title: " + cursor.getString(1) +
                            "\nDescription: " + cursor.getString(2) +
                            "\nBegin: " + DateFormat.getDateInstance(DateFormat.FULL). format(new Date(Long.parseLong(cursor.getString(3))))+ "--" +
                                    DateFormat.getTimeInstance(DateFormat.SHORT). format(new Date(Long.parseLong(cursor.getString(3))))+
                            "\nEnd: " +  DateFormat.getDateInstance(DateFormat.LONG).format(new Date(Long.parseLong(cursor.getString(4)))) + "--" +
                            DateFormat.getTimeInstance(DateFormat.SHORT). format(new Date(Long.parseLong(cursor.getString(4))))+
                            "\nLocation:" + cursor.getString(6) +
                            "\nAll Day: False");
                }
            } while ( cursor.moveToNext());
        }
        cursor.close();

        dailyEvents = dates;

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, androidx.preference.R.layout.support_simple_spinner_dropdown_item, events);
        eventsList.setAdapter(adp);


    }

    /**
     * changes the name of the month and puts the list of dates inside the Recycler View
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setWeekView() {
        title.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInWeek = daysInWeekArray(selectedDate);

        CalendarAdapter ca = new CalendarAdapter(daysInWeek, this);
        RecyclerView.LayoutManager lm = new GridLayoutManager(getApplicationContext(), 7);
        dates.setLayoutManager(lm);
        dates.setAdapter(ca);

        setEventAdapter();
    }


    /**
     * moves the user to the next week
     *
     * @param view the view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextWeekAction(View view) {
        selectedDate = selectedDate.plusWeeks(1);
        setWeekView();
    }

    /**
     * moves the user to the previous week
     *
     * @param view the view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousWeekAction(View view) {
        selectedDate = selectedDate.minusWeeks(1);
        setWeekView();
    }

    /**
     * sends the user to create a new event.
     *
     * @param view the view
     */
    public void CreateNewEvent(View view) {
        Intent i = new Intent(this, CreateEvent.class);
        i.putExtra("date", String.valueOf(selectedDate));
        startActivity(i);
    }

    /**
     * sets the selected date and builds the weekly view
     * @param pos
     * @param date
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int pos, LocalDate date) {
        selectedDate = date;
        setWeekView();
    }

    /**
     * takes the position of the event and takes the right event to show details about it.
     * @param parent
     * @param v
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent,
                            View v, int position, long id) {
        pos = position;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("Scheduled ")
                .setMessage(dailyEvents.get(pos));
        AlertDialog ad = alertDialog.create();
        ad.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.weekly_menu, menu);
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
            case R.id.swipe:
                i = new Intent(this, CalendarView.class);
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
        return true;
    }
}