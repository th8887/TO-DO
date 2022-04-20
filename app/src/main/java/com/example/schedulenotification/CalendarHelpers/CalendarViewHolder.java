package com.example.schedulenotification.CalendarHelpers;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schedulenotification.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final TextView dayOfMonth;
    public final View colorCell;

    private final ArrayList<LocalDate> days;

    private final CalendarAdapter.OnItemClickListener onItemClickListener;


    public CalendarViewHolder(@NonNull View itemView, ArrayList<LocalDate> days, CalendarAdapter.OnItemClickListener onItemClickListener) {
        super(itemView);
        this.dayOfMonth = itemView.findViewById(R.id.dayCell);
        this.colorCell = itemView.findViewById(R.id.colorCell);
        this.days = days;
        this.onItemClickListener = onItemClickListener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        onItemClickListener.onItemClick(getAdapterPosition(),days.get(getAdapterPosition()));
    }
}
