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
    public static String ANUNCION_URL = "https://cvnet.cpd.ua.es/uaanuncios/";
    public static String ANUNCIO_L = "https://cvnet.cpd.ua.es/uaAnuncios/Anuncios/MarcarLeido";
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
    public static String CAL_FEST = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=festivos";
    public static String CAL_DOC = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=docenciaalu";
    public static String CAL_EVA = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=evaluaalu";
    public static String CAL_EXA = "https://cvnet.cpd.ua.es/uaHorarios/Home/ObtenerEventosCalendarioJson?calendario=examenesalu";

    //State
    public static boolean isLogged = false;
    public static String loginUsername = "";
    public static String loginPassword = "";
    //Config
    public static boolean isNotifOn = true;
    public static JSONObject data;
    public static boolean updateData = false;
    public static boolean firstStart = false;

    //LOGIN - PAGE
    //Content
    public static String lt = "";
    public static String execution = "";
    public static String loginURL = "";

    //Main - PAGE
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

}
