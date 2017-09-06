package com.onelio.connectu.Helpers;

import android.content.Context;
import android.util.DisplayMetrics;

import com.onelio.connectu.App;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.Containers.SubjectData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObjectHelper {

    //Calendar code
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

    //Profesores Code
    public static SubjectData getSubjectById(List<SubjectData> subjects, String id) {
        for (SubjectData subject : subjects) {
            if (subject.getId().equals(id)) {
                return subject;
            }
        }
        return null;
    }

    //Evaluacion Code
    public static List<String> getSubjectsName(Context context, int year, boolean withAll) {
        App app = (App)context.getApplicationContext();
        List<String> subjects = new ArrayList<>();
        if (withAll) {
            subjects.add(context.getString(R.string.filter_all_subjects));
        }
        for (SubjectData subject : app.academicYears.get(year).getSubjectsData()) {
            String sname = AppManager.after(subject.getName(), app.academicYears.get(year).getYear() + " ");
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

}
