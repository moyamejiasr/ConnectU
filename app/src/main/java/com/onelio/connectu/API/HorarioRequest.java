package com.onelio.connectu.API;

import android.content.Context;
import android.util.Log;

import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.Managers.DatabaseManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HorarioRequest {

    //Private definitions
    private static String DATES_URL = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=";
    private static String SIGUA_URL = "https://www.sigua.ua.es/api/pub/estancia/";
    //Public
    public static String CALENDAR_DOCENCIA = "docenciaalu";
    public static String CALENDAR_EVALUACION = "evaluaalu";
    public static String CALENDAR_EXAMENES = "examenesalu";
    public static String CAlENDAR_FESTIVOS = "festivos";
    //Private content
    //Session
    private Context context;
    private App app;

    //Content
    private JSONObject horario;

    //define callback interface
    public interface HorarioCallback {
        void onCompleted(boolean onResult, String message);
    }

    public HorarioRequest(Context context) {
        app = (App) context.getApplicationContext();
        this.context = context;
        horario = new JSONObject();
    }

    public void saveFullHorario() { //Only do when all 4 horarios has been downloaded
        DatabaseManager database = new DatabaseManager(context);
        database.putString(Common.PREFERENCE_JSON_HORARIO, horario.toString());
        app.horario = horario;
    }

    public void loadHorario (long start, long stop, final String type, final HorarioCallback callback) {
        String url = DATES_URL + type + "&start=" + String.valueOf(start) + "&end=" + String.valueOf(stop);
        UAWebService.HttpWebGetRequest(context, url, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        horario.put(type, new JSONArray(body));
                        callback.onCompleted(true, "");
                    } catch (JSONException e) {
                        callback.onCompleted(false, e.getMessage());
                    }
                } else {
                    callback.onCompleted(false, body);
                }
            }
        });
    }

    private boolean happensHere(Date start, Date current, Date end) {
        if (start.getYear() <= current.getYear() && start.getMonth() <= current.getMonth() && start.getDate() <= current.getDate()) {
            if (current.getYear() <= end.getYear() && current.getMonth() <= end.getMonth() && current.getDate() <= end.getDate()) {
                return true;
            }
        }
        return false;
    }

    //See if a event happens before another and get his index
    private int isNewerThan(CalendarEvent newevent, List<CalendarEvent> events, Date date) {
        int newDif = (int)(newevent.getStart().getTime() - date.getTime());
        for (int i = 0; i < events.size(); i++) {
            CalendarEvent event = events.get(i);
            int eventDif = (int)(event.getStart().getTime() - date.getTime());
            if (newDif <= eventDif) {
                return i;
            }
        }
        return -1;
    }

    private List<CalendarEvent> getEvents(Date date, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<CalendarEvent> events = new ArrayList<>();
        try {
            JSONArray array = app.horario.getJSONArray(type);
            for (int i = 0; i < array.length(); i++) {
                CalendarEvent event = new CalendarEvent();
                JSONObject jdata = array.getJSONObject(i);
                String sstart = jdata.getString("start").replace("T", " ");
                Date start = sdf.parse(sstart);
                String send = jdata.getString("end").replace("T", " ");
                Date end = sdf.parse(send);

                event.setStart(start);
                event.setEnd(end);

                if(happensHere(start, date, end)) {
                    event.setType(type);
                    event.setTitle(jdata.getString("uaAssNomass"));
                    event.setText(jdata.getString("uaGacDesgac"));
                    event.setLoc(jdata.getString("uaIdAula"));
                    event.setSigua(jdata.getString("uaIdSigua"));
                    event.setFullData(jdata.toString());

                    try {
                        event.setAllDay(jdata.getBoolean("allDay"));
                    }catch(JSONException e) {
                        e.printStackTrace();
                    }

                    if(type.equals(CALENDAR_DOCENCIA)) {
                        event.setSubtitle(jdata.getString("uaCacDescac"));
                    }
                    if(type.equals(CALENDAR_EVALUACION)) {
                        event.setSubtitle(jdata.getString("uaEvaluaTipo") + ": " + jdata.getString("title"));
                    }
                    if(type.equals(CALENDAR_EXAMENES)) {
                        event.setTitle(jdata.getString("uaAssNomass"));
                        event.setSubtitle(jdata.getString("title"));
                        event.setText(context.getString(R.string.view_horario_official_test));
                        event.setLoc(context.getString(R.string.view_horario_lotofplaces));
                    }
                    if(type.equals(CAlENDAR_FESTIVOS)) {
                        event.setTitle(jdata.getString("title"));
                        event.setSubtitle(context.getString(R.string.view_horario_holiday));
                        event.setText(context.getString(R.string.view_horario_not_class));
                        event.setLoc("");
                    }

                    int index = isNewerThan(event, events, date);
                    if (index > -1) {
                        events.add(index, event);
                    } else {
                        events.add(event);
                    }
                }
            }
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }
        return events;
    }

    public List<CalendarEvent> getDateEvent(Date date, String filter) {
        List<CalendarEvent> result = new ArrayList<>();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        //Festivo
        if (filter.contains(Common.HORARIO_FILTER_FESTIVO)) {
            List<CalendarEvent> festivo = getEvents(date, CAlENDAR_FESTIVOS);
            for (CalendarEvent event : festivo) {
                result.add(event);
            }
        }
        //Examenes
        if (filter.contains(Common.HORARIO_FILTER_EXAMENES)) {
            List<CalendarEvent> examenes = getEvents(date, CALENDAR_EXAMENES);
            for (CalendarEvent event : examenes) {
                result.add(event);
            }
        }
        //Evaluacion
        if (filter.contains(Common.HORARIO_FILTER_EVALUACION)) {
            List<CalendarEvent> eva = getEvents(date, CALENDAR_EVALUACION);
            for (CalendarEvent event : eva) {
                result.add(event);
            }
        }
        //Docencia
        if (filter.contains(Common.HORARIO_FILTER_DOCENCIA)) {
            List<CalendarEvent> docencia = getEvents(date, CALENDAR_DOCENCIA);
            for (CalendarEvent event : docencia) {
                result.add(event);
            }
        }
        return result;
    }

    public List<CalendarEvent> getDateEvents(Date date) {
        List<CalendarEvent> result = new ArrayList<>();
        date.setHours(0);
        date.setMinutes(0);
        date.setSeconds(0);

        //Festivo
        if (app.getPublicPreferenceB(Common.HORARIO_FILTER_FESTIVO)) {
            List<CalendarEvent> festivo = getEvents(date, CAlENDAR_FESTIVOS);
            for (CalendarEvent event : festivo) {
                result.add(event);
            }
        }
        //Examenes
        if (app.getPublicPreferenceB(Common.HORARIO_FILTER_EXAMENES)) {
            List<CalendarEvent> examenes = getEvents(date, CALENDAR_EXAMENES);
            for (CalendarEvent event : examenes) {
                result.add(event);
            }
        }
        //Evaluacion
        if (app.getPublicPreferenceB(Common.HORARIO_FILTER_EVALUACION)) {
            List<CalendarEvent> eva = getEvents(date, CALENDAR_EVALUACION);
            for (CalendarEvent event : eva) {
                result.add(event);
            }
        }
        //Docencia
        if (app.getPublicPreferenceB(Common.HORARIO_FILTER_DOCENCIA)) {
            List<CalendarEvent> docencia = getEvents(date, CALENDAR_DOCENCIA);
            for (CalendarEvent event : docencia) {
                result.add(event);
            }
        }
        return result;
    }

    public void getSIGUA(CalendarEvent event, final HorarioCallback callback) {
        String url = SIGUA_URL + event.getSigua();
        UAWebService.HttpWebGetRequest(context, url, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onCompleted(isSuccessful, body);
            }
        });
    }

}
