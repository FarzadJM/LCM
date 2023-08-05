package com.example.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Start the BirthdayNotificationService
            Intent serviceIntent = new Intent(context, BirthdayNotificationService.class);
            context.startService(serviceIntent);
        }
    }
}