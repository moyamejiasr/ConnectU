package com.onelio.connectu.Managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.BuildConfig;
import com.onelio.connectu.R;

import java.io.File;

public class AppManager {

    public static String capFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.toLowerCase().substring(0, 1).toUpperCase() + original.toLowerCase().substring(1);
    }

    @NonNull
    public static String after(String string, String separator) {
        return string.substring(string.lastIndexOf(separator) + separator.length());
    }

    @NonNull
    public static String before(String string, String separator) {
        return string.substring(0, string.indexOf(separator));
    }

    @NonNull
    public static String capAfterSpace(String text) {
        StringBuffer res = new StringBuffer();
        String source = text.toLowerCase();
        String[] strArr = source.split(" ");
        for (String str : strArr) {
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }

        return res.toString().trim();
    }

    public static String removeLastChars(String str, int count) {
        try {
            return str.substring(0, str.length() - count);
        }catch(StringIndexOutOfBoundsException e) {
            return str;
        }
    }

    public static  boolean isStoragePermissionGranted(final Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                AlertManager alert = new AlertManager(activity);
                alert.setMessage(activity.getString(R.string.app_name), activity.getString(R.string.error_download_cancel));
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

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static File getFileSaveLoc(String name) {
        File folderd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/ConnectU/" + name);
        if (!folderd.exists()) {
            folderd.mkdirs();
        }
        return folderd;
    }

    public static String getAppVersion(Context context) {
        PackageInfo pInfo;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            FirebaseCrash.report(e);
            return "Unknown";
        }
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void appClose() {
        System.exit(0);
    }

}
