package com.hntechs.attendanceassistant.Notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

public class AlarmHelper extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences alarmPref = context.getSharedPreferences("AlarmPref",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = alarmPref.edit();

        if(intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            editor.putBoolean("AlarmSet",false);
            editor.commit();
            scheduleAlarms(context);
        }

        //if(intent.getAction().equals(android.intent.action.TIME_SET)){}
    }

    public static void scheduleAlarms(Context context){

        SharedPreferences alarmPref = context.getSharedPreferences("AlarmPref",Context.MODE_PRIVATE);


        if(!alarmPref.getBoolean("AlarmSet",false)) {
            //Set Morning Notifications Alarm

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.setAction("MorningNotification");
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 6);

//====>
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);


            Intent intent1 = new Intent(context, NotificationReceiver.class);
            intent1.setAction("EveningNotification");
            alarmIntent = PendingIntent.getBroadcast(context, 1, intent1, 0);

            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 18);

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);

            SharedPreferences.Editor editor = alarmPref.edit();
            editor.putBoolean("AlarmSet",true);
            editor.commit();
            createNotificationChannel(context);
        }else {
            Log.i("DEBUGGING","AlarmAlreadySet");
        }
    }

    private static void createNotificationChannel(Context context){


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String CHANNEL_ID = "1";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"Attendance Assistant Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Assists you to maintain attendance");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
