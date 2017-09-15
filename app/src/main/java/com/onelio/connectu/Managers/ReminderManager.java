package com.onelio.connectu.Managers;

import android.content.Context;

import com.onelio.connectu.App;
import com.onelio.connectu.Containers.AnuncioData;

import java.util.ArrayList;
import java.util.List;

public class ReminderManager {

    Context context;
    App app;

    List<String> ids;

    ReminderManager(Context context) {
        this.context = context;
        app = (App) context.getApplicationContext();
        ids = new ArrayList<>();

        String raw = app.getPublicPreferenceS("");
    }

    public void saveReminder(String id) {

    }

}
