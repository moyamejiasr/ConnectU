package com.onelio.connectu.Helpers;

import android.content.Context;

import com.onelio.connectu.Common;
import com.onelio.connectu.Managers.DatabaseManager;

import java.util.Calendar;

public class UpdaterHelper {

    private static boolean isTheSameYear(long value) {
        Calendar current = Calendar.getInstance();
        Calendar old = Calendar.getInstance();
        old.clear();
        old.setTimeInMillis(value);
        int cyear = current.get(Calendar.YEAR);
        int oyear = old.get(Calendar.YEAR);
        return cyear == oyear;
    }

    private static boolean isTheSameMonth(long value) {
        Calendar current = Calendar.getInstance();
        Calendar old = Calendar.getInstance();
        old.clear();
        old.setTimeInMillis(value);
        int cmonth = current.get(Calendar.MONTH);
        int omonth = old.get(Calendar.MONTH);
        return cmonth == omonth;
    }

    public static boolean isFirstLauncher(long value) {

        //First Launch or New Month/Year
        return value < 1 || !isTheSameYear(value) || !isTheSameMonth(value);

    }

    public static boolean needsSilenceUpdate(long value) { //Silence Update

        /*If one os those get executed no one else will be
        until it reach the next week or month change usually.
        */

        if (value < 1) //Is First Launch ---> Like Really???, Will you launch it some day???
            return true;

        if (!isTheSameYear(value)) //New Year Update
            return true;

        if (!isTheSameMonth(value)) //New Month Update
            return true;

        //Weekly update
        Calendar current = Calendar.getInstance();
        Calendar old = Calendar.getInstance();
        old.clear();
        old.setTimeInMillis(value);
        return current.get(Calendar. WEEK_OF_YEAR) != old.get(Calendar. WEEK_OF_YEAR);

    }

    public static long changeUpdatedDate(Context context) {
        Calendar calendar = Calendar.getInstance();
        DatabaseManager database = new DatabaseManager(context);
        database.putLong(Common.PREFERENCE_LONG_LAST_UP_TIME, calendar.getTimeInMillis());
        return calendar.getTimeInMillis();
    }

}
