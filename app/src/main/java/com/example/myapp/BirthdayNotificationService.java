package com.example.myapp;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class BirthdayNotificationService extends Service {

    private static final String CHANNEL_ID = "BirthdayChannel";
    private static final int NOTIFICATION_ID = 1;

    private Timer timer;
    private DatabaseHelper dbHelper;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dbHelper = new DatabaseHelper(this);

        // Start the timer to send the text message every one hour every day
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ArrayList<HashMap<String, String>> todayBirthdays = dbHelper.getTodayBirthdays();

                for (int i = 0; i < todayBirthdays.size(); i++) {
                    // Get a customer to send message
                    HashMap<String, String> customer = todayBirthdays.get(i);
                    String phone = customer.get("phone");
                    String name = customer.get("name");

                    // Send message if didn't send for today
                    if (!dbHelper.isMessageSentToday(phone)) {
                        if (ContextCompat.checkSelfPermission(BirthdayNotificationService.this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                            // Send the text message
                            createTextMessage(phone);

                            // Save the message sent today for this number and birthdate to the database
                            dbHelper.saveMessageSentToday(phone);

                            // Send notification to this app user to let him know about sent message
                            createNotification(BirthdayNotificationService.this, "Birthday free haircut message sent to " + name);
                        }
                    }
                }
            }
    }, 0, 1000); // Run every 1 hour

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the timer when the service is destroyed
        timer.cancel();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createTextMessage(String phone) {
        // Compose the message to be sent
        String message = "Happy Birthday! It's your birthday today! You have one free haircut today, give us a visit.";

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
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
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