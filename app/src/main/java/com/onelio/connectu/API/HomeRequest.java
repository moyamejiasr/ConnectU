package com.onelio.connectu.API;

import android.content.Context;

import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.onelio.connectu.API.Networking.UAWebService;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.NotificationData;
import com.onelio.connectu.Managers.DatabaseManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class HomeRequest {

    //Private definitions
    private static String MAIN_URL = "https://cvnet.cpd.ua.es/uacloud/home/indexVerificado";
    //Private content
    //Session
    private Context context;
    private static App app;

    //Alerts
    private static JSONObject content;
    private static JSONArray newcontent;

    //define callback interface
    public interface HomeCallback {

        void onHomeResult(boolean onSuccess, String message);
    }

    public HomeRequest(Context context){
        app = (App) context.getApplicationContext();
        this.context = context;
    }

    private static void putNotificationInJ(NotificationData data) throws JSONException {
        Gson gson = new Gson();
        JSONObject jstring = new JSONObject(gson.toJson(data));
        //Content has type??
        JSONObject tcontent = new JSONObject(content.toString()); //Prevent ConcurrentModificationException by adding a temp obj
        if (!tcontent.has(data.getType())) {
            tcontent.put(data.getType(), new JSONObject());
            tcontent.getJSONObject(data.getType()).put("notifications", new JSONArray());
        }
        //Old notifications has this type?? or has this notification??
        if (!app.notifications.has(data.getType()) || !app.notifications.getJSONObject(data.getType()).toString().contains(jstring.toString())) {
            //If not, set it as new because it's not in the old section
            newcontent.put(jstring);
        }
        tcontent.getJSONObject(data.getType()).getJSONArray("notifications").put(jstring);
        content = tcontent;
    }

    public void parseAlertsFromBody(String body){
        FirebaseCrash.log("XX-Parsing Alerts");
        Document doc = Jsoup.parse(body);
        Element notifications;

        //Set content to 0
        content = new JSONObject();
        newcontent = new JSONArray();
        int count = 0;

        try {
            notifications = doc.select("div[id=contenedorNotificaciones]").get(0);
            notifications = notifications.select("div[id=accordion]").get(0);
        }catch(IndexOutOfBoundsException e) { //Jumps when no notifications found
            saveData(count); //Save that we have 0
            return;
        }

        //Get All Notifications
        for (Element object: notifications.children()) {
            String type = object.attr("data-id");
            Element list = object.select("ul[class=list-group]").get(0);
            for (Element notification: list.children()) {
                NotificationData ndata = new NotificationData();
                try {
                    ndata.setType(type);
                    ndata.setId(notification.attr("data-id"));
                    ndata.setUrl(notification.child(0).attr("href"));
                    ndata.setDate(notification.select("span[class=fechaNotificacion]").text());
                    ndata.setTitle(notification.select("span[class=titulo]").text());
                    ndata.setText(notification.select("span[class=textoNotificacion]").text());
                    ndata.setCount(notification.select("span[class=numeroNotificacion]").text());
                } catch(NullPointerException e) {
                    //Error getting data
                    ndata.setType("ERROR");
                    FirebaseCrash.report(e);
                }
                try {
                    putNotificationInJ(ndata);
                    count++;
                } catch (JSONException e) {
                    //Error setting data
                    FirebaseCrash.report(e);
                }
            }
        }

        //Finally save everything
        saveData(count);
    }

    private void saveData(int count) {
        //Put count
        try {
            content.put("count", count);
        } catch (JSONException e) {
            //Error setting count
            FirebaseCrash.report(e);
        }
        //Save to file
        DatabaseManager database = new DatabaseManager(context);
        String result = content.toString();
        if (result == null) {
            result = "";
        }
        database.putString(Common.PREFERENCE_JSON_NOTIFICATIONS, result);
        //Load to main
        app.notifications = content;
        app.newNotifications = newcontent;
        FirebaseCrash.log("XX-Alerts parsed with " + newcontent.length() + " news");
    }

    //Reload inside
    public void getAlerts(final HomeCallback callback) {
        UAWebService.HttpWebGetRequest(context, MAIN_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    parseAlertsFromBody(body);
                    callback.onHomeResult(true, "");
                } else {
                    callback.onHomeResult(false, body);
                }
            }
        });
    }
}
