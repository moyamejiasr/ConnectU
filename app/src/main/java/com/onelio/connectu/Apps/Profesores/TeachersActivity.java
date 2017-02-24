package com.onelio.connectu.Apps.Profesores;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Common;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TeachersActivity extends AppCompatActivity {

    ProgressDialog dialog;
    String date;
    //Signatures
    JSONArray teachers = new JSONArray();
    List<String> sname = new ArrayList<>();
    List<String> sid = new ArrayList<>();
    TeachersAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //TODO ADD TO ALL

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        requestConn();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;

    }

    int connected = 0;

    public void showGrid() {
        GridView gridView = (GridView) findViewById(R.id.gridview);
        Adapter = new TeachersAdapter(getBaseContext(), teachers);
        gridView.setAdapter(Adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.teacher = teachers.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                View imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(TeachersActivity.this, TeachersViewActivity.class);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Pair<View, String> pair1 = Pair.create(imageView, getString(R.string.activity_image_trans));
                    Pair<View, String> pair2 = Pair.create(imageView, getString(R.string.activity_text_trans));
                    Pair<View, String> pair3 = Pair.create(imageView, getString(R.string.activity_mixed_trans));
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(TeachersActivity.this, pair1, pair2, pair3);
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        });
    }

    public void getAllData() {
        TeachersActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage("Solicitando datos..");
                dialog.show();
            }
        });
        for (int i = 0; i < sid.size(); i++) {
            final String json = "{\"Cod\":\"" + sid.get(i) + "\",\"Curso\":\"" + date + "\"}";
            final int avalue = i; //For final uses inside external callbacks
            UAWebService.HttpWebJSONPostRequest(TeachersActivity.this, UAWebService.TUTORIAS_G_DES, json, new UAWebService.WebCallBack() {
                @Override
                public void onNavigationComplete(boolean isSuccessful, String body) {
                    Document doc = Jsoup.parse(body);
                    //Get Profesor Name
                    Elements elements = doc.select("option");
                    final List<String> ttext = new ArrayList<String>();
                    final List<String> tid = new ArrayList<String>();
                    for (int i = 0; i < elements.size(); i++) {
                        if(elements.eq(i).attr("value").length() > 0) {
                            ttext.add(DeviceManager.capFirstLetter(elements.eq(i).text()));
                            tid.add(elements.eq(i).attr("value"));
                        }
                    }
                    UAWebService.HttpWebJSONPostRequest(TeachersActivity.this, UAWebService.TUTORIAS_G_SIGN, json, new UAWebService.WebCallBack() {
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
                                    String name = DeviceManager.capFirstLetter(elements.eq(i).select("h4").text());
                                    if (ttext.get(c).contains(name)) {
                                        thtml.add(elements.eq(i).select("ul").html());
                                        temail.add(DeviceManager.before(elements.eq(i).select("p").text(),"ua.es") + "ua.es");
                                        timg.add(elements.eq(i).select("img").attr("src"));
                                    }
                                }
                            }
                            if (ttext.size() == timg.size()) {
                                TeachersActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int i = 0; i < tid.size(); i++) {
                                            JSONObject jdata = new JSONObject();
                                            try {
                                                jdata.put("id", tid.get(i));
                                                jdata.put("name", ttext.get(i));
                                                jdata.put("img", timg.get(i));
                                                jdata.put("email", temail.get(i));
                                                jdata.put("html", thtml.get(i));
                                                jdata.put("signature_id", sid.get(avalue));
                                                jdata.put("signature", sname.get(avalue));
                                                jdata.put("date", date);
                                                teachers.put(jdata);
                                            } catch (JSONException e) {}
                                        }
                                        connected++;
                                        TeachersActivity.this.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String end = "";
                                                if (connected == 1) {
                                                    end = ", el primer elemento suele tardar un poco más.";
                                                }
                                                dialog.setMessage("Cargando " + String.valueOf(connected) + "/" + sid.size() + end);
                                                if (connected == sid.size()) {
                                                    dialog.cancel();
                                                    showGrid();
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                TeachersActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertManager alert = new AlertManager(TeachersActivity.this);
                                        alert.setMessage(getString(R.string.error_defTitle), getString(R.string.error_connect));
                                        alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                            @Override
                                            public void onClick(boolean isPositive) {
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }
            });
        }
    }

    public void requestSignatures() {
        UAWebService.HttpWebGetRequest(TeachersActivity.this, UAWebService.TUTORIAS_G_MAKE, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Signatures
                    Elements elements = doc.select("select[id=ddlAsignatura] > option");
                    for (int i = 0; i < elements.size(); i++) {
                        if(elements.eq(i).attr("value").length() > 0) {
                            sname.add(DeviceManager.capFirstLetter(elements.eq(i).text()));
                            sid.add(elements.eq(i).attr("value"));
                        }
                    }
                    getAllData();
                }
            }
        });
    }

    public void requestConn() {
        UAWebService.HttpWebGetRequest(TeachersActivity.this, UAWebService.TUTORIAS, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Material
                    Elements elements = doc.select("select[id=ddlCurso] > option");
                    for (int i = 0; i < elements.size(); i++) {
                        if (elements.eq(i).text().length() > 0) {
                            if (elements.eq(i).hasAttr("selected")) {
                                date = elements.eq(i).text();
                            }
                        }
                    }
                    requestSignatures();
                } else {
                    AlertManager alert = new AlertManager(TeachersActivity.this);
                    alert.setMessage(getResources().getString(R.string.error_defTitle), getResources().getString(R.string.error_connect));
                    alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                        @Override
                        public void onClick(boolean isPositive) {

                        }
                    });
                    alert.show();
                }
            }
        });
    }
}
