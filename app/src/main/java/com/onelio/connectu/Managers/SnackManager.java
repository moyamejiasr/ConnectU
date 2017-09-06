package com.onelio.connectu.Managers;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

public class SnackManager {

    private View view;
    private Snackbar snackbar;

    public SnackManager(View v) {
        view = v;
    }

    public void setMessage(String message, int duration) {
        snackbar = Snackbar.make(view, " " + message, duration);
    }

    public void setIcon(int drawable) {
        View snackView = snackbar.getView();
        TextView snackText = (TextView)snackView.findViewById(android.support.design.R.id.snackbar_text);
        snackText.setCompoundDrawablesWithIntrinsicBounds(drawable, 0, 0, 0);
    }

    public void show() {
        snackbar.show();
    }

}
