package com.onelio.connectu.API;

import android.content.Context;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.Containers.AnuncioData;
import com.onelio.connectu.Managers.ErrorManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AnunciosRequest {

    //Private definitions
    private static String MARCAR_TODOS_LEIDO = "https://cvnet.cpd.ua.es/uaAnuncios/Anuncios/MarcarTodosLeido";
    private static String MARCAR_LEIDO = "https://cvnet.cpd.ua.es/uaAnuncios/Anuncios/MarcarLeido";
    private static String MARCAR_NO_LEIDO = "https://cvnet.cpd.ua.es/uaAnuncios/Anuncios/MarcarNoLeido";
    private static String ANUNCIOS_URL = "https://cvnet.cpd.ua.es/uaanuncios/";
    private static String ANUNCIO_URL = "https://cvnet.cpd.ua.es/uaanuncios/anuncios/anuncio?idanuncio=";
    private static String ANUNCIOS_LIST_BODY = "ul[class=lista]";
    //Private content
    //Session
    private Context context;
    private JSONArray jdata;

    //define callback interface
    public interface AnunciosCallback {

        void onResult(boolean onResult, String message);
    }

    //Initialize session
    public AnunciosRequest(Context context) {
        this.context = context;
        jdata = new JSONArray();
    }

    private void parseAnuncio(Element element, String id) {
        AnuncioData anuncio = new AnuncioData();
        Element el = element.select("div.titulo").first();
        for (Element child : el.children()) {
            if (child.hasClass("badge")) {
                anuncio.setType(child.text());
                break;
            } else {
                anuncio.setType("Error");
            }
        }
        anuncio.setTitle(element.select("span[class=titanuncio]").text());
        anuncio.setText(element.select("div[class=texto]").html());
        anuncio.setSubject(element.select("div[class=asignatura]").text());
        anuncio.setTeacher(element.select("div[class=profesor]").text());
        if (id.length() == 0) {
            anuncio.setNew(element.select("div.noleido").first().child(0).hasClass("oculto"));
            anuncio.setId(element.select("div.noleido").first().child(0).child(0).attr("data-anuncio"));
        } else {
            anuncio.setId(id);
        }
        String str_date = element.select("span[class=fecha]").text();
        SimpleDateFormat formatter = new SimpleDateFormat("(dd/MM/yyyy)");
        try {
            Date date = formatter.parse(str_date);
            anuncio.setDate(date);
        } catch (ParseException e) {
            FirebaseCrash.report(e);
        }
        anuncio.setDate(str_date);
        Gson gson = new Gson();
        try {
            jdata.put(new JSONObject(gson.toJson(anuncio)));
        } catch (JSONException e) {
            FirebaseCrash.report(e);
        }
    }

    public JSONArray getAnuncios() {
        return jdata;
    }

    public JSONObject getAnuncio(int id) {
        try {
            return jdata.getJSONObject(id);
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public void markAllRead(final AnunciosCallback callback) {
        UAWebService.HttpWebPostRequest(context, MARCAR_TODOS_LEIDO, "", new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void markRead(String id, final AnunciosCallback callback) {
        UAWebService.HttpWebPostRequest(context, MARCAR_LEIDO, "idanuncio=" + id, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void markUnread(String id, final AnunciosCallback callback) {
        UAWebService.HttpWebPostRequest(context, MARCAR_NO_LEIDO, "idanuncio=" + id, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void loadAnuncio(final String id, final AnunciosCallback callback) {
        UAWebService.HttpWebGetRequest(context, ANUNCIO_URL + id, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Post data
                    Element anuncio = doc.select(ANUNCIOS_LIST_BODY).first().child(0);
                    parseAnuncio(anuncio, id);
                    callback.onResult(true, id);
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public void loadAnuncios(final AnunciosCallback callback) {
        UAWebService.HttpWebGetRequest(context, ANUNCIOS_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Post data
                    Element anuncios = doc.select(ANUNCIOS_LIST_BODY).first();
                    try {
                        for (Element anuncio : anuncios.children()) {
                            parseAnuncio(anuncio, "");
                        }
                        callback.onResult(true, "");
                    } catch (NullPointerException e) {
                        FirebaseCrash.log(body);
                        FirebaseCrash.report(e);
                        callback.onResult(false, ErrorManager.LOGIN_REJECTED); //Usually because session ended!
                    }
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

}
