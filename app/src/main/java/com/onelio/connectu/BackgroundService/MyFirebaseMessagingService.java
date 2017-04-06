package com.onelio.connectu.BackgroundService;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.LauncherActivity;
import com.onelio.connectu.R;

/**
 * Created by Onelio on 14/03/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.w(TAG, "HI");
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

        /*if (!isMyServiceRunning(Coordinator.mSensorService.getClass())) {
            startService(Coordinator.mServiceIntent);
            Log.w(TAG, body);
            DeviceManager.appClose();
        }*/

    }
    // [END receive_message]
}