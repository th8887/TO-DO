package com.example.schedulenotification.Activities.CalendarActivities;

import static com.example.schedulenotification.Activities.CalendarActivities.CreateEvent.added;
import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.daysInWeekArray;
import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.monthYearFromDate;
import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.selectedDate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;


import com.example.schedulenotification.Activities.About;
import com.example.schedulenotification.Activities.CheckList;
import com.example.schedulenotification.Activities.CreateMission;
import com.example.schedulenotification.Activities.Information;
import com.example.schedulenotification.Activities.TimerForFocus;
import com.example.schedulenotification.CalendarHelpers.CalendarAdapter;
import com.example.schedulenotification.Classes.Events.Event;
import com.example.schedulenotification.Classes.Events.EventAdapter;
import com.example.schedulenotification.R;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Queue;
import java.util.Stack;

public class WeeklyCalendar extends AppCompatActivity implements CalendarAdapter.OnItemClickListener, AdapterView.OnItemClickListener{

    TextView title;
    RecyclerView dates;
    ListView eventsList;
    /**
     * An array for the events in the chosen day
     */
    ArrayList<Event> dailyEvents ;
    Stack<Event> dailyEvents1 = new Stack<Event>();
    /**
     * position form the dailyEvents array
     */
    int pos;
    /**
     * for the id of the calendar
     */
    String [] calendarID;

    Toolbar tb;

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

        getEvents(0);

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

        if (!added){
            dailyEvents = Event.eventsFormDate(selectedDate);
            getEvents(1);
            while(!dailyEvents1.isEmpty()){
                dailyEvents.add(dailyEvents1.pop());
            }
        }
        else{
            dailyEvents.add(Event.eventsFormDate(selectedDate).get(1));

        }
        EventAdapter ea = new EventAdapter(getApplicationContext(), dailyEvents);
        eventsList.setAdapter(ea);
    }

    /**
     * a method that ges the events from google calendar
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getEvents(int n) {
        ContentResolver contentResolver = getContentResolver();
        final Cursor cursor = contentResolver.query(CalendarContract.Calendars.CONTENT_URI,
                (new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME}), null, null, null);

        while (cursor.moveToNext()) {
            final String _id = cursor.getString(0);
            final String displayName = cursor.getString(1);


            if (displayName.equals("tshel403@gmail.com")) {
                //Log.d("Cursor", "true");
                calendarID = new String[]{_id};
            }

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();

            Calendar beginTime = Calendar.getInstance();
            beginTime.set(selectedDate.getYear()
                    , selectedDate.getMonthValue()-1
                    , selectedDate.getDayOfMonth()
                    , 8, 0);
            long startMills = beginTime.getTimeInMillis();


            ContentUris.appendId(builder, startMills);
            ContentUris.appendId(builder, Long.MAX_VALUE);

            Cursor eventCursor = contentResolver.query(builder.build(), new String[]{CalendarContract.Instances.TITLE,
                            CalendarContract.Instances.BEGIN, CalendarContract.Instances.END, CalendarContract.Instances.DESCRIPTION, CalendarContract.Instances.EVENT_LOCATION, CalendarContract.Instances.ALL_DAY},
                    CalendarContract.Instances.CALENDAR_ID + " = ?", calendarID, null);

            while (eventCursor.moveToNext()) {
                final String title = eventCursor.getString(0);
                final Date begin = new Date(eventCursor.getLong(1));
                final Date end = new Date(eventCursor.getLong(2));
                final String description = eventCursor.getString(3);
                final String location = eventCursor.getString(4);
                final String allDay = eventCursor.getString(5);
                boolean allday;

                if (Integer.parseInt(allDay) == 1){
                    allday = true;
                }
                else{
                    allday = false;
                }

                Event e = new Event(title,
                        begin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                        String.valueOf(begin),
                        String.valueOf(end),
                        location,
                        description,
                        allday);
                if (n == 0){
                    dailyEvents1.add(e);
                }
                else{
                    if (dailyEvents1.contains(e)){
                        dailyEvents1.add(e);
                    }
                }
                Log.d("Cursor", "Title: " + title + "\tDescription: " + description + "\tBegin: " + begin + "\tEnd: " + end + "\nLocation:" + location + "\nAll Day:" + allday);
            }
        }
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
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextWeekAction(View view) {
        selectedDate = selectedDate.plusWeeks(1);
        setWeekView();
    }

    /**
     * moves the user to the previous week
     * @param view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousWeekAction(View view) {
        selectedDate = selectedDate.minusWeeks(1);
        setWeekView();
    }

    /**
     * sends the user to create a new event.
     * @param view
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
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        pos = position;

        Event e = dailyEvents.get(pos);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("Scheduled Event")
                .setMessage("Title: " + e.getTitle() +
                        "\nDescription: " + e.getDes() +
                        "\nAt: " + e.getStart() +
                        "\nLocation:" + e.getLoc() +
                        "\nAll Day:" + String.valueOf(e.isAllDay()));

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