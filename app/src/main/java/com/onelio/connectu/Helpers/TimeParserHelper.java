package com.onelio.connectu.Helpers;

import android.content.Context;

import com.onelio.connectu.R;

import java.util.Calendar;
import java.util.Date;

public class TimeParserHelper {

    public static String parseTime(int time) {
        String result = String.valueOf(time);
        if (result.length() == 1)
            result = "0" + result;
        return result;
    }

    public static String getDifference(Context context, Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        if (different < 1)
            return "";

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        String result = "";

        if (elapsedDays > 0) {
            result = result + String.valueOf(elapsedDays) + " " + context.getString(R.string.view_horario_days) + " ";
        }

        if (elapsedHours > 0) {
            result = result + String.valueOf(elapsedHours) + " " + context.getString(R.string.view_horario_hours) + " ";
        }

        if (elapsedMinutes > 0) {
            result = result + String.valueOf(elapsedMinutes) + " " + context.getString(R.string.view_horario_minutes) + " ";
        }

        return result;
    }

    public static String parseTime(Context context, String date) {
        return parseTimeDate(context, new Date(date));
    }

    public static String parseTimeDate(Context context, Date adate) {
        String ret;

        if (adate != null) {
            Calendar current = Calendar.getInstance();
            Calendar cal = Calendar.getInstance();
            cal.setTime(adate);
            if (cal.get(Calendar.YEAR) == current.get(Calendar.YEAR)) {
                if (cal.get(Calendar.MONTH) == current.get(Calendar.MONTH)) {
                    if (cal.get(Calendar.DAY_OF_MONTH) == current.get(Calendar.DAY_OF_MONTH)) {
                        if (cal.get(Calendar.HOUR_OF_DAY) == current.get(Calendar.HOUR_OF_DAY)) {
                            ret = context.getString(R.string.date_time_prev) + " " + context.getString(R.string.date_time_moment);
                        } else {
                            String result = String.valueOf(current.get(Calendar.HOUR_OF_DAY) - cal.get(Calendar.HOUR_OF_DAY));
                            ret = context.getString(R.string.date_time_prev) + " " + result + " " + context.getString(R.string.date_time_hours);
                        }
                    } else {
                        String result = String.valueOf(current.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH));
                        ret = context.getString(R.string.date_time_prev) + " " + result + " " + context.getString(R.string.date_time_days);
                    }
                } else {
                    String result = String.valueOf(current.get(Calendar.MONTH) - cal.get(Calendar.MONTH));
                    ret = context.getString(R.string.date_time_prev) + " " + result + " " + context.getString(R.string.date_time_months);
                }
            } else {
                ret = "";
            }
        } else {
            ret = "";
        }
        return ret;
    }

}
