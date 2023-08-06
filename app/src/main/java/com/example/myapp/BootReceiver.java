package com.example.myapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Get an instance of AlarmManager
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            // Create a PendingIntent that will start the BroadcastReceiver
            Intent intent2 = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent2, PendingIntent.FLAG_IMMUTABLE);

            alarmManager.cancel(pendingIntent);

            // Set the time for the alarm (in this case, one hour from now)
            long interval = AlarmManager.INTERVAL_HOUR;
            long startTime = System.currentTimeMillis();

            // Use setExactAndAllowWhileIdle instead of setRepeating
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
        }
    }
}