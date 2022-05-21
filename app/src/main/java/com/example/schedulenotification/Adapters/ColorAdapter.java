package com.example.schedulenotification.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.schedulenotification.R;

/**
 * creates an adapter with colors for the spinner in CreateMission activity.
 */
public class ColorAdapter extends BaseAdapter {

    Context context;
    String colorList[];
    LayoutInflater inflter;
    String [] names;



    public ColorAdapter(Context applicationContext, String[] colorList, String [] names) {
        this.context = applicationContext;
        this.colorList = colorList;
        this.names = names;

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
        TextView shape = (TextView) view.findViewById(R.id.color_blob);
        shape.setTextColor(Color.parseColor(colorList[i]));
        colorName.setText(names[i]);

        return view;
    }
}
