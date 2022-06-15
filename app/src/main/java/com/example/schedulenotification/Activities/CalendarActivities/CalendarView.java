package com.example.schedulenotification.Activities.CalendarActivities;

import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.daysInMonthArray;
import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.monthYearFromDate;
import static com.example.schedulenotification.CalendarHelpers.CalendarUtils.selectedDate;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.schedulenotification.Activities.About;
import com.example.schedulenotification.Activities.CheckList;
import com.example.schedulenotification.Activities.CreateMission;
import com.example.schedulenotification.Activities.Information;
import com.example.schedulenotification.Activities.TimerBlock;
import com.example.schedulenotification.CalendarHelpers.CalendarAdapter;
import com.example.schedulenotification.R;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * The type Calendar view.
 *
 * @author Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version beta
 * @since 1 /10/2021 creates the calendar for the user with an option to add events to each date.
 */
public class CalendarView extends AppCompatActivity implements CalendarAdapter.OnItemClickListener{

    /**
     * the name of the month in the year
     */
    TextView monthYear;

    RecyclerView dates;
    Toolbar tb;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        monthYear = (TextView) findViewById(R.id.monthYearTV);
        dates = (RecyclerView) findViewById(R.id.calendarRecyclerView);

        tb = (Toolbar) findViewById(R.id.tb);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        selectedDate = LocalDate.now();

        setMonthView();
    }

    /**
     * changes the name of the month and puts the list of dates inside the Recycler View
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView() {
        monthYear.setText(monthYearFromDate(selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(selectedDate);

        CalendarAdapter ca = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager lm = new GridLayoutManager(getApplicationContext(), 7);
        dates.setLayoutManager(lm);
        dates.setAdapter(ca);
    }
    /**
     * @des a function that moves the user to another activity in order to create an event there.
     * @param pos
     * @param date
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int pos, LocalDate date) {
        if(date != null){
            selectedDate = date;
            setMonthView();

            final Dialog ad = new Dialog(CalendarView.this);

            ad.setContentView(R.layout.per_day);

            final TextView tvnew = (TextView) ad.findViewById(R.id.tvnew);
            final TextView tvweekly = (TextView) ad.findViewById(R.id.tvweekly);

            tvnew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent si = new Intent(CalendarView.this, CreateEvent.class);
                    String s = String.valueOf(selectedDate);
                    si.putExtra("date", s);
                    startActivity(si);
                }
            });

            tvweekly.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i =  new Intent(CalendarView.this, WeeklyCalendar.class);
                    startActivity(i);
                }
            });
            ad.show();
        }
    }

    /**
     * moves to the next month
     *
     * @param view the view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextMonthAction(View view) {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    /**
     * moves to the previous month
     *
     * @param view the view
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousMonthAction(View view) {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
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
                i = new Intent(this, WeeklyCalendar.class);
                startActivity(i);
                finish();
                break;
            case R.id.cm:
                i= new Intent(this, CreateMission.class);
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