package com.example.schedulenotification.Classes.Events;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.util.ArrayList;


/**
 * A class for each event object
 */
public class Event {
    public static ArrayList<Event> eventList = new ArrayList<>();
    public static ArrayList<Event> events = new ArrayList<>();
    /**
     * looks for the events in each date and then collects them into an ArrayList
     * @param date
     * @return
     */
    public static ArrayList<Event> eventsFormDate(LocalDate date){
        for(Event event : eventList){
            if(event.getDate().equals(date)){
                events.add(event);
            }
        }

        return events;
    }

    private String title;
    private LocalDate date;
    private String start;
    private String end;
    private String loc;
    private String des;
    private boolean allDay;

    public Event(String name, LocalDate date, String start, String end, String loc, String des, boolean allDay) {
        this.title = name;
        this.date = date;
        this.start = start;
        this.end = end;
        this.loc = loc;
        this.des = des;
        this.allDay = allDay;
    }

    public Event(String name, LocalDate date, String start, String end, String loc,  boolean allDay) {
        this.title = name;
        this.date = date;
        this.start = start;
        this.end = end;
        this.loc = loc;
        this.allDay = allDay;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() { return end; }

    public void setEnd(String end) { this.end = end; }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    @Override
    public String toString() {
        return title + '\'' +
                "," + date +
                "," + start + '\'' +
                "," + end + '\'' +
                "," + loc + '\'' +
                "," + des + '\'' +
                "," + allDay +
                '}';
    }
}
