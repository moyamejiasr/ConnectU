package com.onelio.connectu.Device;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Spanned;
import android.widget.Toast;

import com.onelio.connectu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onelio on 14/02/2017.
 */

public class AlertManager {
    //Define
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    private Activity activity;

    //define callback interface
    public interface AlertCallBack {
        void onClick(boolean isPositive);
    }

    public AlertManager(Activity activity) {
        this.activity = activity;
        builder = new AlertDialog.Builder(activity);
        //Default
        builder.setIcon(R.drawable.advw)
                .setMessage("No message")
                .setTitle("No title");
    }

    public void setCancelable(boolean state) {
        builder.setCancelable(state);
    }

    public void setIcon(int drawable) {
        builder.setIcon(drawable);
    }

    public void setMessage(String title, String message) {
        builder.setMessage(message)
                .setTitle(title);
    }

    public void setMessage(String title, Spanned message) {
        builder.setMessage(message)
                .setTitle(title);
    }

    public void setPositiveButton(String text, final AlertCallBack callBack) {
        builder.setPositiveButton(text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                callBack.onClick(true);
            }
        });
    }

    public void setNegativeButton(String text, final AlertCallBack callBack) {
        builder.setNegativeButton(text, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callBack.onClick(false);
            }
        });
    }

    private boolean getIfRunning(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !activity.isDestroyed();
        } else {
            return !activity.isFinishing();
        }
    }

    public void show() {
        if (getIfRunning(activity)) {
            dialog = builder.create();
            dialog.show();
        }
    }

    public void cancel() {
        dialog.cancel();
    }
}
