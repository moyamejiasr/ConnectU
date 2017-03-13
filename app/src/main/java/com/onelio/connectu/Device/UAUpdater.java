package com.onelio.connectu.Device;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.widget.ProgressBar;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Common;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Onelio on 02/03/2017.
 */

public class UAUpdater {
    //define callback interface
    public interface UpdaterCallBack {
        void onNavigationComplete(boolean isSuccessful, JSONObject data);
    }

    static JSONObject data = new JSONObject();
    static boolean error = false;
    static boolean completed = false;
    static int progress = 0;
    //Private structures
    static int connected = 0;
    static JSONArray teachers = new JSONArray();

    static Calendar time;
    static int calCount = 0;
    static JSONArray calendar = new JSONArray();

    static NotificationManager mNotifyManager;
    static NotificationCompat.Builder mBuilder;



    static void getNextCal(final Activity activity) {
        //vars
        String url = "";

        switch (calCount) {
            case 0:
                url = UAWebService.CAL_DOC;
                break;
            case 1:
                url = UAWebService.CAL_EVA;
                break;
            case 2:
                url = UAWebService.CAL_EXA;
                break;
            case 3:
                url = UAWebService.CAL_FEST;
                break;
        }

        final int month = time.get(Calendar.MONTH);
        int year = time.get(Calendar.YEAR);

        //Define range
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 1);
        start.set(Calendar.MONTH, month);
        start.set(Calendar.YEAR, year);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.DATE, end.getActualMaximum(Calendar.DATE));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MONTH, month + 1);
        end.set(Calendar.YEAR, year);

        String finalPart = "&start=" + String.valueOf(start.getTimeInMillis()/1000) + "&end=" + String.valueOf(end.getTimeInMillis()/1000);

        UAWebService.HttpWebGetRequest(activity, url + finalPart , new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        JSONArray doc = new JSONArray(body);
                        for(int c = 0; c < doc.length(); c++) {
                            JSONObject event = doc.getJSONObject(c);
                            calendar.put(event);
                        }
                        if (calCount < 3) {
                            calCount++;
                            progress += 10;
                            getNextCal(activity);
                        } else {
                            data.put("calendar", calendar);
                            completed = true;
                        }
                    } catch (JSONException e) {
                        FirebaseCrash.log("Failed in url with" + String.valueOf(calCount));
                        FirebaseCrash.log(e.getMessage().toString());
                        error = true;
                    }
                }
            }
        });

    }

    static void getTeachersData(final Activity activity) {
        try {
            final JSONArray signatures = data.getJSONArray("signatures");
            for (int i = 0; i < signatures.length(); i++) {
                final String date = data.getString("year");
                final String id = signatures.getJSONObject(i).getString("id");
                final String name = signatures.getJSONObject(i).getString("name");

                final String json = "{\"Cod\":\"" + id + "\",\"Curso\":\"" + date + "\"}";
                UAWebService.HttpWebJSONPostRequest(activity, UAWebService.TUTORIAS_G_DES, json, new UAWebService.WebCallBack() {
                    @Override
                    public void onNavigationComplete(boolean isSuccessful, String body) {
                        Document doc = Jsoup.parse(body);
                        //Get Profesor Name
                        Elements elements = doc.select("option");
                        final List<String> ttext = new ArrayList<String>();
                        final List<String> tid = new ArrayList<String>();
                        for (int i = 0; i < elements.size(); i++) {
                            if(elements.eq(i).attr("value").length() > 0) {
                                ttext.add(elements.eq(i).text());
                                tid.add(elements.eq(i).attr("value"));
                            }
                        }
                        UAWebService.HttpWebJSONPostRequest(activity, UAWebService.TUTORIAS_G_SIGN, json, new UAWebService.WebCallBack() {
                            @Override
                            public void onNavigationComplete(boolean isSuccessful, String body) {
                                Document doc = Jsoup.parse(body);
                                //Get Professor Img
                                Elements elements = doc.select("div.well");
                                final List<String> timg = new ArrayList<String>();
                                final List<String> temail = new ArrayList<String>();
                                final List<String> thtml = new ArrayList<String>();
                                for (int c = 0; c < ttext.size(); c++) {
                                    for (int i = 0; i < elements.size(); i++) {
                                        String name = elements.eq(i).select("h4").text();
                                        if (ttext.get(c).contains(name)) {
                                            thtml.add(elements.eq(i).select("ul").html());
                                            temail.add(DeviceManager.before(elements.eq(i).select("p").text(),"ua.es") + "ua.es");
                                            timg.add(elements.eq(i).select("img").attr("src"));
                                        }
                                    }
                                }
                                if (ttext.size() == timg.size()) {
                                    try {
                                        for (int i = 0; i < tid.size(); i++) {
                                            JSONObject jdata = new JSONObject();
                                            jdata.put("id", tid.get(i));
                                            jdata.put("name", ttext.get(i));
                                            jdata.put("img", timg.get(i));
                                            jdata.put("email", temail.get(i));
                                            jdata.put("html", thtml.get(i));
                                            jdata.put("signature_id", id);
                                            jdata.put("signature", name);
                                            jdata.put("date", date);
                                            teachers.put(jdata);
                                        }
                                    } catch (JSONException e) {
                                        error = true;
                                    }
                                }
                                connected++;
                                progress += 5;
                                if (connected == signatures.length()) {
                                    try {
                                        data.put("teachers", teachers);
                                        getNextCal(activity);
                                    } catch (JSONException e) {
                                        FirebaseCrash.log("JSON Exception in Tutorias_G_SIGN 2");
                                        FirebaseCrash.log(e.getMessage());
                                        error = true;
                                    }
                                }
                            }
                        });
                    }
                });
            }
        } catch (JSONException e) {
            FirebaseCrash.log("JSON Exception in Tutorias_G_DES 1");
            FirebaseCrash.log(e.getMessage().toString());
            error = true;
        }
    }
    static void requestSignatures(final Activity activity) {
        UAWebService.HttpWebGetRequest(activity, UAWebService.TUTORIAS_G_MAKE, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Signatures
                    Elements elements = doc.select("select[id=ddlAsignatura] > option");
                    JSONArray signatures = new JSONArray();
                    for (int i = 0; i < elements.size(); i++) {
                        if(elements.eq(i).attr("value").length() > 0) {
                            JSONObject signature = new JSONObject();
                            try {
                                signature.put("name", elements.eq(i).text());
                                signature.put("id", elements.eq(i).attr("value"));
                                signatures.put(signature);
                            } catch (JSONException e) {
                                FirebaseCrash.log("Failed in Tutorias_G_MAKE with");
                                FirebaseCrash.log(e.getMessage().toString());
                                error = true;
                            }
                        }
                    }
                    try {
                        data.put("signatures", signatures);
                    } catch (JSONException e) {
                        FirebaseCrash.log("Failed in Tutorias_G_MAKE with");
                        FirebaseCrash.log(e.getMessage().toString());
                        error = true;
                    }
                    progress = 10;
                    getTeachersData(activity);
                }
            }
        });
    }
    static void requestConn(final Activity activity) {
        UAWebService.HttpWebGetRequest(activity, UAWebService.TUTORIAS, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Material
                    Elements elements = doc.select("select[id=ddlCurso] > option");
                    for (int i = 0; i < elements.size(); i++) {
                        if (elements.eq(i).text().length() > 0) {
                            if (elements.eq(i).hasAttr("selected")) {
                                try {
                                    data.put("year", elements.eq(i).text());
                                } catch (JSONException e) {
                                    error = true;
                                    FirebaseCrash.log("JSON Exception connecting to UA.Tutorias");
                                }
                            }
                        }
                    }
                    progress = 5;
                    requestSignatures(activity);
                } else {
                    FirebaseCrash.log("Failed connecting to UA.Tutorias");
                }
            }
        });
    }

    static public class updateDataResult extends AsyncTask<Void,Integer,Void>{ //change Object to required type
        private UpdaterCallBack listener;
        private Activity activity;

        public updateDataResult(Activity activity, UpdaterCallBack listener){
            this.listener=listener;
            this.activity=activity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Restart
            time = Calendar.getInstance();
            data = new JSONObject();
            error = false;
            completed = false;
            progress = 0;
            connected = 0;
            teachers = new JSONArray();

            calCount = 0;
            calendar = new JSONArray();

            if (Common.updateData) {
                FirebaseCrash.log("UAUpdater - Updating data");
                mNotifyManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
                mBuilder = new NotificationCompat.Builder(activity);
                mBuilder.setContentTitle("ConnectU Manager")
                        .setContentText("Updating user profile...")
                        .setSmallIcon(R.drawable.download);
                mBuilder.setProgress(100, 0, false);
                // Displays the progress bar for the first time.
                mNotifyManager.notify(5, mBuilder.build());
            } else {
                FirebaseCrash.log("UAUpdater - Creating data");
            }

            //Connect
            requestConn(activity);
            int count = 0;
            while (!completed || error) {
                if (progress > count) {
                    count = progress;
                    publishProgress(count);
                }
            }
            return null;
        }

        protected void onPostExecute(Void s){
            // your stuff
            if (Common.updateData && error) {
                mBuilder.setContentText("Error updating user profile!").setProgress(0, 0, false);
                mNotifyManager.notify(5, mBuilder.build());
            } else if (Common.updateData) {
                mNotifyManager.cancel(5);
            }
            FirebaseCrash.log("Finished with error (0=no) " + String.valueOf(!error));
            listener.onNavigationComplete(!error, data);
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!Common.updateData) {
                        ProgressBar pb = (ProgressBar)activity.findViewById(R.id.progressBar);
                        pb.setProgress(values[0]);
                    } else {
                        mBuilder.setProgress(100, values[0], false);
                        mNotifyManager.notify(5, mBuilder.build());
                    }
                }
            });
        }

    }

}
