package com.example.schedulenotification;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ColorAdapter extends BaseAdapter {

    Context context;
    String colorList[];
    LayoutInflater inflter;

    public ColorAdapter(Context applicationContext, String[] colorList) {
        this.context = context;
        this.colorList = colorList;
        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return 0;
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
        view = inflter.inflate(R.layout.color_cell, null);
        View shape = (View) view.findViewById(R.id.color_blob);
        shape.setBackgroundColor(Color.parseColor(colorList[i]));
        return view;
    }
}
