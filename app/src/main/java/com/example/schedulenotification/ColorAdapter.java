package com.example.schedulenotification;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ColorAdapter extends BaseAdapter {

    Context context;
    String colorList[];
    LayoutInflater inflter;
    String [] names = {"light pink",
            "light purple",
            "ocean",
            "light gray",
            "orange",
            "green",
            "light green",
            "light yellow",
            "dark green",
            "light blue",
            "peach",
            "gray blue",
            "beige",
            "pink"};



    public ColorAdapter(Context applicationContext, String[] colorList) {
        this.context = applicationContext;
        this.colorList = colorList;

        inflter = (LayoutInflater.from(applicationContext));
    }


    @Override
    public int getCount() {
        return colorList.length;
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
        TextView colorName= (TextView) view.findViewById(R.id.colorName);
        View shape = (View) view.findViewById(R.id.color_blob);
        shape.setBackgroundColor(Color.parseColor(colorList[i]));
        colorName.setText(names[i]);

        return view;
    }
}
