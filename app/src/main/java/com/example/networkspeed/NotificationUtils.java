package com.example.networkspeed;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationUtils {
    private static final String CHANNEL_ID = "NetworkSpeedChannel";
    private static final String CHANNEL_NAME = "Network Speed Channel";

    private static Notification notification;

    public static void createNotification(Context context, String title, String content) {
        createNotificationChannel(context);

        notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentText(content)
                .build();
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    public static Notification getNotification() {
        return notification;
    }
}
