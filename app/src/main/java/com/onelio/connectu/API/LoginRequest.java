package com.onelio.connectu.API;

import android.content.Context;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.DatabaseManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginRequest {

    //Private definitions
    private static String LOGIN_DOMAIN = "https://autentica.cpd.ua.es/cas/login?service=https%3a%2f%2fcvnet.cpd.ua.es%2fuacloud%2fhome%2findexVerificado";
    //Private content
    //Session
    private Context context;
    private App app;
    //Error
    private int error;
    private String err_message;

    //define callback interface
    public interface LoginCallback {

        void onLoginResult(boolean onResult, String message);
    }

    //Initialize session
    public LoginRequest(Context context) {
        app = (App) context.getApplicationContext();
        this.context = context;
        error = 0;
        err_message = "Error, Alert not added";
    }

    private String getJData(String loginUsername, String loginPassword) {
        String json = "fail";
        try {
            json = "username=" + URLEncoder.encode(loginUsername, "UTF-8") +
                    "&password=" + loginPassword +
                    "&execution=" + app.account.getExecution() +
                    "&_eventId=" + app.account.getEvent() + "&geolocation=";
        } catch (Exception e) {
            FirebaseCrash.report(e);
        }
        return json;
    }

    private boolean loginConfirmState(Document doc, String user) {
        //Get User Data
        error = 0;
        err_message = "Error, Alert not added";
        String name = "";
        try {
            name = doc.select("span[id=nombre]").first().text();
            error = doc.select("div.contenido-caja-texto > div.row").get(0).children().size(); //Get Count of alerts
            err_message = doc.select("div.contenido-caja-texto > div.row").get(0).children().text(); //Get text inside
        } catch (Exception ex) {}//In case of 0, prevent exception

        if (error > 0) { // If 0 we have no alerts, Which usually means login success
            return false;
        } else {
            //Confirm that name is not invalid
            if (!name.equals("Usuario no validado")) {
                return true; // Name is valid, continue login
            } else {
                //User still not match but error not found, that means that the error is in the method so let's report it
                FirebaseCrash.report(new Exception(user + " " + doc.text()));
                err_message = context.getString(R.string.error_login_failed);
                return false;
            }
        }
    }

    private void getSessionFromBody(Document doc) throws NullPointerException { //Throws added to handle in case of server fail on response
        //Get Post data
        Element exe = doc.select("input[name=execution]").first();
        if (exe != null) {
            app.account.setExecution(exe.attr("value"));
        }
        Element eve = doc.select("input[name=_eventId]").first();
        if (eve != null) {
            app.account.setEvent(eve.attr("value"));
        }
    }

    private void saveLoginData() {
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        DatabaseManager database = new DatabaseManager(context);
        database.putString(Common.PREFERENCE_STRING_LASTLOGINVERSION, AppManager.getAppVersion(context));
        database.putString(Common.PREFERENCE_STRING_LASTLOGINDATE, currentDateTimeString);
    }

    //Get Session data from login page
    public void createSession(final LoginCallback callback){
        UAWebService.HttpWebGetRequest(context, LOGIN_DOMAIN, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                try {
                    if (isSuccessful) {
                        Document doc = Jsoup.parse(body);
                        getSessionFromBody(doc);
                        FirebaseCrash.log("Session created!");
                        callback.onLoginResult(true, "");
                    } else {
                        callback.onLoginResult(false, body);
                    }
                } catch(NullPointerException e) {
                    callback.onLoginResult(false, ErrorManager.EMPTY_RESPONSE);
                }
            }
        });
    }

    //Login account in
    public void loginAccount(final String user, final String pass, final LoginCallback callback) { //Change bool for callback
        UAWebService.HttpWebPostRequest(context, LOGIN_DOMAIN, getJData(user, pass), new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                try {
                    if (isSuccessful) {
                        Document doc = Jsoup.parse(body);
                        boolean loginSuccess = loginConfirmState(doc, user);
                        if (loginSuccess) {
                            FirebaseCrash.log("Loggin success");
                            saveLoginData(); //Save date & version of actual login
                            app.account.setEmail(user);
                            app.account.setPassword(pass);
                            app.account.setName(doc.select("a.dropdown-toggle > span[id=nombre]").text());
                            app.account.setPictureURL(doc.select("a.dropdown-toggle > span[id=retrato] > img").attr("src"));
                            HomeRequest notificationsLoader = new HomeRequest(context);
                            notificationsLoader.parseAlertsFromBody(body);
                        } else {
                            getSessionFromBody(doc);
                        }
                        app.account.setLogged(loginSuccess);
                        callback.onLoginResult(loginSuccess, err_message);
                    } else {
                        callback.onLoginResult(false, body);
                    }
                } catch(NullPointerException e) {
                    callback.onLoginResult(false, ErrorManager.EMPTY_RESPONSE);
                }
            }
        });
    }
}
