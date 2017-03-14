package com.onelio.connectu.BackgroundService;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.onelio.connectu.Common;
import com.onelio.connectu.Database.Settings;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.LauncherActivity;
import com.onelio.connectu.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Onelio on 02/02/2017.
 */

public class UAService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static ClearableCookieJar cookieJar;
    public static OkHttpClient client;

    public static Call get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
    public static Call post(String url, String json, Callback callback) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }
    String loginUsername;
    String loginPassword;
    int timeSleep = 900000; // 30 minutos -> 1800000 // 1 hora - >3600000 // Actually 15 min
    int curAlert = 0;
    public static boolean active = true;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    public int counter=0;
    public UAService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public UAService() {
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            do {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                if (hour > 7 && hour < 23) { //No molestar antes de las 8 o despues de las 10
                    addManagerInstance();
                }
                try {
                    Thread.sleep(timeSleep);
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
            } while(active);
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            //stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        //This service will run until we stop it
        //startService(new Intent(getBaseContext(), MyServices.class));
        //stopService

        //Get Data
        //Create Realm
        Log.w("UAService", "Started!");
        Realm.init(getBaseContext());
        RealmConfiguration config = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(config);
        Realm realm = Realm.getDefaultInstance();
        Settings username = realm.where(Settings.class).equalTo("name", "username").findFirst();
        Settings data = realm.where(Settings.class).equalTo("name", "NotiCount").findFirst();
        String isOn = realm.where(Settings.class).equalTo("name", "isNotifOn").findFirst().getValue();
        if (username != null && isOn.contains("yes")) {
            loginUsername = username.getValue();
            if (!loginUsername.contains("Guest")) {
                curAlert = Integer.parseInt(data.getValue());
                Settings pass = realm.where(Settings.class).equalTo("name", "pass").findFirst();
                loginPassword = pass.getValue();
                //Start Work
                Message msg = mServiceHandler.obtainMessage();
                msg.arg1 = startId;
                mServiceHandler.sendMessage(msg);
            }
            realm.close();
        } else {
            realm.close();
            active = false;
            stopSelf();
        }
        realm.close();
        return START_STICKY;
    }

    public void addManagerInstance() {
        //Build Okhttp cookieJar and client
        cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getBaseContext()));
        client = new OkHttpClient().newBuilder()
                .cookieJar(cookieJar)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        //Get Session
        try {
            get(Common.LOGIN_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Document doc = Jsoup.parse(response.body().string());
                        //Get Post data
                        Element lurl = doc.select("form[id=fm1]").first();
                        String loginURL = lurl.attr("action");
                        Element lt = doc.select("input[name=lt]").first();
                        String ltr = lt.attr("value");
                        Element execution = doc.select("input[name=execution]").first();
                        String executionr = execution.attr("value");
                        startMainActivity(loginURL, ltr, executionr);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMainActivity(String loginURL, String lt, String execution) {
        try {
            String json = "username=" + URLEncoder.encode(loginUsername, "UTF-8") +
                    "&password=" + loginPassword +
                    "&lt=" + lt +
                    "&execution=" + execution +
                    "&_eventId=submit&submit=";
            try {
                post("https://autentica.cpd.ua.es" + loginURL, json, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            Document doc = Jsoup.parse(response.body().string());
                            //Get User Data
                            Elements name = doc.select("a.dropdown-toggle > span[id=nombre]");
                            if (!name.text().contains("Usuario no validado")) {
                                Elements alertas = doc.select("a.dropdown-toggle > span.numeroAlertas");
                                int data = Integer.parseInt(alertas.text());
                                if (data > curAlert) {
                                    Intent notificationIntent = new Intent(getBaseContext(), LauncherActivity.class);
                                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    PendingIntent intent = PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, 0);
                                    NotificationCompat.Builder mBuilder =
                                            new NotificationCompat.Builder(getBaseContext())
                                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                                    .setSmallIcon(R.drawable.advw)
                                                    .setContentIntent(intent)
                                                    .setContentTitle(getString(R.string.app_name) + " Alert")
                                                    .setContentText(getResources().getString(R.string.you_have) + " " + String.valueOf(data - curAlert) + " " + getResources().getString(R.string.unseen_not))
                                                    .setAutoCancel(true);
                                    NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    mNotifyMgr.notify(1, mBuilder.build());
                                }
                                if (curAlert != data) {
                                    Realm realm = Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    RealmQuery<Settings> query = realm.where(Settings.class);
                                    query.equalTo("name", "NotiCount");
                                    Settings result1 = query.findFirst();
                                    result1.setValue(String.valueOf(data));
                                    realm.copyToRealmOrUpdate(result1);
                                    realm.commitTransaction();
                                    curAlert = data;
                                }
                            }
                        }
                        response.close();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onDestroy() {
        Log.i("EXIT", "ondestroy!");
        if(active) {
            Intent broadcastIntent = new Intent(getBaseContext(), UAServiceKill.class);
            sendBroadcast(broadcastIntent);
        }
        super.onDestroy();
    }
}
