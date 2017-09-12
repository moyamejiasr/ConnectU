package com.onelio.connectu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.NotificationManager;

public class ReceiverBoot extends BroadcastReceiver {

    //Reiniciar Alarma, debido a que un shutdown o un package_update las borra todas
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TEST ONELIO", "RESTARING ALARM FIRED BY " + intent.getAction());

        if (AppManager.isDebug())
            return; //Only activate in release mode, Reason below

        App app = (App)context.getApplicationContext();
        if (app.loadUser()) { //User logged in
            if (app.getPublicPreferenceB(Common.GLOBAL_SETTING_ISNOTIFON)) { //Are notifications on?
                NotificationManager manager = new NotificationManager(context);
                if (!manager.isAlarmOn()) {
                    manager.setRecurrentService();
                }
            }
        }
    }
}

/***
 * Important to distinguish between release & debug
 * Android execute package replaced, which is fired by Studio..
 * Which is a non sense action when they fire package removed & added too..
 * Don't ask why... just google stuff
 ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░▄▄▄░░░░░
 ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░███▀█░░░░
 ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░███░██░░░
 ░░░░░░░░░░░░░░░░░░░░░░░░░░░░░███░░██░░░
 ░░░░░░░░░░░░░░░░░░░░░░░░░░░░▄██░░░░██░░
 ░░░░░░░░░░░░░░░░▄▄█▀▀▀▀█▄▄▄▄███▄░░░██░░
 ░░░░░░░░░░░░░▄█▀░░░░░░░░░▀▀▀▀█░▀█░░██░░
 ░░░░░░░░░░░░█▀░░░░░░░░░░░░░░░▀█▄█▀▀▀░░░
 ░░░░░░░░░░░▄▀░░░░░░░░░░░░░░░░▀█▄░░░░░░░
 ░░░░░░░░░░░▀█▄░█░░▄▀░░░░░░░▄▄█░░░░░░░░░
 ░░░░░░░░▄▄▄▄█▀▀▀██▄▄░░░░░░▄▀░░░░░░░░░░░
 ░░░░▄▄██▄▀▀░░░░░█▀░░░░░░▄██▄░░░░░░░░░░░
 ░░▄██▀▀░░░░░░░░▄█░░░░░▄█▀▄█░▀▀█▄▄▄▄▄▄▄▄
 ░██▀░░░░░░░░▄██░░░░░░▄▀▄▄▀░░░░░░░░░░░░░
 █▀▀░░░░░░▄█▀▄░▀▄▄▄▄██▀▀▀░░░░░█░░░░░░░░░
 █░░░░░▄▄▀░░░░█░░░░░░░░░░░░░░░▀░░░░░░░░░
 ░▀▀▀▀▀▀░░░░░░░█░░░░░░░░░░░░░░░▀▀█▀▀▀▀▀▀
 ░░░░░░░░░░░░░░█░░░░░░░░░░░░░░░░█▀░░░░░░
 ░░░░░░░░░░░░░░█░░░░░░░░░░░░░░░▄█░░░░░░░
 */
