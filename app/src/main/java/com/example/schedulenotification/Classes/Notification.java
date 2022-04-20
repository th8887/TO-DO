package com.example.schedulenotification.Classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.schedulenotification.Activities.CreateMission;
import com.example.schedulenotification.R;

public class Notification extends BroadcastReceiver {
    public static final int notificationID = 1;
    public static final String channelID = "channelID";
    public static final String titleExtra = "titleExtra";
    public static final String messageExtra = "messageExtra";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, CreateMission.class);
        i.putExtra("check",1);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_ONE_SHOT);


        android.app.Notification notification = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(messageExtra))
                .addAction(R.drawable.ic_action_check,"check",pendingIntent)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationID,notification);
    }
}
