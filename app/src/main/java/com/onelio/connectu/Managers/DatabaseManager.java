package com.onelio.connectu.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DatabaseManager {

  // Preference
  private SharedPreferences preferences;

  public DatabaseManager(Context context) {
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
  }

  public int getInt(String name) {
    SharedPreferences.Editor editor = preferences.edit();
    int result = preferences.getInt(name, -1);
    editor.apply();
    return result;
  }

  public long getLong(String name) {
    SharedPreferences.Editor editor = preferences.edit();
    long result = preferences.getLong(name, -1);
    editor.apply();
    return result;
  }

  public boolean getBoolean(String name) {
    SharedPreferences.Editor editor = preferences.edit();
    boolean result = preferences.getBoolean(name, false);
    editor.apply();
    return result;
  }

  public String getString(String name) {
    SharedPreferences.Editor editor = preferences.edit();
    String result = preferences.getString(name, null);
    editor.apply();
    return result;
  }

  public void putLong(String name, long value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putLong(name, value);
    editor.apply();
  }

  public void putInt(String name, int value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putInt(name, value);
    editor.apply();
  }

  public void putBoolean(String name, boolean value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putBoolean(name, value);
    editor.apply();
  }

  public void putString(String name, String value) {
    SharedPreferences.Editor editor = preferences.edit();
    editor.putString(name, value);
    editor.apply();
  }

  public void deleteAll() { // MEME:: ARE U SURE ABOUT THAT???
    preferences.edit().clear().apply();
  }
}
