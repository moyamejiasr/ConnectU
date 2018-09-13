package com.onelio.connectu.API;

import android.content.Context;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.App;
import com.onelio.connectu.Containers.BubbleData;
import com.onelio.connectu.Containers.SubjectData;
import com.onelio.connectu.Containers.TutoriaData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class TutoriasRequest {
    //Private definitions
    private static String LOGIN_TUTORIAS_HECHAS = "https://cvnet.cpd.ua.es/uatutorias/emisor/hechas";
    private static String TUTORIAS_ALL_FETCH_SUBJECT = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/FiltroListadoTutoHechasAsig";
    private static String TUTORIAS_UNSEEN_FETCH_SUBJECT = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/FiltroListadoTutoSinLeerAsig";
    private static String TUTORIAS_TUTORIA = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/Details/";
    private static String TUTORIAS_LIST_BODY = "div[id=bodyDialogo]";
    private static String TUTORIA_MARK_READ = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/MarcarTutoLeida";
    private static String TUTORIA_MARK_UNREAD = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/MarcarTutoNoLeida";
    private static String TUTORIAS_G_MAKE = "https://cvnet.cpd.ua.es/uatutorias/emisor/hacer";
    private static String TUTORIAS_NT = "https://cvnet.cpd.ua.es/uatutorias/Emisor/NuevaTutoria";
    private static String TUTORIAS_NP = "https://cvnet.cpd.ua.es/uatutorias/Emisor/NuevaPregunta";
    private static String TUTORIAS_TEACHER_FIND = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/GetDestinatariosmisPDI";
    //Private content
    //Session
    private Context context;
    private App app;
    private List<BubbleData> bubbles;
    private String idPadre = "";
    private static int year = 0; //It's static to share the same value throw all the instances

    //define callback interface
    public interface TutoriasCallback {
        void onResult(boolean onResult, String message);
    }

    private String fetchJSONBy(String pDisplayCount, String pDisplay, String year, String subject) {
        String displayStart = String.valueOf(Integer.valueOf(pDisplayCount) * Integer.valueOf(pDisplay));
        String requestNumber = "0";
        String result = "sEcho=" + requestNumber +
                "&iColumns=6" +
                "&sColumns=" +
                "&iDisplayStart=" + displayStart +
                "&iDisplayLength=" + pDisplayCount +
                "&mDataProp_0=0" +
                "&mDataProp_1=1" +
                "&mDataProp_2=2" +
                "&mDataProp_3=3" +
                "&mDataProp_4=4" +
                "&mDataProp_5=5" +
                "&sSearch=" +
                "&bRegex=false" +
                "&sSearch_0=" +
                "&bRegex_0=false" +
                "&bSearchable_0=true" +
                "&sSearch_1=" +
                "&bRegex_1=false" +
                "&bSearchable_1=false" +
                "&sSearch_2=" +
                "&bRegex_2=false" +
                "&bSearchable_2=false" +
                "&sSearch_3=" +
                "&bRegex_3=false" +
                "&bSearchable_3=false" +
                "&sSearch_4=" +
                "&bRegex_4=false" +
                "&bSearchable_4=true" +
                "&sSearch_5=" +
                "&bRegex_5=false" +
                "&bSearchable_5=true" +
                "&iSortCol_0=2" +
                "&sSortDir_0=desc" +
                "&iSortingCols=1" +
                "&bSortable_0=true" +
                "&bSortable_1=true" +
                "&bSortable_2=true" +
                "&bSortable_3=true" +
                "&bSortable_4=true" +
                "&bSortable_5=false" +
                "&AssCodnum=" + subject +
                "&AnyCaca=" + year +
                "&sRangeSeparator=~";
        return result;
    }

    private String parseYear() {
        return app.academicYears.get(year).getYear();
    }

    private List<BubbleData> parseBubblesFromBody(String body) {
        List<BubbleData> bubbles = new ArrayList<>();
        Document doc = Jsoup.parse(body);

        // Get Padre
        idPadre = doc.select("input[name=idPadre]").attr("value");
        // Get Rows
        Element chats = doc.select(TUTORIAS_LIST_BODY).first();
        try {
            for (Element chat : chats.children()) {
                BubbleData bubble = new BubbleData();
                if (!chat.attr("id").equals("destino")) {
                    if (!chat.select("div.bubble").isEmpty()) {
                        //For me
                        bubble.setText(chat.select("div.bubble").html());
                        Element author = chat.select("div.autor").first();
                        String date = AppManager.after(author.text(), "</strong> ");
                        Element pdata = author.select("img.imgUsuarioG").first();
                        bubble.setDate(date);
                        bubble.setAuthor(pdata.attr("title"));
                        bubble.setImage(pdata.attr("src"));
                        bubble.setMe(true);
                    } else {
                        //For others
                        bubble.setText(chat.select("div.oddbubble").html());
                        Element author = chat.select("div.autorodd").first();
                        String date = AppManager.after(author.text(), "</strong> ");
                        Element pdata = author.select("img.imgUsuarioG").first();
                        bubble.setDate(date);
                        bubble.setAuthor(pdata.attr("title"));
                        bubble.setImage(pdata.attr("src"));
                        bubble.setMe(false);
                    }
                    bubbles.add(bubble);
                }
            }
        } catch (NullPointerException e) {
            FirebaseCrash.log("Failed loading chats from chat");
            FirebaseCrash.report(new Exception(body));
        }
        return bubbles;
    }

    public List<TutoriaData> parseTutorias(boolean isNew, String text, SubjectData subject) {
        List<TutoriaData> tutorias = new ArrayList<>();
        try {
            JSONObject jdata = new JSONObject(text);
            if (jdata.has("aaData")) {
                JSONArray jarray = jdata.getJSONArray("aaData");
                for (int i = 0; i < jarray.length(); i++) {
                    TutoriaData tutoria = new TutoriaData();
                    JSONArray jtutoria = jarray.getJSONArray(i);
                    String name;
                    String state;
                    String html;
                    String id;
                    if (isNew) {
                        name = jtutoria.getString(0);
                        state = "";
                        html = jtutoria.getString(3);
                        id = jtutoria.getString(4);
                    } else {
                        name = jtutoria.getString(0);
                        state = jtutoria.getString(3);
                        if (state.contains("pendiente")) {
                            state = context.getString(R.string.tutoria_without_answer);
                        }
                        html = jtutoria.getString(4);
                        id = jtutoria.getString(5);
                    }
                    Document docname = Jsoup.parse(name);
                    name = docname.select("span").first().text().replace("\n", "");
                    Document doc = Jsoup.parse(html);
                    Elements elements = doc.select("img");
                    String user = elements.attr("title");
                    String src = elements.attr("src");

                    String sname = AppManager.after(subject.getName(), parseYear() + " ");
                    sname = AppManager.before(sname, "(" + subject.getId());

                    tutoria.setSubject(subject.getId());
                    tutoria.setSubjectName(sname);
                    tutoria.setTitle(name);
                    tutoria.setId(id);
                    tutoria.setLastModify(state);
                    tutoria.setTeacherName(user);
                    tutoria.setTeacherPicture(src);
                    tutorias.add(tutoria);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tutorias;
    }

    public TutoriasRequest(Context context) {
        this.context = context;
        app = (App) context.getApplicationContext();
    }

    public void loginService(final TutoriasCallback callback) {
        String post = "oVariable=SelAnyAcaAlu&pFiltroCombo=S&oSel=" + parseYear();
        UAWebService.HttpWebPostRequest(context, LOGIN_TUTORIAS_HECHAS, post, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void setYear(int date) {
        year = date;
    }

    public int getYear() {
        return year;
    }

    public List<BubbleData> getTutoriaChats() {
        return bubbles;
    }

    public void fetchAllTutoriasFrom(String subject, final TutoriasCallback callback) {
        String json = fetchJSONBy("5", "0", parseYear(), subject);
        UAWebService.HttpWebPostRequest(context, TUTORIAS_ALL_FETCH_SUBJECT, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void fetchPendingTutoriasFrom(String subject, final TutoriasCallback callback) {
        String json = fetchJSONBy("5", "0", parseYear(), subject);
        UAWebService.HttpWebPostRequest(context, TUTORIAS_UNSEEN_FETCH_SUBJECT, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void fetchTutoriaById(String id, final TutoriasCallback callback) {
        UAWebService.HttpWebGetRequest(context, TUTORIAS_TUTORIA + id, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    bubbles = parseBubblesFromBody(body);
                }
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void teacherFind(String subjectId, final String teacherName, final TutoriasCallback callback) {
        String json = "{\"Cod\":\"" + subjectId + "\",\"Curso\":\"" + parseYear() + "\"}";
        UAWebService.HttpWebJSONPostRequest(context, TUTORIAS_TEACHER_FIND, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Post data
                    Element element = doc.select("select[id=ddlDestinatario]").first();
                    boolean found = false;
                    if (element.children() == null) {
                        callback.onResult(false, ErrorManager.TEACHER_ID_NOT_FOUND);
                        return;
                    }
                    for (Element teacher : element.children()) {
                        if (teacher.text().equals(teacherName)) {
                            found = true;
                            callback.onResult(true, teacher.attr("value"));
                        }
                    }
                    if (!found)
                        callback.onResult(false, ErrorManager.TEACHER_ID_NOT_FOUND);
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public void requestNewTutoria(final String subjectId, final String teacherName, final TutoriasCallback callback) {
        UAWebService.HttpWebGetRequest(context, TUTORIAS_G_MAKE, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(final boolean isSuccessful, String body) {
                if (isSuccessful) {
                    teacherFind(subjectId, teacherName, new TutoriasCallback() {
                        @Override
                        public void onResult(boolean onResult, String message) {
                            callback.onResult(onResult, message);
                        }
                    });
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public void createTutoria(String subjectId, String destinationId, String title, String text, final TutoriasCallback callback) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("ddlCurso", parseYear())
                .addFormDataPart("ddlAsignatura", subjectId)
                .addFormDataPart("ddlDestinatario", destinationId)
                .addFormDataPart("ckDestinatarios", "1")
                .addFormDataPart("ckDestinatarios", "false")
                .addFormDataPart("txtAsunto", title)
                .addFormDataPart("txtPregunta", text)
                .build();
        UAWebService.HttpWebMultiPartPostRequest(context, TUTORIAS_NT, requestBody, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        JSONObject jdata = new JSONObject(body);
                        callback.onResult(true, jdata.getString("result"));
                    } catch (JSONException e) {
                        callback.onResult(false, ErrorManager.BAD_RESPONSE);
                    }
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public void answerTutoria(String tutId, String text, final TutoriasCallback callback) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("idTuto", tutId)
                .addFormDataPart("idPadre", idPadre)
                .addFormDataPart("TextBoxPregunta", text)
                .addFormDataPart("inputFileArch", "")
                .build();
        UAWebService.HttpWebMultiPartPostRequest(context, TUTORIAS_NP, requestBody, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        JSONObject jdata = new JSONObject(body);
                        callback.onResult(true, jdata.getString("result"));
                    } catch (JSONException e) {
                        callback.onResult(false, ErrorManager.BAD_RESPONSE);
                    }
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public void markTutoriaReadId(String id, final TutoriasCallback callback) {
        String jdata = "idTuto=" + id;
        UAWebService.HttpWebPostRequest(context, TUTORIA_MARK_READ, jdata, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        JSONObject jdata = new JSONObject(body);
                        callback.onResult(true, jdata.getString("result"));
                    } catch (JSONException e) {
                        callback.onResult(false, context.getString(R.string.error_casting_action));
                    }
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public void markTutoriaUnreadId(String id, final TutoriasCallback callback) {
        String jdata = "idTuto=" + id;
        UAWebService.HttpWebPostRequest(context, TUTORIA_MARK_UNREAD, jdata, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        JSONObject jdata = new JSONObject(body);
                        callback.onResult(true, jdata.getString("result"));
                    } catch (JSONException e) {
                        callback.onResult(false, context.getString(R.string.error_casting_action));
                    }
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

}
