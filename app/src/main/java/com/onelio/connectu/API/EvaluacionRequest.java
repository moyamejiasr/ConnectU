package com.onelio.connectu.API;

import android.content.Context;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.App;
import com.onelio.connectu.Containers.EvaluacionData;
import com.onelio.connectu.Managers.ErrorManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EvaluacionRequest {

    //Public Types
    private static String[] FILTER_TYPE_V = {"T", "A", "P"};

    //Private definitions
    private static String EVALUACION_URL = "https://cvnet.cpd.ua.es/uaevalua";
    private static String EVALUACION_URL_2 = "https://cvnet.cpd.ua.es/uaevalua/miscontroles";
    private static String EVALUACION_URL_3 = "https://cvnet.cpd.ua.es/uaEvalua/miscontroles/dtControles?";
    private static String EVALUACION_VIEW = "https://cvnet.cpd.ua.es/uaEvalua/misControles/Detalle/";
    //Private content
    //Session
    private Context context;
    private App app;
    private static int year = 0; //It's static to share the same value throw all the instances
    private static int subject = 0;
    private static int filter = 0; //By default we always start with P

    //Login objects
    private static String oVariable;
    private static String pIdOpc;
    private static String pFiltroCombo;
    private static String pCodprs;

    //Results
    private List<EvaluacionData> events;

    //define callback interface
    public interface EvaluacionCallback {
        void onResult(boolean onResult, String message);
    }

    public EvaluacionRequest(Context context){
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

    private String parseFilter() {
        return FILTER_TYPE_V[filter];
    }

    public void setFilter(int data) {
        filter = data;
    }

    public int getFilter() {
        return filter;
    }

    private EvaluacionData parseObject(JSONArray jobject) {
        EvaluacionData event = new EvaluacionData();
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try {
                Date start = formatter.parse(jobject.getString(9));
                event.setStart(start);
                Date end = formatter.parse(jobject.getString(10));
                event.setEnd(end);
            } catch (ParseException e) {
                FirebaseCrash.report(e);
            }
            event.setType(jobject.getString(1));
            event.setName(jobject.getString(2) + " " + jobject.getString(5));
            event.setLoc(jobject.getString(3));
            event.setSubjectId(jobject.getString(4));
            event.setText(jobject.getString(6));
            event.setId(jobject.getString(8));
            event.setTypeID(jobject.getString(11));
            event.setSubject(jobject.getString(13));
            event.setOpen(!jobject.getString(14).equals("N"));
            event.setCompleted(!jobject.getString(15).equals("N"));
            event.setNote(jobject.getString(16));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return event;
    }

    public void loginService(final EvaluacionCallback callback) {
        //2-Step login (Why UACloud?, Why in the world...)
        //First Login Step
        UAWebService.HttpWebGetRequest(context, EVALUACION_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    oVariable = doc.select("input[name=oVariable]").attr("value");
                    pIdOpc = doc.select("input[name=pIdOpc]").attr("value");
                    pFiltroCombo = doc.select("input[name=pFiltroCombo]").attr("value");
                    //Second login Step
                    String json = "oVariable=" + oVariable + "&pIdOpc=" + pIdOpc +"&pFiltroCombo=" + pFiltroCombo + "&oSel=" + getYear();
                    UAWebService.HttpWebPostRequest(context, EVALUACION_URL_2, json, new UAWebService.WebCallBack() {
                        @Override
                        public void onNavigationComplete(boolean isSuccessful, String body) {
                            if (isSuccessful) {
                                //Get Code
                                Document doc = Jsoup.parse(body);
                                pCodprs = doc.select("input[name=pCodprs]").attr("value");
                                callback.onResult(true, "");
                            } else {
                                callback.onResult(false, body);
                            }
                        }
                    });
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public List<EvaluacionData> getEvents() {
        return events;
    }

    public void getFullDataFrom(final EvaluacionData data, final EvaluacionCallback callback) {
        String url = EVALUACION_VIEW + data.getId() + "?caca=" + parseYear() + "&codasi=" + data.getSubjectId() + "&tipo=" + data.getTypeID();
        UAWebService.HttpWebGetRequest(context, url, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    Element table = doc.select("table.table").first();
                    if (table != null) {
                        callback.onResult(true, table.toString());
                    } else {
                        callback.onResult(false, ErrorManager.UNABLE_DISPLAY);
                    }
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public void fetchEvents(final EvaluacionCallback callback) {
        events = new ArrayList<>();
        String url = EVALUACION_URL_3 + "caca=" + parseYear() + "&codper=" + pCodprs + "&codasi=" + parseSubject() + "&ver=" + parseFilter() + "&tipo=";
        String json = "iDisplayLength=-1";
        UAWebService.HttpWebPostRequest(context, url, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        JSONArray jdata = new JSONObject(body).getJSONArray("aaData");
                        for (int i = 0; i < jdata.length(); i++) {
                            events.add(parseObject(jdata.getJSONArray(i)));
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

}
