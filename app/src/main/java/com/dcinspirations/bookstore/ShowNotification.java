package com.dcinspirations.bookstore;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ShowNotification {

    public void notifyUser(Activity activity,
                           int notificationId,
                           String notificationChannelId,
                           String notificationChannelName,
                           String notificationChannelDescription,
                           String notificationTitle,
                           String notificationBody) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, notificationChannelId)
                .setSmallIcon(R.mipmap.micon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationBody)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationBody))
                .setColor(activity.getResources().getColor(R.color.colorPrimaryDark))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(notificationChannelDescription);
            NotificationManager manager = activity.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(activity);
            notificationManagerCompat.notify(notificationId, builder.build());
        } else {
            builder.build();
        }
    }

}
