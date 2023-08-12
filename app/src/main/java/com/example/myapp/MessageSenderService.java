package com.example.myapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.telephony.SmsManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageSenderService extends Service {

    public MessageSenderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        createTextMessage("09036229568");
        createNotification(this, "Birthday free haircut message sent to Farzad");

        ArrayList<HashMap<String, String>> todayBirthdays = dbHelper.getTodayBirthdays();

        for (int i = 0; i < todayBirthdays.size(); i++) {
            // Get a customer to send message
            HashMap<String, String> customer = todayBirthdays.get(i);
            String phone = customer.get("phone");
            String name = customer.get("name");

            // Send message if didn't send for today
            if (!dbHelper.isMessageSentToday(phone)) {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                    // Send the text message
                    createTextMessage(phone);

                    // Save the message sent today for this number and birthdate to the database
                    dbHelper.saveMessageSentToday(phone);

                    // Send notification to this app user to let him know about sent message
                    createNotification(this, "Birthday free haircut message sent to " + name);
                }
            }
        }

        return START_STICKY;
    }

    private void createTextMessage(String phone) {
        // Compose the message to be sent
        String message = "\uD83C\uDF89 Happy Birthday! \uD83C\uDF89 \n" +
                "As a valued customer of Wirral Turkish Barbers, we want to celebrate your special day with you. \uD83E\uDD73 Enjoy a complimentary birthday haircut on us!\n" +
                "Book your appointment within the next 3 days to claim your free cut. Just reply to this message or give us a call at 07415174030.";

        // Send the text message
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);

//        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
//        sendIntent.putExtra("sms_body", message);
//        sendIntent.setType("vnd.android-dir/mms-sms");
//        startActivity(sendIntent);
    }

    private void createNotification(Context context, String description) {
        // Create a notification channel (required for Android Oreo and above)
        String CHANNEL_ID = "BirthdayChannel";
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
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_cake_24)
                .setContentTitle("Birthday Haircut!")
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
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
        int NOTIFICATION_ID = 1;
        startForeground(NOTIFICATION_ID, builder.build());
    }
}