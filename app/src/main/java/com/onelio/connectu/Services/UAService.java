package com.onelio.connectu.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.onelio.connectu.API.LoginRequest;
import com.onelio.connectu.Activities.LauncherActivity;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.NotificationData;
import com.onelio.connectu.Helpers.UpdaterHelper;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;

import org.json.JSONException;

public class UAService extends Service {

    //Data
    App app;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public UAService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (!App.isFirstLaunch) {
            Log.e("ONELIO TEST", "ACTION CONTINUED, EXPECT UPDATE");
            //Run App
            app = (App) this.getApplication();
            app.initializeNetworking();
            FirebaseCrash.log("01-UAService started, cache restarted!");
            final LoginRequest login = new LoginRequest(this);
            login.createSession(new LoginRequest.LoginCallback() {
                @Override
                public void onLoginResult(boolean onResult, String message) {
                    if (onResult) {
                        if (app.loadUser()) {
                            //Continue to login
                            login.loginAccount(app.account.getEmail(), app.account.getPassword(), onUserLogin);
                        }
                    } else {
                        if (!message.equals(ErrorManager.FAILED_CONNECTION)) {
                            FirebaseCrash.report(new Exception("Failed UAService login with error: " + message));
                        }
                    }
                }
            });
        } else {
            Log.e("ONELIO TEST", "ACTION CANCELLED, IS FIRST LAUNCH");
            App.isFirstLaunch = false;
        }
        return START_STICKY;
    }

    //User now logged in and alerts updated
    LoginRequest.LoginCallback onUserLogin = new LoginRequest.LoginCallback() {
        @Override
        public void onLoginResult(final boolean onResult, final String message) {
            if (onResult) {
                //Notifications
                userSearchForNews();
                //Updates
                FirebaseCrash.log("Trying update in UAService with last time " + String.valueOf(app.lastUpdateTime));
                if (!message.equals("_ISFIRST") && UpdaterHelper.needsSilenceUpdate(app.lastUpdateTime)) {
                    //Silence Update
                    Intent intent = new Intent(UAService.this, UAUpdate.class);
                    intent.putExtra(Common.INTENT_KEY_UPDATE_TYPE, Common.UAUPDATE_TYPE_SILENCE);
                    startService(intent);
                }
            }
        }
    };

    // Build the notification, setting the group appropriately
    void userSearchForNews() {
        String title = "ConnectU";
        String text = getString(R.string.notifi_more_text);
        if (app.newNotifications.length() == 1) {
            //One new alert
            try {
                String jdata = app.newNotifications.getJSONObject(0).toString();
                Gson gson = new Gson();
                NotificationData data = gson.fromJson(jdata, NotificationData.class);
                //Set title
                title = data.getTitle();
                if (data.getTitle().isEmpty()) {
                    title = data.getType();
                }
                //Set text
                text = data.getText();
            } catch (JSONException e) {
                FirebaseCrash.log(app.newNotifications.toString());
                FirebaseCrash.report(e);
                title = "Error";
                text = "Error";
            }
        } else if (app.newNotifications.length() > 1) {
            //More than one alert
            title = getString(R.string.notifi_more_title_have) + " " + String.valueOf(app.newNotifications.length()) + " " + getString(R.string.notifi_more_title_more);
            text = getString(R.string.notifi_more_text);
        }
        if (app.newNotifications.length() != 0) {

            Intent notificationIntent = new Intent(getBaseContext(), LauncherActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            boolean display = app.getPublicPreferenceB(Common.GLOBAL_SETTING_NOTIFICATIONDISPLAY);

            Notification notif;
            if (display) {
                notif = new android.support.v7.app.NotificationCompat.Builder(getBaseContext())
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.icon)
                        .setGroup(Common.GROUP_KEY_ALERTS)
                        .setContentIntent(intent)
                        .setVibrate(new long[]{500, 500, 500, 500, 500})
                        .setLights(Color.BLUE, 3000, 3000)
                        .setSound(alarmSound)
                        .build();
            } else {
                notif = new android.support.v7.app.NotificationCompat.Builder(getBaseContext())
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setSmallIcon(R.drawable.icon)
                        .setGroup(Common.GROUP_KEY_ALERTS)
                        .setContentIntent(intent)
                        .build();
            }

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());
            notificationManager.notify(0, notif);

            //end service
            UAService.this.stopSelf();
        }
    }

}
