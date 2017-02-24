package com.onelio.connectu.Apps.Expediente;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class ExpedienteActivity extends AppCompatActivity {

    Elements elements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expediente);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AlertManager alert = new AlertManager(ExpedienteActivity.this);
        alert.setIcon(R.mipmap.ic_launcher);
        alert.setMessage(getString(R.string.app_name), "This app is still in development and could not work as expected");
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

    public void buildSelectDialog() {
        //Building data
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ExpedienteActivity.this, android.R.layout.select_dialog_singlechoice);
        for (int i = 0; i < elements.size(); i++) {
            String name = elements.get(i).select("td").first().text();
            arrayAdapter.add(DeviceManager.capFirstLetter(name));
        }

        //Building dialog
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ExpedienteActivity.this);
        builderSingle.setIcon(R.mipmap.ic_launcher);
        builderSingle.setTitle("Selecciona una opción:-");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strURL = elements.get(which).select("button.btn").attr("onclick");
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
                    Elements signatures = doc.select("div.NomAsi");
                    Elements notes = doc.select("div.container");
                    final String html = notes.get(1).html();
                    ExpedienteActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView text = (TextView)findViewById(R.id.content);
                            text.setText(Html.fromHtml(html));
                        }
                    });
                }
            }
        });
    }

}
