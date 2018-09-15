package com.onelio.connectu.Managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.onelio.connectu.App;
import com.onelio.connectu.Services.AlarmNotificatorReceiver;

public class NotificationManager {

  private App app;
  private AlarmManager alarmMgr;
  private PendingIntent pendingIntent;
  private PendingIntent ppendingIntent;

  public NotificationManager(Context context) {
    app = (App) context.getApplicationContext();
    // Set service
    Intent dialogIntent = new Intent(context, AlarmNotificatorReceiver.class);
    pendingIntent =
        PendingIntent.getBroadcast(context, 0, dialogIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    ppendingIntent =
        PendingIntent.getBroadcast(context, 0, dialogIntent, PendingIntent.FLAG_NO_CREATE);
    // Set alarm
    alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
  }

  public boolean isAlarmOn() {
    return ppendingIntent == null;
    // return false;
  }

  public void deactivateRecurrentService() {
    if (alarmMgr != null) alarmMgr.cancel(pendingIntent);
  }

  public void setRecurrentService() {
    int time = app.account.NotificationTime;
    alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time, pendingIntent);
  }
}
