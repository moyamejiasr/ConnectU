package com.onelio.connectu;

import com.onelio.connectu.Apps.Anuncios.AdList;

import org.json.JSONObject;
import org.jsoup.select.Elements;

/**
 * Created by Onelio on 07/10/2016.
 */
public class Common {

    //Here goes everything related with the App
    //URLs List
    public static String LOGIN_URL = "https://cvnet.cpd.ua.es/uacloud/home/indexVerificado";
    public static String EVALUACION_URL = "https://cvnet.cpd.ua.es/uaevalua";
    public static String EVALUACION_URL_2 = "https://cvnet.cpd.ua.es/uaevalua/miscontroles";
    public static String NOTAS_SIG = "https://cvnet.cpd.ua.es/uaEvalua/misnotas/DashBoardAsignatura/";
    public static String MATERIALES_LOGIN = "https://cvnet.cpd.ua.es/uaMatDocente"; //https://autentica.cpd.ua.es/cas/login?service=https%3a%2f%2fcvnet.cpd.ua.es%2fuamatdocente%2f
    public static String MATERIALES_NEW = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/CursoMaterialesPend";
    public static String MATERIALES_ALL = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/CursoMaterialesTodos";
    public static String MATERIALES_NAV = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/VistaMateriales";
    public static String TUTORIAS_UNSEEN = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/FiltroListadoTutoSinLeerAsig";
    public static String TUTORIAS_SEEN = "https://cvnet.cpd.ua.es/uaTutorias/Emisor/FiltroListadoTutoHechasAsig";
    public static String TUTORIAS_NT = "https://cvnet.cpd.ua.es/uatutorias/Emisor/NuevaTutoria";
    public static String TUTORIAS_NP = "https://cvnet.cpd.ua.es/uatutorias/Emisor/NuevaPregunta";

    //State
    public static boolean isLogged = false;
    public static String loginUsername = "";
    public static String loginPassword = "";
    //Config
    public static boolean isNotifOn = true;
    public static boolean isNotifUOn = true;
    public static JSONObject data;
    public static boolean updateData = false;
    public static boolean firstStart = false;

    //LOGIN - PAGE
    //Content
    public static String lt = "";
    public static String execution = "";
    public static String loginURL = "";

    //Main - PAGE
    public static boolean needMainReload = false;
    public static String alerts = "0";
    public static String name = "Username";
    public static String src = "";
    //Alerts
    public static Elements MATDOCENTE;
    public static Elements SUBMATDOCENTE;
    public static Elements ANUNCIOS;
    public static Elements SUBANUNCIOS;
    public static Elements UATUTORIAS;
    public static Elements SUBUATUTORIAS;
    public static Elements UAEVALUACION;
    public static Elements SUBUAEVALUACION;


    //Apps
    //Anuncios
    public static AdList announce;
    //WebView
    public static String webURL = "";
    public static String webName = "";
    //Tutoria
    public static String cTitle = "DEFAULT TITLE LAYOUT";
    public static boolean isNewChat = false;
    public static String tutId = "";
    public static JSONObject jdata = new JSONObject();
    //Evaluacion
    public static String evid = "";
    //Profesores
    public static JSONObject teacher = new JSONObject();
    //Horario
    public static String siguaID = "";
    public static String siguaName = "";
    public static String siguaHorario = "";

}
