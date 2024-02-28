package com.example.scraps;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Check if the message contains data payload.
        if (remoteMessage.getData().size() > 0) {
            // Handle data payload (if any).
            // This is where you can retrieve custom key-value pairs from the message payload.
            // For example:
            String customData = remoteMessage.getData().get("custom_key");
            // Perform actions based on the custom data.
        }

        // Check if the message contains notification payload.
        if (remoteMessage.getNotification() != null) {
            // Handle notification payload (if any).
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            // Create and show notification.
            sendNotification(title, body);
        }
    }

    private void sendNotification(String title, String body) {
        // Create an Intent for launching your app when the notification is tapped.
        Intent intent = new Intent(this, FoodItemScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Build the notification.
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.drawable.menu_home)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Show the notification.
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }



}
