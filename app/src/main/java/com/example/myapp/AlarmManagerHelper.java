package com.example.myapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmManagerHelper {

    private static final int ALARM_ID = 123;
    private static final long INTERVAL_MILLIS = 60 * 60 * 1000; // 1 hour

    public static void scheduleAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            Intent intent = new Intent(context, MessageSenderService.class);
            PendingIntent pendingIntent = PendingIntent.getService(context, ALARM_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), INTERVAL_MILLIS, pendingIntent);
        }
    }
}