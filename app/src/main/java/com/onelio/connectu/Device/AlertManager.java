package com.onelio.connectu.Device;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Spanned;

import com.onelio.connectu.R;

/**
 * Created by Onelio on 14/02/2017.
 */

public class AlertManager {
    //Define
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    //define callback interface
    public interface AlertCallBack {
        void onClick(boolean isPositive);
    }

    public AlertManager(Activity activity) {
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

    public void show() {
        dialog = builder.create();
        dialog.show();
    }

    public void cancel() {
        dialog.cancel();
    }
}
