package com.onelio.connectu.API;

import android.content.Context;

import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.Containers.MaterialData;
import com.onelio.connectu.Managers.ErrorManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MaterialesRequest {

    //Private definitions
    private static String LOGIN_OUT_INDICATOR = "uamatdocente/Conexion/LogOffCas";
    private static String LOGIN_OUT = "https://cvnet.cpd.ua.es/uamatdocente/Conexion/LogOffCas";
    private static String LOGIN_MATERIALES = "https://cvnet.cpd.ua.es/uamatdocente/";
    private static String MATERIALES_FIRST_DATE_CHANGE = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/AsignaturasMateriales";
    private static String MATERIALES_DATE_CHANGE = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/CambiarCursoAlu";
    private static String MATERIALES_UNSEEN = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/AsignaturasMateriales";
    private static String MATERIALES_ALL = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/CursoMaterialesTodos";
    private static String MATERIALES_LIST_BODY = "div.tabla > div.fila";
    //Private content
    //Session
    private Context context;
    private List<MaterialData> files;

    //define callback interface
    public interface MaterialsCallback {
        void onResult(boolean onResult, String message);
    }

    public MaterialesRequest(Context context) {
        this.context = context;
    }

    public void loginService(final MaterialsCallback callback) {
        UAWebService.HttpWebGetRequest(context, LOGIN_MATERIALES, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void logoutService(final MaterialsCallback callback) {
        UAWebService.HttpWebGetRequest(context, LOGIN_OUT, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public boolean isSessionTerminated(String body) {
        return body.contains(LOGIN_OUT_INDICATOR);
    }


    private static String ryear = ""; //To allow reload on the same value when re-login

    public void changeDate(String year, boolean isFirst, final MaterialsCallback callback) {
        String url;
        if (isFirst) {
            url = MATERIALES_FIRST_DATE_CHANGE;
        } else {
            url = MATERIALES_DATE_CHANGE;
        }
        if (!year.isEmpty()) {
            ryear = year;
        }
        UAWebService.HttpWebPostRequest(context, url, "caca=" + ryear, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                callback.onResult(isSuccessful, body);
            }
        });
    }

    private MaterialData parseElement(Element element) {
        MaterialData data = new MaterialData();
        data.setId(element.select("div.columna1").text());
        data.setSubjectId(element.attr("data-codasi"));
        if (element.hasClass("carpeta")) {
            data.setFolder(true);
            if (!element.hasClass("filacarpvacia")) {
                data.setAvailableFolder(true);
            }
        } else {
            data.setType(element.select("div.columna2").text());
            String typeName = element.select("div.columna4 > span.imagentipo > span.glyphs").attr("title");
            data.setTypeName(typeName);
        }
        Element name = element.select("div.columna5aux").first();
        if (name != null) {
            data.setFileName(name.text());
        } else {
            data.setFileName(element.select("div.columna5").text());
        }
        data.setFileDescription(element.select("div.columna6").text());
        data.setSubjectName(element.select("div.columna15").text());
        data.setDate(element.select("div.columna13").text());
        //Apartado Publisher
        Element publisher = element.select("div.columna12 > span.publicador > img.imgcreador").first();
        data.setPublisherName(publisher.attr("title"));
        data.setPublisherPicture(publisher.attr("src"));
        return data;
    }

    private void parseBody(Elements body) {
        files = new ArrayList<>();
        for (Element element : body) {
            files.add(parseElement(element));
        }
    }

    public List<MaterialData> getMateriales() {
        return files;
    }

    public void loadPendingMateriales(final MaterialsCallback callback) {
        String json = "idmat=-1&codasi=-1&expresion=&direccion=&filtro=&pendientes=S";
        UAWebService.HttpWebPostRequest(context, MATERIALES_UNSEEN, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Post data
                    Elements mensajes = doc.select(MATERIALES_LIST_BODY);
                    parseBody(mensajes);
                    callback.onResult(true, "");
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

    public void loadMaterialesLoc(List<String> location, final MaterialsCallback callback) {
        String idmat = "-1";
        String codasi = "-1";
        if (location.size() == 1) {
            codasi = location.get(0);
        } else if (location.size() > 1) {
            codasi = location.get(0);
            idmat = location.get(location.size()-1);
        }
        String json = "idmat=" + idmat + "&codasi=" + codasi + "&expresion=&direccion=&filtro=";
        UAWebService.HttpWebPostRequest(context, MATERIALES_ALL, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Post data
                    Elements mensajes = doc.select(MATERIALES_LIST_BODY);
                    parseBody(mensajes);
                }
                callback.onResult(isSuccessful, body);
            }
        });
    }

    public void restoreSession(final MaterialsCallback callback) { //Re-login when session ended
        //Re-login
        loginService(new MaterialsCallback() {
            @Override
            public void onResult(boolean onResult, String message) {
                //Set last date
                if (onResult) {
                    changeDate("", false, new MaterialsCallback() {
                        @Override
                        public void onResult(boolean onResult, String message) {
                            //Continue to app service
                            if (onResult) {
                                callback.onResult(true, "");
                            } else {
                                callback.onResult(false, message);
                            }
                        }
                    });
                } else {
                    callback.onResult(false, message);
                }
            }
        });

    }

    public void downloadFile(MaterialData data, final File file, final MaterialsCallback callback) {
        String url = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/File?idMat=" + data.getId() + "&tipoorigen=" + data.getType();
        UAWebService.HttpFileDownloadRequest(context, url, file, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    if (file.exists()) {
                        callback.onResult(true, "");
                    } else {
                        callback.onResult(false, ErrorManager.FILE_DONT_EXIST);
                    }
                } else {
                    callback.onResult(false, body);
                }
            }
        });
    }

}
