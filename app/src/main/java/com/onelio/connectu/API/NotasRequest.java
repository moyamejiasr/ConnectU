package com.onelio.connectu.API;

import android.content.Context;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.App;
import com.onelio.connectu.Containers.EvaluacionData;
import com.onelio.connectu.Containers.NotaData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.ErrorManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotasRequest {

    //Private definitions
    private static String NOTAS_URL = "https://cvnet.cpd.ua.es/uaevalua/misnotas";
    private static String NOTAS_FETCH_URL = "https://cvnet.cpd.ua.es/uaEvalua/misnotas/DashBoardAsignatura/";
    //Private content
    //Session
    private Context context;
    private App app;
    private static int year = 0; //It's static to share the same value throw all the instances
    private static int subject = 0;

    //Results
    private List<NotaData> grades;

    //define callback interface
    public interface NotasCallback {
        void onResult(boolean onResult, String message);
    }

    public NotasRequest(Context context){
        this.context = context;
        app = (App) context.getApplicationContext();
    }

    private String parseYear() {
        return app.academicYears.get(year).getYear();
    }

    public void setYear(int date) {
        year = date;
    }

    public int getYear() {
        return year;
    }

    private String parseSubject() {
        if (subject != 0) {
            return app.academicYears.get(year).getSubjectsData().get(subject - 1).getId();
        } else {
            return "0"; //Means all
        }
    }

    public void setSubject(int data) {
        subject = data;
    }

    public int getSubject() {
        return subject;
    }

    public void loginService(final NotasCallback callback) {
        //2-Step login (Why UACloud?, Why in the world...)
        //First Login Step
        UAWebService.HttpWebGetRequest(context, NOTAS_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    callback.onResult(true, "");
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public List<NotaData> getGrades() {
        return grades;
    }

    public void fetchGrades(final NotasCallback callback) {
        grades = new ArrayList<>();
        String json = "asignatura=" + parseSubject() + "&caca=" + parseYear();
        UAWebService.HttpWebPostRequest(context, NOTAS_FETCH_URL, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        JSONArray jdata = new JSONObject(body).getJSONArray("notas");
                        for( int i = 0; i < jdata.length(); i++) {
                            grades.add(parseObject(jdata.getJSONObject(i)));
                        }
                        callback.onResult(true, "");
                    }catch (JSONException e) {
                        callback.onResult(false, ErrorManager.UNKNOWN_RESPONSE_FORMAT);
                    }
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    private NotaData parseObject(JSONObject jobject) {
        if (jobject == null) {
            return null;
        }
        NotaData event = new NotaData();
        try {
            long date = Long.valueOf(AppManager.before(AppManager.after(jobject.getString("FECHA"), "("), ")"));
            event.setDate(new Date(date));
            event.setType(jobject.getString("TIPO"));
            event.setTypeId(jobject.getInt("IDTIPO"));
            event.setTitle(jobject.getString("TITULO"));
            event.setDescription(jobject.getString("DESCRIPCION"));
            event.setObservations(jobject.getString("OBSERVACIONES"));
            event.setNota(jobject.getDouble("NOTANUM"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

}
