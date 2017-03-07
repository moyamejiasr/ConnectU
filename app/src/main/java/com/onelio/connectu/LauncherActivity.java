package com.onelio.connectu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.API.WebApi;
import com.onelio.connectu.Database.RealmManager;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.Device.UAUpdater;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Visual content
        setContentView(R.layout.activity_launcher);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        TextView tversion = (TextView)findViewById(R.id.tversion);
        tversion.setText(getResources().getString(R.string.app_name) + " v" + DeviceManager.getAppVersion(getBaseContext()));

        //Build Okhttp
        WebApi.initialize(getBaseContext());

        //If for all statements
        getSession();


    }

    public void getSession() {
        //Get Session data from login page
        UAWebService.HttpWebGetRequest(LauncherActivity.this, UAWebService.LOGIN_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Post data
                    Element lurl = doc.select("form[id=fm1]").first();
                    Common.loginURL = lurl.attr("action");
                    Element lt = doc.select("input[name=lt]").first();
                    Common.lt = lt.attr("value");
                    Element execution = doc.select("input[name=execution]").first();
                    Common.execution = execution.attr("value");
                    startMainActivity();
                }
            }
        });
    }

    public void startMainActivity() {

        //Create Realm database instance
        RealmManager realm = new RealmManager(getBaseContext());

        if (realm.onOptionExist("isNotifOn")) {
            if (realm.getOption("isNotifOn").contains("yes")) {
                Common.isNotifOn = true;
            } else {
                Common.isNotifOn = false;
            }
        } else {
            Common.isNotifOn = true;
            realm.createOption("isNotifOn", "yes"); //This option will allow notifications
            realm.createOption("NotiCount", "0"); //This option is a notification count
        }

        if (realm.onOptionExist("username")) {
            Common.loginUsername = realm.getOption("username");
            if (!Common.loginUsername.contains("Guest")) {
                //Process to login and then main
                Common.loginPassword = realm.getOption("pass");
                loginAccount();
            } else {
                //Database exist but user is not logged in
                Intent intent = new Intent(getApplication(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        } else {
            //No database exist, create it and login
            realm.createOption("username", "Guest"); //Login username
            realm.createOption("pass", "none"); //Login password
            realm.deleteRealmInstance();
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

    }

    String getJData() {
        String json = "fail";
        try {
            json = "username=" + URLEncoder.encode(Common.loginUsername, "UTF-8") +
                    "&password=" + Common.loginPassword +
                    "&lt=" + Common.lt +
                    "&execution=" + Common.execution +
                    "&_eventId=submit&submit=";
        } catch (UnsupportedEncodingException e) {
            AlertManager manager = new AlertManager(LauncherActivity.this);
            manager.setMessage(getResources().getString(R.string.error_defTitle), getResources().getString(R.string.error_format));
            manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                        DeviceManager.appClose();
                }
            });
            manager.show();
        }
        return json;
    }

    public void loginAccount() {
        //Get Session data from login page
        UAWebService.HttpWebPostRequest(LauncherActivity.this, UAWebService.LOGIN_DOMAIN + Common.loginURL, getJData(), new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get User Data
                    int error = 0;
                    String err_message = "Error, Alert not added";
                    try {
                        error = doc.select("div.contenido-caja-texto > div.row").get(0).children().size(); //Get Count of alerts
                        err_message = doc.select("div.contenido-caja-texto > div.row").get(0).children().text(); //Get text inside
                    } catch(Exception ex){}; //In case of 0, prevent exception
                    Elements name = doc.select("a.dropdown-toggle > span[id=nombre]");
                    if (error == 0) { //We have no alerts
                        Elements alertas = doc.select("a.dropdown-toggle > span.numeroAlertas");
                        Elements foto = doc.select("a.dropdown-toggle > span[id=retrato] > img");
                        Common.ANUNCIOS = doc.select("a[target=ANUNCIOS] > span.titulo");
                        Common.SUBANUNCIOS = doc.select("a[target=ANUNCIOS] > span.textoNotificacion");
                        Common.MATDOCENTE = doc.select("a[target=MATDOCENTE] > span.textoNotificacion");
                        Common.SUBMATDOCENTE = doc.select("a[target=MATDOCENTE] > span.numeroNotificacion");
                        Common.UATUTORIAS = doc.select("a[target=TUTORIAS] > span.titulo");
                        Common.SUBUATUTORIAS = doc.select("a[target=TUTORIAS] > span.textoNotificacion");
                        Common.UAEVALUACION = doc.select("a[target=UAEVALUACION] > span.titulo");
                        Common.SUBUAEVALUACION = doc.select("a[target=UAEVALUACION] > span.textoNotificacion");
                        Common.alerts = alertas.text();
                        Common.name = name.text();
                        Common.src = foto.attr("src");
                        Common.isLogged = true;
                        LauncherActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                executeEnviorement();
                            }
                        });
                    } else {
                        AlertManager manager = new AlertManager(LauncherActivity.this);
                        manager.setMessage(getResources().getString(R.string.error_defTitle), err_message);
                        manager.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                            @Override
                            public void onClick(boolean isPositive) {
                            }
                        });
                        manager.show();
                        Intent intent = new Intent(getApplication(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void executeEnviorement() {
        Calendar time = Calendar.getInstance();
        int week = time.get(Calendar.WEEK_OF_MONTH);
        int month = time.get(Calendar.MONTH) + 1;
        int year = time.get(Calendar.YEAR);
        final RealmManager realm = new RealmManager(LauncherActivity.this);
        if (realm.onOptionExist("launchTimes")) {
            try {
                JSONObject jdate = new JSONObject(realm.getOption("launchTimes"));
                if (year != jdate.getInt("year") || month != jdate.getInt("month") || week != jdate.getInt("week")) {
                    //Action
                    Common.updateData = true;
                }
            } catch (JSONException e) {
                Common.updateData = true;
            }
            try {
                Common.data = new JSONObject(realm.getOption("userData"));
                realm.deleteRealmInstance();
                Intent intent = new Intent(getApplication(), HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                AlertManager alert = new AlertManager(LauncherActivity.this);
                alert.setIcon(R.mipmap.ic_launcher);
                alert.setMessage(getString(R.string.error_defTitle), getString(R.string.error_old));
                alert.setPositiveButton("Ok", new AlertManager.AlertCallBack() {
                    @Override
                    public void onClick(boolean isPositive) {
                        DeviceManager.appClose();
                    }
                });
                alert.show();
            }
        } else {
            JSONObject jdate = new JSONObject();
            try {
                jdate.put("week", week);
                jdate.put("month", month);
                jdate.put("year", year);
                realm.createOption("launchTimes", jdate.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TextView beta = (TextView)findViewById(R.id.beta);
            beta.setText(getString(R.string.beta1));
            ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
            pb.setIndeterminate(false);
            new UAUpdater.updateDataResult(LauncherActivity.this, new UAUpdater.UpdaterCallBack() {
                @Override
                public void onNavigationComplete(boolean isSuccessful, JSONObject data) {
                    if (isSuccessful && data.toString().length() > 0) {
                        realm.createOption("userData", data.toString());
                        realm.deleteRealmInstance();
                        Common.data = data;
                        Common.firstStart = true;
                        Intent intent = new Intent(getApplication(), HomePage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        FirebaseCrash.log("Launcher Activity - Failed Creating User profile!");
                        FirebaseCrash.report(new Exception("Failed to profile"));
                        realm.deleteRealmInstance();
                        AlertManager alert = new AlertManager(LauncherActivity.this);
                        alert.setIcon(R.mipmap.ic_launcher);
                        alert.setMessage(getString(R.string.error_defTitle), getString(R.string.error_profile));
                        alert.setPositiveButton("Ok", new AlertManager.AlertCallBack() {
                            @Override
                            public void onClick(boolean isPositive) {
                                Intent intent = new Intent(getApplication(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                }
            }).execute();
        }
    }
}
