package com.example.networkspeed;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.drawable.IconCompat;

import java.util.Locale;

public class NetworkSpeedService extends Service {
    private Handler handler;
    private Runnable runnable;
    private long lastUpdateTime = System.currentTimeMillis();
    private long lastDownloadBytes = 0;
    private long lastUploadBytes = 0;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private String CHANNEL_ID = "NetworkSpeedChannel";;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationBuilder = new NotificationCompat.Builder(this, createNotificationChannel());

        runnable = new Runnable() {
            @Override
            public void run() {
                calculateNetworkSpeed();
                handler.postDelayed(this, 1000); // Update every second
            }
        };
        handler.post(runnable);
    }

    private String createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "NetworkSpeedChannel";
            String channelName = "Network Speed Channel";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            return channelId;
        }
        return ""; // Return an empty string for devices below Android Oreo
    }

    private void calculateNetworkSpeed() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastUpdateTime;

        if (networkCapabilities != null && elapsedTime > 0) {
            // Calculate download speed
            long currentDownloadBytes = TrafficStats.getTotalRxBytes();
            long downloadSpeed = ((currentDownloadBytes - lastDownloadBytes) * 8000) / elapsedTime; // in bits per second

            // Calculate upload speed
            long currentUploadBytes = TrafficStats.getTotalTxBytes();;
            long uploadSpeed = ((currentUploadBytes - lastUploadBytes) * 8000) / elapsedTime; // in bits per second

            // Display the network speed in the notification
            showNotification(downloadSpeed, uploadSpeed);

            // Update variables for the next iteration
            lastUpdateTime = currentTime;
            lastDownloadBytes = currentDownloadBytes;
            lastUploadBytes = currentUploadBytes;
        }
    }

    private void showNotification(long downloadSpeed, long uploadSpeed) {
        Bitmap bitmap;
        String test;
        double downloadSpeedMbps = downloadSpeed / (1024.0 * 1024.0); // Convert bits per second to Megabits per second
        double uploadSpeedMbps = uploadSpeed / (1024.0 * 1024.0); // Convert bits per second to Megabits per second
        double downloadSpeedKbps = downloadSpeedMbps * 125.0;
        double uploadSpeedKbps = uploadSpeedMbps * 125.0;

        if(downloadSpeedKbps > 1000){
            test = String.format("%.0f\nmB/s", downloadSpeedMbps);
        }else{
            test = String.format("%.0f\nkB/s", downloadSpeedKbps);

        }

        bitmap = createBitmapFromString(test.trim());
        String content = String.format(Locale.getDefault(), "Down: %.0f kB/s Up: %.0f kB/s", downloadSpeedMbps * 125.0, uploadSpeedMbps * 125.0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationBuilder.setContentText(content)
                    .setTicker(String.valueOf(uploadSpeed))
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setSmallIcon(IconCompat.createWithBitmap(bitmap).setTint(Color.TRANSPARENT));
        }

        Notification notification = notificationBuilder.build();

        startForeground(1, notification);
    }

    private Bitmap createBitmapFromString(String inputNumber) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(90);
        paint.setTextAlign(Paint.Align.CENTER);

        Rect textBounds = new Rect();
        paint.getTextBounds(inputNumber, 0, inputNumber.length(), textBounds);

        Bitmap bitmap = Bitmap.createBitmap(textBounds.width() + 10, 90,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(inputNumber, textBounds.width() / 2 + 5, 70, paint);
        return bitmap;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
