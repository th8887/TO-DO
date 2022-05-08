package com.example.schedulenotification;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.schedulenotification.Classes.Mission;

import java.util.ArrayList;

public class checklistAdapter extends BaseAdapter {

    Context context;
    ArrayList<String> missionNames;
    ArrayList<Mission> missions;
    LayoutInflater inflter;

    public checklistAdapter(Context applicationContext, ArrayList<String> missionNames, ArrayList<Mission> missions){
        this.context = applicationContext;
        this.missionNames = missionNames;
        this.missions = missions;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return missionNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        view = inflter.inflate(R.layout.checklist_cell, null);
        TextView missionName= (TextView) view.findViewById(R.id.nameOfMission);
        View colorOfMission = (View) view.findViewById(R.id.colorOfMission);
        colorOfMission.setBackgroundColor(Color.parseColor(missions.get(i).getColor()));
        missionName.setText(missionNames.get(i));
        return null;
    }
}
