package com.example.schedulenotification.Adapters;


import android.content.Context;
import android.widget.ArrayAdapter;

/**
 * creates a circular list view the puts the middle object first.
 * On order to have a roll time picker in the Focus Timer Activity.
 * @param <T>
 */
public class rollAdapter< T > extends ArrayAdapter< T >
{

    public static final int HALF_MAX_VALUE = Integer.MAX_VALUE/2;
    public final int MIDDLE;
    private T[] objects;

    public rollAdapter(Context context, int textViewResourceId, T[] objects)
    {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        MIDDLE = HALF_MAX_VALUE - HALF_MAX_VALUE % objects.length;
    }

    /**
     * gets the number of objects in the list view
     * @return
     */
    @Override
    public int getCount()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    public T getItem(int position)
    {
        return objects[position % objects.length];
    }
}
