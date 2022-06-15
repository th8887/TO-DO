package com.example.schedulenotification.Classes;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.schedulenotification.Activities.CheckList;
import com.example.schedulenotification.Activities.CreateMission;
import com.example.schedulenotification.R;

/**
 * @author		Tahel Hazan <th8887@bs.amalnet.k12.il>
 * @version	beta
 * @since		1/10/2021
 * A Broadcast receiver that shots a notification to the user about the finished mission
 * (the mission was supposed to be done by the time that the notification pops up)
 */

public class Notification extends BroadcastReceiver {
    public static final int notificationID = 1;
    public static final String channelID = "channelID";
    public static final String titleExtra = "titleExtra";
    public static final String messageExtra = "messageExtra";

    /**
     * builds a notification and shoots an intent to CheckList activity in order to check the mission
     * (will move the mission to the completed list)
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, CheckList.class);
        i.putExtra("check",1);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, i, PendingIntent.FLAG_ONE_SHOT);

        Uri alarmSound = RingtoneManager.getDefaultUri(R.raw.notifsound);

        android.app.Notification notification = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(intent.getStringExtra(titleExtra))
                .setContentText(intent.getStringExtra(messageExtra))
                .addAction(R.drawable.ic_action_check,"check",pendingIntent)
                .setSound(alarmSound)
                .build();

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(notificationID,notification);
    }
}
