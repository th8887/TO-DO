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
import android.os.Build;
import android.os.Bundle;
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
import java.util.ArrayList;

public class WeeklyCalendar extends AppCompatActivity implements CalendarAdapter.OnItemClickListener, AdapterView.OnItemClickListener{

    TextView title;
    RecyclerView dates;
    ListView eventsList;
    /**
     * An array for the events in the chosen day
     */
    ArrayList<Event> dailyEvents;
    /**
     * position form the dailyEvents array
     */
    int pos;

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

        setWeekView();
    }

    /**
     * after going back to the app from google calendar, the user will see the current week
     * he chose with the updated array of events.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setEventAdapter();
    }

    private void setEventAdapter() {
        dailyEvents = Event.eventsFormDate(selectedDate);
        EventAdapter ea = new EventAdapter(getApplicationContext(), dailyEvents);
        eventsList.setAdapter(ea);
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
                        "\nAt: " + String.valueOf(e.getDate()) + ", " + e.getStart() +
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