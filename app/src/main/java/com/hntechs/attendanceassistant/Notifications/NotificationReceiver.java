package com.hntechs.attendanceassistant.Notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.hntechs.attendanceassistant.R;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("DEBUGGING","Broadcast Received");

        if(intent.getAction().equals("MorningNotification")){
            Log.i("DEBUGGING","Morning Noti Broadcast Received");

            NotificationCompat.Builder b = new NotificationCompat.Builder(context,"1");
            b.setContentTitle("Attend College")
                    .setContentText("Time to attend classes!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.icon_calender);

            NotificationManagerCompat notificationManagerCompat =NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(101,b.build());
        }

        if (intent.getAction().equals("EveningNotification")){
            Log.i("DEBUGGING","Evening Noti Broadcast Received");
            NotificationCompat.Builder b = new NotificationCompat.Builder(context,"1");
            b.setContentTitle("Today's attendance")
                    .setContentText("Time to update your todays attendance!")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.icon_calender);

            NotificationManagerCompat notificationManagerCompat =NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(102,b.build());
        }
    }
}
