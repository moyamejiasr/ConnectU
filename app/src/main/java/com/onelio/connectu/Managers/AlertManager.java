package com.onelio.connectu.Managers;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.onelio.connectu.R;

public class AlertManager {
    //Define
    private AlertDialog.Builder builder;
    private AlertDialog dialog;

    //define callback interface
    public interface AlertCallBack {
        void onClick(boolean isPositive);
    }

    public AlertManager(Context context) {
        builder = new AlertDialog.Builder(context);
        //Default
        builder.setIcon(R.drawable.ic_announcement_black_24dp)
                .setTitle("No title");
    }

    public void setCancelable(boolean state) {
        builder.setCancelable(state);
    }

    public void setIcon(int drawable) {
        builder.setIcon(drawable);
    }

    public void setMessage(String title) {
        builder.setTitle(title);
    }

    public void setMessage(String title, String message) {
        builder.setMessage(message)
                .setTitle(title);
    }

    public void setMessage(String title, Spanned message) {
        builder.setMessage(message)
                .setTitle(title);
    }

    private EditText input;
    public void enableDataRequest(Context context) {
        input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
        builder.setView(input);
    }

    public String getInputResult() {
        return input.getText().toString();
    }

    public void setAdapter(ListAdapter adapter, DialogInterface.OnClickListener callback) {
        builder.setAdapter(adapter, callback);
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

