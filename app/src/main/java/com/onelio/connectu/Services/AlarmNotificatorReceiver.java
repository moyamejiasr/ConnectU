package com.onelio.connectu.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmNotificatorReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Send command to Service
        Log.e("TEST ONELIO", "FIRED NOTIFICATION CHECKER");
        context.startService(new Intent(context, UAService.class));
    }
}
