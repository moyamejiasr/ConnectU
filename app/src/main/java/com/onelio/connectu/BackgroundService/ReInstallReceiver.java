package com.onelio.connectu.BackgroundService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Onelio on 05/02/2017.
 */

public class ReInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TT", "App Upgraded or Reinstalled. Restarting service");
        context.startService(new Intent(context, UAService.class));
    }
}
