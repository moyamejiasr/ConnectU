package com.onelio.connectu.Managers;

import android.content.Context;
import android.os.Build;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AnuncioData;
import com.onelio.connectu.Containers.StringReminderData;
import com.onelio.connectu.Containers.TutoriaData;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReminderManager {

  public final String TYPE = "type";
  public final String DATE = "date";
  public final String OBJECT = "object";

  public enum ReminderType {
    ANUNCIO,
    TUTORIA,
    TEXT
  }

  Context context;
  App app;

  JSONArray rawObjects;

  private JSONObject getStandartJData(ReminderType type) throws JSONException {
    JSONObject jdata = new JSONObject();
    jdata.put(TYPE, type.name());
    jdata.put(DATE, (new Date()).getTime());
    return jdata;
  }

  public ReminderManager(Context context) {
    this.context = context;
    app = (App) context.getApplicationContext();

    try {
      rawObjects = new JSONArray(app.getPublicPreferenceS(""));
    } catch (JSONException e) {
      rawObjects = new JSONArray();
    }
  }

  public boolean add(StringReminderData object) {
    Gson gson = new Gson();
    try {
      JSONObject jdata = getStandartJData(ReminderType.TEXT);
      jdata.put(OBJECT, gson.toJson(object));
    } catch (JSONException e) {
      return false;
    }
    rawObjects.put(object);
    DatabaseManager database = new DatabaseManager(context);
    database.putString(Common.GLOBAL_STRINGS_REMINDERS, rawObjects.toString());
    return true;
  }

  public boolean add(AnuncioData object) {
    Gson gson = new Gson();
    try {
      JSONObject jdata = getStandartJData(ReminderType.ANUNCIO);
      jdata.put(OBJECT, gson.toJson(object));
    } catch (JSONException e) {
      return false;
    }
    rawObjects.put(object);
    DatabaseManager database = new DatabaseManager(context);
    database.putString(Common.GLOBAL_STRINGS_REMINDERS, rawObjects.toString());
    return true;
  }

  public boolean add(TutoriaData object) {
    Gson gson = new Gson();
    try {
      JSONObject jdata = getStandartJData(ReminderType.TUTORIA);
      jdata.put(OBJECT, gson.toJson(object));
    } catch (JSONException e) {
      return false;
    }
    rawObjects.put(object);
    DatabaseManager database = new DatabaseManager(context);
    database.putString(Common.GLOBAL_STRINGS_REMINDERS, rawObjects.toString());
    return true;
  }

  public boolean remove(int i) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      rawObjects.remove(i);
      DatabaseManager database = new DatabaseManager(context);
      database.putString(Common.GLOBAL_STRINGS_REMINDERS, rawObjects.toString());
      return true;
    } else {
      FirebaseCrash.report(new Exception("<Kitkat bug delete on reminder"));
      return false;
    }
  }

  public JSONArray getArrayList() {
    return rawObjects;
  }
}
