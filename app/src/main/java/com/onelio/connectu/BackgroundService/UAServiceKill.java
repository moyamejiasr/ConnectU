package com.onelio.connectu.BackgroundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Onelio on 02/02/2017.
 */

public class UAServiceKill extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(UAServiceKill.class.getSimpleName(), "Service Stops!");
        context.startService(new Intent(context, UAService.class));;
    }
}
