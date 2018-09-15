package com.onelio.connectu.Activities.Preferences;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.View;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Managers.NotificationManager;
import com.onelio.connectu.R;

public class NotificationsFragment extends PreferenceFragmentCompat {

  App app;

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    addPreferencesFromResource(R.xml.notifications);
  }

  int getIdByTime(int time) {
    int loc = 0;
    String[] list = getResources().getStringArray(R.array.TimeData);
    for (int i = 0; i < list.length; i++) {
      if (list[i].equals(String.valueOf(time))) loc = i;
    }
    return loc;
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    app = (App) getContext().getApplicationContext();
    view.setBackgroundColor(Color.parseColor("#FFFFFF"));
    SwitchPreferenceCompat preference =
        (SwitchPreferenceCompat) findPreference("enableNotifications");
    preference.setOnPreferenceChangeListener(onSetAlarm);
    ListPreference delay = (ListPreference) findPreference("delayNotifications");
    delay.setOnPreferenceChangeListener(onDelayChange);
    CheckBoxPreference display = (CheckBoxPreference) findPreference("displaySilNotification");
    display.setOnPreferenceChangeListener(onDisplayChange);
    if (app.getPublicPreferenceB(Common.GLOBAL_SETTING_ISNOTIFON)) {
      preference.setChecked(true);
      delay.setEnabled(true);
      delay.setValueIndex(getIdByTime(app.account.NotificationTime));
      display.setEnabled(true);
      display.setChecked(app.getPublicPreferenceB(Common.GLOBAL_SETTING_NOTIFICATIONDISPLAY));
    } else {
      preference.setChecked(false);
      delay.setEnabled(false);
      display.setEnabled(false);
      display.setChecked(false);
    }
  }

  CheckBoxPreference.OnPreferenceChangeListener onDisplayChange =
      new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
          app.savePublicPreference(Common.GLOBAL_SETTING_NOTIFICATIONDISPLAY, (boolean) newValue);
          return true;
        }
      };

  ListPreference.OnPreferenceChangeListener onDelayChange =
      new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
          app.savePublicPreference(
              Common.PREFERENCE_INT_RECTIME, Integer.valueOf((String) newValue));
          app.account.NotificationTime = Integer.valueOf((String) newValue);
          NotificationManager manager = new NotificationManager(getContext());
          manager.deactivateRecurrentService();
          manager.setRecurrentService();
          return true;
        }
      };

  SwitchPreferenceCompat.OnPreferenceChangeListener onSetAlarm =
      new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
          NotificationManager manager = new NotificationManager(getContext());
          ListPreference delay = (ListPreference) findPreference("delayNotifications");
          CheckBoxPreference display =
              (CheckBoxPreference) findPreference("displaySilNotification");
          app.savePublicPreference(Common.GLOBAL_SETTING_ISNOTIFON, (boolean) newValue);
          if ((boolean) newValue) {
            delay.setEnabled(true);
            display.setEnabled(true);
            display.setChecked(app.getPublicPreferenceB(Common.GLOBAL_SETTING_NOTIFICATIONDISPLAY));
            manager.setRecurrentService();
          } else {
            delay.setEnabled(false);
            display.setEnabled(false);
            display.setChecked(false);
            manager.deactivateRecurrentService();
          }
          return true;
        }
      };
}
