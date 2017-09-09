package com.onelio.connectu.Managers;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.onelio.connectu.R;

public class ErrorManager {

    //Data
    private Context context;
    private Activity activity;
    private View v;
    private String error;
    private int icon;
    private int mode = 0; // 0=Snack/1=Alert/2=Toast

    //Error messages
    public static String FAILED_CONNECTION = "_failed_connection_failed";
    public static String LOGIN_REJECTED = "_failed_login_failed";
    public static String UNKNOWN_RESPONSE_FORMAT = "_failed_response_not_expected";
    public static String FILE_DONT_EXIST = "_failed_file_not_found";
    public static String BAD_RESPONSE = "_failed_unexpected_response_code";
    public static String UNABLE_DISPLAY = "_failed_unable_to_display";
    public static String EMPTY_RESPONSE = "_failed_empty_response";
    /** Tutorias**/
    public static String TEACHER_ID_NOT_FOUND = "_failed_teacher_not_found";

    public ErrorManager(View v){
        this.v = v;
        this.context = v.getContext();
        mode = 0;
    }

    public ErrorManager(Activity activity){
        this.activity = activity;
        this.context = activity.getBaseContext();
        mode = 1;
    }

    public ErrorManager(Context context){
        this.context = context;
        mode = 2;
    }

    public String getError() {
        return error;
    }

    public int getIcon() {
        return icon;
    }

    public boolean handleError(String message) {
        if (findError(message)) {
            switch (mode) {
                case 0:
                    SnackManager snack = new SnackManager(v);
                    snack.setMessage(error, Snackbar.LENGTH_LONG);
                    snack.setIcon(icon);
                    snack.show();
                    break;
                case 1:
                    AlertManager alert = new AlertManager(activity);
                    alert.setIcon(icon);
                    alert.setPositiveButton("Ok", new AlertManager.AlertCallBack() {
                        @Override
                        public void onClick(boolean isPositive) {

                        }
                    });
                    alert.setMessage(context.getResources().getString(R.string.app_name_error), error);
                    alert.show();
                    break;
                case 2:
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return false;
    }

    private boolean findError(String message) {
        if (message.equals(FAILED_CONNECTION)) {
            error = context.getResources().getString(R.string.error_no_internet);
            icon = R.drawable.ic_signal_wifi_off_black_24dp;
            return true;
        }
        if (message.equals(LOGIN_REJECTED)) {
            error = context.getResources().getString(R.string.error_no_logged);
            icon = R.drawable.ic_no_encryption_black_24dp;
            return true;
        }
        if (message.equals(FILE_DONT_EXIST)) {
            error = context.getResources().getString(R.string.error_file_not_downloaded);
            icon = R.drawable.ic_chat_bubble_outline_black_24dp;
            return true;
        }
        if (message.equals(UNKNOWN_RESPONSE_FORMAT)) {
            error = context.getResources().getString(R.string.error_unkown_response_format);
            icon = R.drawable.ic_chat_bubble_outline_black_24dp;
            return true;
        }
        if (message.equals(BAD_RESPONSE)) {
            error = context.getResources().getString(R.string.error_bad_response);
            icon = R.drawable.ic_chat_bubble_outline_black_24dp;
            return true;
        }
        if (message.equals(UNABLE_DISPLAY)) {
            error = context.getResources().getString(R.string.error_unable_show);
            icon = R.drawable.ic_chat_bubble_outline_black_24dp;
            return true;
        }
        if (message.equals(EMPTY_RESPONSE)) {
            error = context.getResources().getString(R.string.error_empty_response);
            icon = R.drawable.ic_chat_bubble_outline_black_24dp;
            return true;
        }
        if (message.equals(TEACHER_ID_NOT_FOUND)) {
            error = context.getResources().getString(R.string.error_tutoria_n_found);
            icon = R.drawable.ic_chat_bubble_outline_black_24dp;
            return true;
        }
        return false;
    }

}
