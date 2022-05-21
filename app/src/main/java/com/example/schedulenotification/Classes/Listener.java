package com.example.schedulenotification.Classes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * A service that blocks the notifications from other apps when the user approves permission.
 */
public class Listener extends NotificationListenerService
{
    Context context;
    public static boolean status;

    @Override

    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }

    /**
     * when the notification in going to be posted, the listener cancels it and by doing
     * that it deletes the option to see the upcoming notifications.
     * (This is meant for the Focus Timer activity)
     * @param sbn
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (status) {
            String pack = sbn.getPackageName();
            String ticker = sbn.getNotification().tickerText.toString();
            Bundle extras = sbn.getNotification().extras;
            String title = extras.getString("android.title");
            String text = extras.getCharSequence("android.text").toString();

            //Log.i("Msg", pack);
            //Log.i("Msg", ticker);
            //Log.i("Msg", title);
            //Log.i("Msg", text);

            Intent msgrcv = new Intent("Msg");
            msgrcv.putExtra("package", pack);
            msgrcv.putExtra("ticker", ticker);
            msgrcv.putExtra("title", title);
            msgrcv.putExtra("text", text);
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
            //Toast.makeText(context, "GOT SOMETHING", Toast.LENGTH_LONG).show();

            Listener.this.cancelAllNotifications();
        }
        else{
            Log.i("msg", "notification created.");
        }
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
    }
}
