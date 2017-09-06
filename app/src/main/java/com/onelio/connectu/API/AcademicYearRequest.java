package com.onelio.connectu.API;

import android.content.Context;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Containers.SubjectData;
import com.onelio.connectu.Containers.TeacherData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.DatabaseManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AcademicYearRequest {

    //Private definitions
    private static String DATES_URL = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/HorariosPresenciales";
    private static String SUBJECTS_URL = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/GetAsignaturasXaHorario";
    private static String TEACHERS_ALU_URL = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/getHTAsignaturaAlumno";
    private static String TEACHERS_URL = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/getHTAsignatura";
    //Private content
    //Session
    private Context context;
    private App app;

    //Content
    public List<AcademicYear> academicYears;

    //define callback interface
    public interface UserDataCallback {

        void onCompleted(boolean onResult, String message);
    }

    public AcademicYearRequest(Context context) {
        app = (App) context.getApplicationContext();
        this.context = context;
        academicYears = new ArrayList<>();
    }

    public void saveAcademicYear() {
        DatabaseManager database = new DatabaseManager(context);
        Gson gson = new Gson();
        database.putString(Common.PREFERENCE_JSON_ACADEMIC_YEAR, gson.toJson(academicYears));
        app.academicYears = academicYears;
    }

    //Get Teachers allowed
    public void loadTeachersByYearAndSubject(final String year, final String sid, boolean all, final UserDataCallback callback){
        String json = "{\"Cod\":" + sid + ",\"Curso\":\"" + year + "\"}";
        String url = TEACHERS_ALU_URL;
        if (all)
            url = TEACHERS_URL;
        UAWebService.HttpWebJSONPostRequest(context, url, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    List<TeacherData> teachers = new ArrayList<>();
                    //Get data
                    Elements selects = doc.select("div.well");
                    for (Element eteacher : selects) {
                        TeacherData teacher = new TeacherData();
                        Element image = eteacher.select("img.img-rounded").first();
                        Element name = eteacher.select("h4").first();
                        String email = AppManager.before(eteacher.select("p").text(),"ua.es") + "ua.es";
                        Element description = eteacher.select("ul").first();
                        teacher.setPicture(image.attr("src"));
                        teacher.setSubject(sid);
                        teacher.setYear(year);
                        teacher.setName(name.text());
                        teacher.setEmail(email);
                        if (description != null)
                            teacher.setDescription(description.html());
                        teachers.add(teacher);
                    }
                    int yLoc = getYearLoc(year);
                    academicYears.get(yLoc).getSubjectsData().get(getSubjectLoc(yLoc, sid)).setTeachers(teachers);
                    FirebaseCrash.log("Teachers loaded with sice of " + teachers.size());
                    callback.onCompleted(true, "");
                } else {
                    callback.onCompleted(false, body);
                }
            }
        });
    }

    public int getSubjectLoc(int yearLoc, String id) {
        for (int i = 0; i < academicYears.get(yearLoc).getSubjectsData().size(); i++) {
            if (academicYears.get(yearLoc).getSubjectsData().get(i).getId().equals(id))
                return i;
        }
        return 0;
    }

    //Get Subjects allowed
    public void loadSubjectsByYear(final String year, final UserDataCallback callback){
        String json = "{\"curso\":\"" + year + "\"}";
        UAWebService.HttpWebJSONPostRequest(context, SUBJECTS_URL, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    List<SubjectData> subjects = new ArrayList<SubjectData>();
                    //Get data
                    Elements selects = doc.select("div.panel-primary");
                    for (Element sigGroup : selects) {
                        SubjectData subject = new SubjectData();
                        Element name = sigGroup.select("div.panel-heading").first();
                        Element id = sigGroup.select("div.panel-body").first();
                        subject.setId(id.attr("data-cod"));
                        subject.setName(name.text().replace("&nbsp", " "));
                        subjects.add(subject);
                    }
                    academicYears.get(getYearLoc(year)).setSubjectsData(subjects);
                    FirebaseCrash.log("Subjects loaded with sice of " + subjects.size());
                    callback.onCompleted(true, "");
                } else {
                    callback.onCompleted(false, body);
                }
            }
        });
    }

    public int getYearLoc(String year) {
        for (int i = 0; i < academicYears.size(); i++) {
            if (academicYears.get(i).getYear().equals(year))
                return i;
        }
        return 0;
    }

    //Get Years allowed
    public void loadYearsList(final UserDataCallback callback){
        UAWebService.HttpWebGetRequest(context, DATES_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    academicYears = new ArrayList<>();
                    //Get data
                    Element selects = doc.select("select[id=ddlCurso]").first();
                    try {
                        for (Element year : selects.children()) {
                            if (year.attr("value").length() != 0) {
                                AcademicYear ayear = new AcademicYear();
                                ayear.setYear(year.attr("value"));
                                if (year.hasAttr("selected")) {
                                    ayear.setSelected(true);
                                }
                                academicYears.add(ayear);
                            }
                        }
                        FirebaseCrash.log("Years loaded with sice of " + academicYears.size());
                        callback.onCompleted(true, "");
                    } catch (NullPointerException e) {
                        FirebaseCrash.log("Error updating years nullpointer with data:" + body);
                        FirebaseCrash.report(e);
                        callback.onCompleted(false, body);
                    }
                } else {
                    callback.onCompleted(false, body);
                }
            }
        });
    }

}
