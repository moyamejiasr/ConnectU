package com.onelio.connectu;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.onelio.connectu.Activities.LauncherActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

  @Override
  public void onMessageReceived(RemoteMessage remoteMessage) {
    String name = remoteMessage.getNotification().getTitle();
    String body = remoteMessage.getNotification().getBody();

    Intent notificationIntent = new Intent(getBaseContext(), LauncherActivity.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);
    NotificationCompat.Builder mBuilder =
        new NotificationCompat.Builder(getBaseContext())
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(intent)
            .setContentTitle(name)
            .setContentText(body)
            .setAutoCancel(true);
    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    mNotifyMgr.notify(1, mBuilder.build());
  }
}
