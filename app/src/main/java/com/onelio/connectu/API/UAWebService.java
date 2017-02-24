package com.onelio.connectu.API;

import android.app.Activity;

import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UAWebService {

    //Here goes all UA URLs
    public static String LOGIN_URL = "https://cvnet.cpd.ua.es/uacloud/home/indexVerificado";
    public static String LOGIN_DOMAIN = "https://autentica.cpd.ua.es";
    public static String ANUNCION_URL = "https://cvnet.cpd.ua.es/uaanuncios/";
    public static String ANUNCIO_L = "https://cvnet.cpd.ua.es/uaAnuncios/Anuncios/MarcarLeido";
    public static String ANUNCIO_NL = "https://cvnet.cpd.ua.es/uaAnuncios/Anuncios/MarcarNoLeido";
    public static String EVALUACION_URL = "https://cvnet.cpd.ua.es/uaevalua";
    public static String EVALUACION_URL_2 = "https://cvnet.cpd.ua.es/uaevalua/miscontroles";
    public static String EVALUACION_URL_3 = "https://cvnet.cpd.ua.es/uaEvalua/miscontroles/dtControles?";
    public static String EVAULIACION_VIEW = "https://cvnet.cpd.ua.es/uaEvalua/misControles/Detalle/";
    public static String NOTAS = "https://cvnet.cpd.ua.es/uaevalua/misnotas";
    public static String NOTAS_SIG = "https://cvnet.cpd.ua.es/uaEvalua/misnotas/DashBoardAsignatura/";
    public static String MATERIALES_LOGIN = "https://cvnet.cpd.ua.es/uaMatDocente"; //https://autentica.cpd.ua.es/cas/login?service=https%3a%2f%2fcvnet.cpd.ua.es%2fuamatdocente%2f
    public static String MATERIALES_NEW = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/CursoMaterialesPend";
    public static String MATERIALES_ALL = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/CursoMaterialesTodos";
    public static String MATERIALES_NAV = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/VistaMateriales";
    public static String TUTORIAS = "https://cvnet.cpd.ua.es/uatutorias/";
    public static String TUTORIAS_ALL = "https://cvnet.cpd.ua.es/uatutorias/emisor/hechas";
    public static String TUTORIAS_UNSEEN = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/FiltroListadoTutoSinLeerAsig";
    public static String TUTORIAS_SEEN = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/FiltroListadoTutoHechasAsig";
    public static String TUTORIAS_VIEW = "https://cvnet.cpd.ua.es/uaTutorias/emisor/Details/";
    public static String TUTORIAS_READ = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/MarcarTutoLeida";
    public static String TUTORIAS_G_MAKE = "https://cvnet.cpd.ua.es/uatutorias/emisor/hacer";
    public static String TUTORIAS_G_SIGN = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/getHTAsignaturaAlumno";
    public static String TUTORIAS_G_DES = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/GetDestinatariosmisPDI";
    public static String TUTORIAS_NT = "https://cvnet.cpd.ua.es/uatutorias/Emisor/NuevaTutoria";
    public static String TUTORIAS_NP = "https://cvnet.cpd.ua.es/uatutorias/Emisor/NuevaPregunta";
    public static String TUTORIAS_PRESENCIALES = "https://cvnet.cpd.ua.es/uatutorias/emisor/presenciales";
    public static String CAL_FEST = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=festivos";
    public static String CAL_DOC = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=docenciaalu";
    public static String CAL_EVA = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=evaluaalu";
    public static String CAL_EXA = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=examenesalu";
    public static String EXP_LOAD = "https://cvnet.cpd.ua.es/uaexpeaca/";

    //define callback interface
    public interface WebCallBack {

        void onNavigationComplete(boolean isSuccessful, String body);
    }

    //Get Method
    public static void HttpWebGetRequest(final Activity activity, String stringUrl, final WebCallBack callback) {
        try {
            WebApi.get(stringUrl, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //Couldn't connect with UACloud
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertManager manager = new AlertManager(activity);
                            manager.setMessage(activity.getResources().getString(R.string.error_defTitle), activity.getResources().getString(R.string.error_connect));
                            manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                @Override
                                public void onClick(boolean isPositive) {
                                    DeviceManager.appClose();
                                }
                            });
                            manager.show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callback.onNavigationComplete(response.isSuccessful(), response.body().string());
                    response.close();
                }
            });
        } catch (IOException e) {
            AlertManager manager = new AlertManager(activity);
            manager.setMessage(activity.getResources().getString(R.string.error_defTitle), e.getMessage());
            manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    DeviceManager.appClose();
                }
            });
            manager.show();
        }
    }

    //Post Method
    public static void HttpWebPostRequest(final Activity activity, String stringUrl, String json, final WebCallBack callback) {
        try {
            WebApi.post(stringUrl, json, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //Couldn't connect with UACloud
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertManager manager = new AlertManager(activity);
                            manager.setMessage(activity.getResources().getString(R.string.error_defTitle), activity.getResources().getString(R.string.error_connect));
                            manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                @Override
                                public void onClick(boolean isPositive) {
                                    DeviceManager.appClose();
                                }
                            });
                            manager.show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callback.onNavigationComplete(response.isSuccessful(), response.body().string());
                    response.close();
                }
            });
        } catch (IOException e) {
            AlertManager manager = new AlertManager(activity);
            manager.setMessage(activity.getResources().getString(R.string.error_defTitle), e.getMessage());
            manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    DeviceManager.appClose();
                }
            });
            manager.show();
        }
    }

    //JSONPost Method
    public static void HttpWebJSONPostRequest(final Activity activity, String stringUrl, String json, final WebCallBack callback) {
        try {
            WebApi.jpost(stringUrl, json, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //Couldn't connect with UACloud
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertManager manager = new AlertManager(activity);
                            manager.setMessage(activity.getResources().getString(R.string.error_defTitle), activity.getResources().getString(R.string.error_connect));
                            manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                @Override
                                public void onClick(boolean isPositive) {
                                    DeviceManager.appClose();
                                }
                            });
                            manager.show();
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    callback.onNavigationComplete(response.isSuccessful(), response.body().string());
                    response.close();
                }
            });
        } catch (IOException e) {
            AlertManager manager = new AlertManager(activity);
            manager.setMessage(activity.getResources().getString(R.string.error_defTitle), e.getMessage());
            manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    DeviceManager.appClose();
                }
            });
            manager.show();
        }
    }

}
