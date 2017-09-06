package com.onelio.connectu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ReceiverBoot extends BroadcastReceiver {

    //Reiniciar Alarma, debido a que un shutdown o un package_update las borra todas
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TEST ONELIO", "RESTARING ALARM FIRED BY " + intent.getAction());
        //TODO RESTORE ON COMPLETE
        /*App app = (App)context.getApplicationContext();
        if (app.loadUser()) { //User logged in
            if (app.getPublicPreferenceB(Common.GLOBAL_SETTING_ISNOTIFON)) { //Are notifications on?
                NotificationManager manager = new NotificationManager(context);
                if (!manager.isAlarmOn()) {
                    manager.setRecurrentService();
                }
            }
        }*/
    }
}
