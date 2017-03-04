package com.onelio.connectu.Apps.Expediente;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Apps.Tutorias.TutoriaActivity;
import com.onelio.connectu.Apps.Tutorias.TutoriaAdapter;
import com.onelio.connectu.Apps.Tutorias.TutoriaViewActivity;
import com.onelio.connectu.Common;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ExpedienteActivity extends AppCompatActivity {

    Elements elements;
    ExpedienteAdapter Adapter;
    List<JSONObject> jarray = new ArrayList<>();
    String cname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expediente);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getCarrera();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;

    }

    public void getCarrera() {
        //Limpiar grid
        UAWebService.HttpWebGetRequest(ExpedienteActivity.this, UAWebService.EXP_LOAD, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    elements = doc.select("table.table");
                    ExpedienteActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buildSelectDialog();
                        }
                    });
                }
            }
        });
    }

    ArrayList<String> ids = new ArrayList<>();

    public void buildSelectDialog() {
        //Building data
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ExpedienteActivity.this, android.R.layout.select_dialog_item);
        for (int i = 0; i < elements.size(); i++) {
            Elements objects = elements.get(i).select("tr");
            for (int j = 0; j < objects.size(); j++) {
                if (objects.get(j).children().size() > 0) {
                    String name = objects.get(j).child((0)).text();
                    arrayAdapter.add(DeviceManager.capFirstLetter(name));
                    ids.add(objects.get(j).child(1).select("button.btn").attr("onclick"));
                }
            }
        }

        //Building dialog
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ExpedienteActivity.this);
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle(getString(R.string.select));

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cname = arrayAdapter.getItem(which);
                String strURL = ids.get(which);
                strURL = strURL.substring(strURL.indexOf("'") + 1, strURL.length() - 1);
                loadPage("https://cvnet.cpd.ua.es" + strURL);
            }
        });
        builderSingle.show();
    }

    void loadPage(String url) {
        UAWebService.HttpWebGetRequest(ExpedienteActivity.this, url, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc  = Jsoup.parse(body);
                    Elements test = doc.select("div.campoDato");

                    for (int i = 0; i < test.size()/7; i++) {
                        try {
                            int cpunt = i*7;
                            JSONObject jdata = new JSONObject();
                            jdata.put("sig_id", test.get(cpunt).text());
                            jdata.put("sig_name", test.get(cpunt + 1).text());
                            jdata.put("sig_type", test.get(cpunt + 2).text());
                            jdata.put("convocat", test.get(cpunt + 5).text());
                            jdata.put("nota", test.get(cpunt + 6).text());
                            jarray.add(jdata);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    putData();
                }
            }
        });
    }

    void putData() {
        ExpedienteActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView name = (TextView)findViewById(R.id.name);
                TextView carrer = (TextView)findViewById(R.id.carrer);
                name.setText(Common.name);
                carrer.setText(cname);

                GridView gridView = (GridView) findViewById(R.id.gridview);
                Adapter = new ExpedienteAdapter(getBaseContext(), jarray);

                Animation animation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.grid_item_anim);
                GridLayoutAnimationController controller = new GridLayoutAnimationController(animation, .2f, .2f);
                gridView.setVisibility(View.VISIBLE);
                gridView.setAdapter(Adapter);
                gridView.setLayoutAnimation(controller);
            }
        });
    }

}
