package com.onelio.connectu;

public class Common {

    /**
    Project developed by Onelio
     This project has been developed by Onlio and don't offer any warranty of any type.
     If you want to copy this software please refer to this github repository as the original
     in your app
    **/

    //Format: OBJECTUSE_DATATYPE_DATANAME

    //AccountManager
    public static String PREFERENCE_BOOLEAN_ISLOGGED = "isLogged";
    public static String PREFERENCE_STRING_EMAIL = "userEmail";
    public static String PREFERENCE_STRING_PASSWORD = "userPassword";
    public static String PREFERENCE_STRING_LASTLOGINVERSION = "lastLoginVersion";
    public static String PREFERENCE_STRING_LASTLOGINDATE = "lastLoginDate";
    public static String PREFERENCE_JSON_NOTIFICATIONS = "userNotifications";
    public static String PREFERENCE_INT_RECTIME = "notiTime";
    public static String PREFERENCE_LONG_LAST_UP_TIME = "lastUpdateTime";
    public static String PREFERENCE_JSON_ACADEMIC_YEAR = "academicYear";
    public static String PREFERENCE_JSON_HORARIO = "uaHorario";
    public static String PREFERENCE_JSON_PUBLICPREF = "publicPreferences";

    //Notifications
    public static String GROUP_KEY_ALERTS = "group_key_alerts";
    public static int INT_REC_TIME = 900000; //15 min

    //UAUpdater Service
    public static String INTENT_KEY_UPDATE_TYPE = "updateType";
    public static String INTENT_KEY_RESULT = "result";
    public static String INTENT_KEY_LOC =  "yloc";
    public static String INTENT_KEY_ERROR =  "ierror";

    //UAUpdate Type
    public static String UAUPDATE_TYPE_NORMAL = "uanormal";
    public static String UAUPDATE_TYPE_SILENCE = "uasilence";

    //Home
    public static String HOME_EXTRA_TYPE = "type";

    //WebView Extras
    public static String WEBVIEW_EXTRA_NAME = "activityName";
    public static String WEBVIEW_EXTRA_URL = "activityUrl";
    public static String WEBVIEW_EXTRA_COLOR = "activityColor";
    public static String WEBVIEW_EXTRA_NLOGIN = "activityNeedsLogin";

    //Horario Filter Type
    public static String HORARIO_FILTER_DOCENCIA = "docencia";
    public static String HORARIO_FILTER_EVALUACION = "evaluación";
    public static String HORARIO_FILTER_EXAMENES = "examenes";
    public static String HORARIO_FILTER_FESTIVO = "festivos";

    //Materiales
    public static String MATERIALES_TEMP_FILE = "temp";

    //Tutorias
    public static String TUTORIAS_STRING_ID = "id";
    public static String TUTORIAS_STRING_TITLE = "title";
    public static String TUTORIAS_STRING_AUTHOR = "publisher";
    public static String TUTORIAS_STRING_AUTHOR_IMG = "publisherImg";
    public static String TUTORIAS_STRING_YEAR = "year";
    public static String TUTORIAS_STRING_SUBJECTID = "subjectID";
    public static String TUTORIAS_BOOL_ISHOME = "isHome";


    //Global Filter Type
    public static String GLOBAL_FILTER_YEAR = "year";
    public static String GLOBAL_SETTING_ISNOTIFON = "notiOn";
    public static String GLOBAL_SETTING_NOTIFICATIONDISPLAY = "notiDisplay";

}
