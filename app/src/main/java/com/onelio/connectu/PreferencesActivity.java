package com.onelio.connectu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;

import com.onelio.connectu.BackgroundService.Coordinator;
import com.onelio.connectu.BackgroundService.UAService;
import com.onelio.connectu.Database.RealmManager;

import io.realm.Realm;

public class PreferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference pref;
        pref = findPreference("full_name");
        pref.setTitle(Common.name);
        pref.setSummary(Common.loginUsername);

        final CheckBoxPreference pref1;
        pref1 = (CheckBoxPreference)findPreference("notif");
        if (Common.isNotifOn) {
            pref1.setChecked(true);
        } else {
            pref1.setChecked(false);
        }
        pref1.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                RealmManager realm = new RealmManager(getBaseContext());
                if (o.equals(true)) {
                    Common.isNotifOn = true;
                    UAService.active = true;
                    //Realm
                    realm.modifyOption("isNotifOn", "yes");
                    startService(Coordinator.mServiceIntent);
                } else {
                    Common.isNotifOn = false;
                    UAService.active = false;
                    stopService(Coordinator.mServiceIntent);
                    //Realm
                    realm.modifyOption("isNotifOn", "no");
                    realm.modifyOption("NotiCount", "0");
                }
                realm.deleteRealmInstance();
                return true;
            }
        });

        Preference numeros;
        numeros = findPreference("logoutnow");
        numeros.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final AlertDialog.Builder adb = new AlertDialog.Builder(preference.getContext());
                adb.setTitle("Account Manager");
                adb.setMessage(getResources().getString(R.string.rlogout));
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        realm.deleteAll();
                        realm.commitTransaction();
                        realm.close();
                        UAService.active = false;
                        Common.isNotifOn = false;
                        Intent intent = new Intent(getApplication(), LauncherActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } });


                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    } });
                PreferencesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adb.show();
                    }
                });
                return true;
            }
        });

    }
}
