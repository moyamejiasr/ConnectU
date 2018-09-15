package com.onelio.connectu.Helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import com.onelio.connectu.App;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.Containers.SubjectData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public class ObjectHelper {

  // Calendar code
  public static int getActualLocFromCalendarList(List<CalendarEvent> events) {
    int loc = 0;
    for (int i = 0; i < events.size(); i++) {
      CalendarEvent event = events.get(i);
      if (event.getStart().getTime() <= new Date().getTime()) {
        loc = i;
      }
    }
    return loc;
  }

  // Profesores Code
  public static SubjectData getSubjectById(List<SubjectData> subjects, String id) {
    for (SubjectData subject : subjects) {
      if (subject.getId().equals(id)) {
        return subject;
      }
    }
    return null;
  }

  // Evaluacion Code
  public static List<String> getSubjectsName(Context context, int year, boolean withAll) {
    App app = (App) context.getApplicationContext();
    List<String> subjects = new ArrayList<>();
    if (withAll) {
      subjects.add(context.getString(R.string.filter_all_subjects));
    }
    for (SubjectData subject : app.academicYears.get(year).getSubjectsData()) {
      String sname =
          AppManager.after(subject.getName(), app.academicYears.get(year).getYear() + " ");
      sname = AppManager.before(sname, "(" + subject.getId());
      subjects.add(AppManager.capFirstLetter(sname));
    }
    return subjects;
  }

  public static int pxToDp(Context context, int px) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
  }

  public static int dpToPx(Context context, int dp) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
  }

  public static String getPlace(Context context, String sigua) {
    try {
      String raw = LoadFile("places", context);
      JSONArray jdata = new JSONArray(raw);
      for (int i = 0; i < jdata.length(); i++) {
        if (jdata.getJSONObject(i).getString("ID").equals(sigua))
          return jdata.getJSONObject(i).getString("PLACE");
      }
      return "";
    } catch (IOException | JSONException e) {
      return "";
    }
  }

  public static String getPlace(String raw, String sigua) {
    try {
      JSONArray jdata = new JSONArray(raw);
      for (int i = 0; i < jdata.length(); i++) {
        if (jdata.getJSONObject(i).getString("ID").equals(sigua))
          return jdata.getJSONObject(i).getString("PLACE");
      }
      return "";
    } catch (JSONException e) {
      return "";
    }
  }

  // load file from apps res/raw folder or Assets folder
  public static String LoadFile(String fileName, Context context) throws IOException {
    // Create a InputStream to read the file into
    InputStream iS;

    // get the resource id from the file name
    int rID =
        context.getResources().getIdentifier("com.onelio.connectu:raw/" + fileName, null, null);
    // get the file as a stream
    iS = context.getResources().openRawResource(rID);

    // create a buffer that has the same size as the InputStream
    byte[] buffer = new byte[iS.available()];
    // read the text file as a stream, into the buffer
    iS.read(buffer);
    // create a output stream to write the buffer into
    ByteArrayOutputStream oS = new ByteArrayOutputStream();
    // write this buffer to the output stream
    oS.write(buffer);
    // Close the Input and Output streams
    oS.close();
    iS.close();

    // return the output stream as a String
    return oS.toString();
  }
}
