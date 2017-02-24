package com.onelio.connectu.Apps.Anuncios;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AnunciosActivity extends AppCompatActivity {

    public List<AdList> data = new ArrayList<AdList>();
    ProgressDialog dialog;
    AdAdapter Adapter;
    boolean isLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("UAAnuncios");
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        refreshData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.anuncios, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.reload) {
            dialog.show();
            data.clear();
            refreshData();
            return super.onOptionsItemSelected(item);
        } else {
            super.onBackPressed();
            return true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }

    public void refreshData() {
        UAWebService.HttpWebGetRequest(AnunciosActivity.this, UAWebService.ANUNCION_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get User Data
                    Elements list = doc.select("li.bloque");
                    for(int c = 0; c < list.size(); c++) {
                        AdList ldata = new AdList();
                        ldata.setBadge(list.eq(c).select("div.tipo > span.badge").text());
                        ldata.setTitulo1(list.eq(c).select("div.titulo > div.bloquetitulo > span.titanuncio").text());
                        ldata.setTitul2(list.eq(c).select("div.titulo > div.bloquetitulo > span.titulo2").text());
                        ldata.setFecha(list.eq(c).select("div.titulo > div.bloquetitulo > span.fecha").text());
                        //
                        ldata.setTexto(list.eq(c).select("div.anunciocollapse > div.texto > div.textovalor").text());
                        ldata.setHTexto(list.eq(c).select("div.anunciocollapse > div.texto > div.textovalor").outerHtml());
                        ldata.setAsignatura(list.eq(c).select("div.anunciocollapse > div.asignatura > span.asignaturavalor").text());
                        ldata.setProfesor(list.eq(c).select("div.anunciocollapse > div.profesor > span.profesorvalor").text());
                        ldata.setId(list.eq(c).select("div.tipo > a.accordion-toggle > span.verocultar").attr("data-anuncio"));
                        if (list.eq(c).select("div.tipo > a.accordion-toggle > span.verocultar").text().contains("Marcar como leido")) {
                            ldata.setState(true);
                        } else {
                            ldata.setState(false);
                        }
                        data.add(ldata);
                    }
                    AnunciosActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateData();
                            dialog.cancel();
                        }
                    });

                } else {
                    AlertManager alert = new AlertManager(AnunciosActivity.this);
                    alert.setMessage(getString(R.string.error_defTitle) ,getString(R.string.error_connect));
                    alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                        @Override
                        public void onClick(boolean isPositive) {
                            DeviceManager.appClose();
                        }
                    });
                    alert.show();
                }
            }
        });
    }

    public void updateData() {
        GridView gridView = (GridView) findViewById(R.id.gridview);
        Adapter = new AdAdapter(getBaseContext(), data, AnunciosActivity.this);
        gridView.setVisibility(View.VISIBLE);
        gridView.setAdapter(Adapter);
    }

}
