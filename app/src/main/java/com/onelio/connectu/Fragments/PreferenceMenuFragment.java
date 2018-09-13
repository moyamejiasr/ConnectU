package com.onelio.connectu.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.onelio.connectu.Activities.Preferences.AboutActivity;
import com.onelio.connectu.Activities.Preferences.NotificationsActivity;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.DatabaseManager;
import com.onelio.connectu.R;

import java.util.ArrayList;
import java.util.List;

public class PreferenceMenuFragment extends PreferenceFragmentCompat {

    App app;
    ListAdapter yearAdapter;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_preference);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        app = (App) getContext().getApplicationContext();
        setYearAdapters();
        view.setBackgroundColor(Color.parseColor("#FFFFFF"));

        Preference preference = findPreference("preference_logout");
        Preference test = findPreference("preference_notification");
        Preference about = findPreference("preference_about");
        Preference shortcut = findPreference("preference_shortcut");
        Preference shortcut1 = findPreference("preference_webmail");
        Preference tys = findPreference("preference_tys");
        Preference year = findPreference("preference_staticyear");
        Preference fastmode = findPreference("preference_fastmode");

        fastmode.setDefaultValue(app.account.fastmode);
        fastmode.setEnabled(false);

        fastmode.setOnPreferenceClickListener(onFastmodeClick);
        preference.setOnPreferenceClickListener(onLogout);
        year.setOnPreferenceClickListener(onStaticYear);
        test.setOnPreferenceClickListener(onNotificationsOpen);
        about.setOnPreferenceClickListener(onAbout);
        shortcut.setOnPreferenceClickListener(onTheme);
        shortcut1.setOnPreferenceClickListener(onTheme1);
        tys.setOnPreferenceClickListener(onTySRequest);

        String type = "release";
        if (AppManager.isDebug())
            type = "debug";

        Preference version = findPreference("preference_version");
        version.setTitle("ConnectU App version " + AppManager.getAppVersion(getContext()));
        version.setSummary("You are on " + type + " mode");
    }

    Preference.OnPreferenceClickListener onFastmodeClick = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            app.account.fastmode = !app.account.fastmode;
            DatabaseManager database = new DatabaseManager(getContext());
            database.putBoolean(Common.PREFERENCE_FASTMODE_ENABLED, app.account.fastmode);
            Log.d("FASTMODE", Boolean.toString(app.account.fastmode));
            return true;
        }
    };

    private void setYearAdapters() {
        List<String> years = new ArrayList<>();
        if (app.academicYears == null)
            return;
        for (AcademicYear year : app.academicYears) {
            years.add(year.getYear());
        }
        yearAdapter = new ArrayAdapter<>(getContext(), R.layout.view_dialog_select, years);
    }

    Preference.OnPreferenceClickListener onNotificationsOpen = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
            return true;
        }
    };

    Preference.OnPreferenceClickListener onTySRequest = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            final AlertManager alert = new AlertManager(getActivity());
            alert.setCancelable(false);
            alert.setMessage(getString(R.string.app_name), Html.fromHtml(getString(R.string.disclaimer)));
            alert.setPositiveButton(getString(R.string.accept), new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    alert.cancel();
                }
            });
            alert.show();
            return true;
        }
    };

    Preference.OnPreferenceClickListener onLogout = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            app.logoutUser(getActivity());
            return true;
        }
    };

    Preference.OnPreferenceClickListener onStaticYear = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            final AlertManager alert = new AlertManager(getContext());
            alert.setIcon(R.drawable.ic_filter_black_24dp);
            alert.setMessage(getString(R.string.year));
            alert.setNegativeButton("CANCEL", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    alert.cancel();
                }
            });
            alert.setAdapter(yearAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    app.savePublicPreference(Common.GLOBAL_FILTER_YEAR, which);
                }
            });
            alert.show();
            return true;
        }
    };

    Preference.OnPreferenceClickListener onAbout = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            startActivity(new Intent(getActivity(), AboutActivity.class));
            return true;
        }
    };

    Preference.OnPreferenceClickListener onTheme = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AppManager.addShortcutToHorario(getContext());
            Toast.makeText(getContext(), "Done!", Toast.LENGTH_LONG).show();
            return true;
        }
    };

    Preference.OnPreferenceClickListener onTheme1 = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            AppManager.addShortcutToWebmail(getContext());
            Toast.makeText(getContext(), "Done!", Toast.LENGTH_LONG).show();
            return true;
        }
    };
}
