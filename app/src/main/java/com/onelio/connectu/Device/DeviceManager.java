package com.onelio.connectu.Device;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.onelio.connectu.Apps.Horario.HorarioActivity;
import com.onelio.connectu.HomePage;
import com.onelio.connectu.R;


public class DeviceManager {

    public static String getAppVersion(Context context) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unknown";
        }
    }

    public static void addShortcutToHorario(Context context) {
        //Adding shortcut for MainActivity
        //on Home screen
        Intent shortcutIntent = new Intent(context, HorarioActivity.class);

        shortcutIntent.setAction(Intent.ACTION_CREATE_SHORTCUT);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "HorarioUA");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context,
                        R.drawable.calendar));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);  //may it's already there so don't duplicate
        context.sendBroadcast(addIntent);
    }

    public static int appGetSrc(int position) {
        int color = 0;
        switch (position) {
            case 0:
                color = R.drawable.leaf;
                break;
            case 1:
                color = R.drawable.book;
                break;
            case 2:
                color = R.drawable.user1;
                break;
            case 3:
                color = R.drawable.chat;
                break;
            case 4:
                color = R.drawable.moodle;
                break;
            case 5:
                color = R.drawable.calendar;
                break;
            case 6:
                color = R.drawable.eva;
                break;
            case 7:
                color = R.drawable.nota;
                break;
            case 8:
                color = R.drawable.uaproject;
                break;
            case 9:
                color = R.drawable.oservices;
                break;
        }
        return color;
    }

    public static String appGetColor(int position) {
        String color = null;
        switch (position) {
            case 0:
                color = "#00BFA5";
                break;
            case 1:
                color = "#00BFA5";
                break;
            case 2:
                color = "#00BFA5";
                break;
            case 3:
                color = "#00BFA5";
                break;
            case 4:
                color = "#FF9800";
                break;
            case 5:
                color = "#00BFA5";
                break;
            case 6:
                color = "#00BFA5";
                break;
            case 7:
                color = "#E91E63";
                break;
            case 8:
                color = "#aaaaaa";
                break;
            case 9:
                color = "#428bca";
                break;
        }
        return color;
    }

    public static String capFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.toLowerCase().substring(0, 1).toUpperCase() + original.toLowerCase().substring(1);
    }

    public static String after(String string, String separator) {
        return string.substring(string.lastIndexOf(separator) + separator.length());
    }

    public static String before(String string, String separator) {
        return string.substring(0, string.indexOf(separator));
    }

    public static  boolean isStoragePermissionGranted(final Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                AlertManager alert = new AlertManager(activity);
                alert.setMessage(activity.getString(R.string.app_name), activity.getString(R.string.download_cancel));
                alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                    @Override
                    public void onClick(boolean isPositive) {
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                });
                alert.show();
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation

            return true;
        }
    }

    public static void appClose() {
        System.exit(0);
    } //TODO CHANGE EXIT

}
