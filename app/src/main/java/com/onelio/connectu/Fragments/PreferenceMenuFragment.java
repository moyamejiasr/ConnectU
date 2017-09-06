package com.onelio.connectu.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;
import android.widget.Toast;

import com.onelio.connectu.Activities.Preferences.AboutActivity;
import com.onelio.connectu.Activities.Preferences.NotificationsActivity;
import com.onelio.connectu.App;
import com.onelio.connectu.R;

public class PreferenceMenuFragment extends PreferenceFragmentCompat {

    App app;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.fragment_preference);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        app = (App) getContext().getApplicationContext();
        view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        Preference preference = findPreference("preference_logout");
        preference.setOnPreferenceClickListener(onLogout);
        Preference test = findPreference("preference_notification");
        test.setOnPreferenceClickListener(onNotificationsOpen);
        Preference about = findPreference("preference_about");
        about.setOnPreferenceClickListener(onAbout);
        Preference theme = findPreference("preference_theme");
        theme.setOnPreferenceClickListener(onTheme);
    }

    Preference.OnPreferenceClickListener onNotificationsOpen = new Preference.OnPreferenceClickListener() {
        @Override
        public boolean onPreferenceClick(Preference preference) {
            //app.savePublicPreference(Common.GLOBAL_FILTER_YEAR, 1);
            startActivity(new Intent(getActivity(), NotificationsActivity.class));
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
