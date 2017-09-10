package com.onelio.connectu.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.onelio.connectu.Activities.LoginActivity;
import com.onelio.connectu.Activities.Preferences.AboutActivity;
import com.onelio.connectu.Activities.Preferences.NotificationsActivity;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Managers.AlertManager;
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        app = (App) getContext().getApplicationContext();
        setYearAdapters();
        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        Preference preference = findPreference("preference_logout");
        preference.setOnPreferenceClickListener(onLogout);
        Preference year = findPreference("preference_staticyear");
        year.setOnPreferenceClickListener(onStaticYear);
        Preference test = findPreference("preference_notification");
        test.setOnPreferenceClickListener(onNotificationsOpen);
        Preference about = findPreference("preference_about");
        about.setOnPreferenceClickListener(onAbout);
        Preference theme = findPreference("preference_theme");
        theme.setOnPreferenceClickListener(onTheme);
        Preference tys = findPreference("preference_tys");
        tys.setOnPreferenceClickListener(onTySRequest);
    }

    private void setYearAdapters() {
        List<String> years = new ArrayList<>();
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
            Toast.makeText(getContext(), getString(R.string.not_available), Toast.LENGTH_SHORT).show();
            return true;
        }
    };
}
