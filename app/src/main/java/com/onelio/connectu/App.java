package com.onelio.connectu;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.onelio.connectu.Activities.LauncherActivity;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Containers.AccountData;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.DatabaseManager;
import com.onelio.connectu.Managers.NotificationManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static boolean isFirstLaunch = false; //For UAService to prevent on set
    public boolean isAppRunning = false;
    public ClearableCookieJar cookieJar;
    public AccountData account;
    public JSONObject notifications = new JSONObject();
    public JSONArray newNotifications = new JSONArray();
    //Extra
    public JSONObject publicPreferences = new JSONObject();

    //Content
    public long lastUpdateTime = 0;
    public List<AcademicYear> academicYears;
    public JSONObject horario = new JSONObject();

    //Bugfix API 16 Ressource Compat
    @Override
    public void onCreate() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate();
    }

    public void initializeNetworking() {
        //Initialize NetWorking
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getBaseContext()));
        //Initialize userData
        account = new AccountData();
    }

    public boolean loadUser() {
        DatabaseManager database = new DatabaseManager(getBaseContext());
        Gson gson = new Gson();
        boolean isLogged = database.getBoolean(Common.PREFERENCE_BOOLEAN_ISLOGGED);
        if (isLogged) {
            if (account == null)
                account = new AccountData(); //This happens when coming from ReceiverBoot
            account.setLogged(true);
            account.setEmail(database.getString(Common.PREFERENCE_STRING_EMAIL));
            account.setPassword(database.getString(Common.PREFERENCE_STRING_PASSWORD));
            account.setNotificationTime(database.getInt(Common.PREFERENCE_INT_RECTIME));
            if (account.getNotificationTime() == -1) {
                account.setNotificationTime(Common.INT_REC_TIME);
            }

            try {
                notifications = new JSONObject(database.getString(Common.PREFERENCE_JSON_NOTIFICATIONS));
            } catch (JSONException | NullPointerException e) { //If content is wrong or empty create new
                e.printStackTrace(); //dummy action for exception cause it has been initialized before
            }

            //Get Last Update
            lastUpdateTime = database.getLong(Common.PREFERENCE_LONG_LAST_UP_TIME);

            //Getting Academic Years
            String ayear = database.getString(Common.PREFERENCE_JSON_ACADEMIC_YEAR);
            Type listType = new TypeToken<ArrayList<AcademicYear>>(){}.getType();
            if (ayear != null && ayear.length() != 0) {
                academicYears = gson.fromJson(ayear, listType);
            } else {
                academicYears = new ArrayList<>();
            }

            //Getting Horario
            try {
                horario = new JSONObject(database.getString(Common.PREFERENCE_JSON_HORARIO));
            } catch (JSONException | NullPointerException e) { //If content is wrong or empty create new
                e.printStackTrace(); //dummy action for exception cause it has been initialized before
            }
            //Getting Horario Preferences
            try {
                publicPreferences = new JSONObject(database.getString(Common.PREFERENCE_JSON_PUBLICPREF));
            } catch (JSONException | NullPointerException e) { //If content is wrong or empty create new
                e.printStackTrace(); //dummy action for exception cause it has been initialized before
            }

        }
        return isLogged;
    }

    public boolean savePublicPreference(String name, int value) {
        DatabaseManager database = new DatabaseManager(getBaseContext());
        try {
            publicPreferences.put(name, value);
            database.putString(Common.PREFERENCE_JSON_PUBLICPREF, publicPreferences.toString());
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public boolean savePublicPreference(String name, String value) {
        DatabaseManager database = new DatabaseManager(getBaseContext());
        try {
            publicPreferences.put(name, value);
            database.putString(Common.PREFERENCE_JSON_PUBLICPREF, publicPreferences.toString());
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public boolean savePublicPreference(String name, boolean value) {
        DatabaseManager database = new DatabaseManager(getBaseContext());
        try {
            publicPreferences.put(name, value);
            database.putString(Common.PREFERENCE_JSON_PUBLICPREF, publicPreferences.toString());
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    public boolean getPublicPreferenceB(String name) {
        try {
            return publicPreferences.getBoolean(name);
        } catch (JSONException e) {
            return true; //Default no exist
        }
    }

    public String getPublicPreferenceS(String name) {
        try {
            return publicPreferences.getString(name);
        } catch (JSONException e) {
            return "";
        }
    }

    public int getPublicPreferenceI(String name) {
        try {
            return publicPreferences.getInt(name);
        } catch (JSONException e) {
            return 0; //Default no exist
        }
    }

    public void logoutUser(final Activity activity) {
        AlertManager alert = new AlertManager(activity);
        alert.setMessage(activity.getString(R.string.app_name), activity.getString(R.string.alert_logout));
        alert.setNegativeButton(activity.getString(R.string.no), new AlertManager.AlertCallBack() {
            @Override
            public void onClick(boolean isPositive) {
            }
        });
        alert.setPositiveButton(activity.getString(R.string.yes), new AlertManager.AlertCallBack() {
            @Override
            public void onClick(boolean isPositive) {
                NotificationManager manager = new NotificationManager(activity);
                manager.deactivateRecurrentService();
                DatabaseManager database = new DatabaseManager(activity);
                database.deleteAll();
                account = null;
                //Prevent from accessing again without load
                lastUpdateTime = 0;
                initializeNetworking();

                Intent intent = new Intent(activity, LauncherActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                activity.finish();
            }
        });
        alert.show();
    }

}
