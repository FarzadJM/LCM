package com.example.myapp;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "BirthdayChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        ArrayList<HashMap<String, String>> todayBirthdays = dbHelper.getTodayBirthdays();

        for (int i = 0; i < todayBirthdays.size(); i++) {
            // Get a customer to send message
            HashMap<String, String> customer = todayBirthdays.get(i);
            String phone = customer.get("phone");
            String name = customer.get("name");

            // Send message if didn't send for today
            if (!dbHelper.isMessageSentToday(phone)) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    // Send the text message
                    createTextMessage(phone);

                    // Save the message sent today for this number and birthdate to the database
                    dbHelper.saveMessageSentToday(phone);

                    // Send notification to this app user to let him know about sent message
                    createNotification(context, "Birthday free haircut message sent to " + name);
                }
            }
        }
    }

    private void createTextMessage(String phone) {
        // Compose the message to be sent
        String message = "\uD83C\uDF89 Happy Birthday! \uD83C\uDF89 \n" +
                "As a valued customer of Wirral Turkish Barbers, we want to celebrate your special day with you. \uD83E\uDD73 Enjoy a complimentary birthday haircut on us!\n" +
                "Book your appointment within the next 3 days to claim your free cut. Just reply to this message or give us a call at 07415174030.";

        // Send the text message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
    }

    private void createNotification(Context context, String description) {
        // Create a notification channel (required for Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Birthday Haircut";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create an intent to launch the MainActivity when the notification is clicked
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_cake_24)
                .setContentTitle("Birthday Haircut!")
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}