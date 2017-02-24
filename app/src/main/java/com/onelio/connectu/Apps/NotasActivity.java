package com.onelio.connectu.Apps;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.onelio.connectu.API.WebApi;
import com.onelio.connectu.Common;
import com.onelio.connectu.LoginActivity;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotasActivity extends AppCompatActivity {

    ProgressDialog dialog;
    ArrayAdapter<String> namepAsignatura;
    List<String> arraypAsignatura;
    ArrayAdapter<String> arrayoSel;
    String defSelection;
    String defAsignatura;
    String oVariable;
    String pIdOpc;
    String pFiltroCombo;
    NotasAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);
        setTitle(getString(R.string.title_activity_notas));
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();


        //Set
        arrayoSel  = new ArrayAdapter<String>(NotasActivity.this,  R.layout.spinner_item);
        arrayoSel.setDropDownViewResource(R.layout.spinner_dropdown_item);
        namepAsignatura  = new ArrayAdapter<String>(NotasActivity.this,  R.layout.spinner_item);
        namepAsignatura.setDropDownViewResource(R.layout.spinner_dropdown_item);
        arraypAsignatura = new ArrayList<String>();

        setYears();
    }

    public void setYears() {
        try {
            WebApi.get(Common.EVALUACION_URL, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Document doc = Jsoup.parse(response.body().string());
                        //Get Year
                        Elements oSel = doc.select("select[id=oSel] > option");
                        for(int c = 0; c < oSel.size(); c++) {
                            String data = oSel.eq(c).attr("value");
                            if (data.length() > 0) {
                                arrayoSel.add(data);
                            }
                        }
                        defSelection = arrayoSel.getItem(0);
                        oVariable = doc.select("input[name=oVariable]").attr("value");
                        pIdOpc = doc.select("input[name=pIdOpc]").attr("value");
                        pFiltroCombo = doc.select("input[name=pFiltroCombo]").attr("value");
                        NotasActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setTitle(getString(R.string.title_activity_notas) + " " + defSelection);
                                setDelim();
                            }
                        });
                    } else {
                        Intent intent = new Intent(getApplication(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDelim() {
        try {
            String json = "oVariable=" + oVariable + "&pIdOpc=" + pIdOpc +"&pFiltroCombo=" + pFiltroCombo + "&oSel=" + defSelection;
            WebApi.post(Common.EVALUACION_URL_2, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Document doc = Jsoup.parse(response.body().string());
                        //Get Year
                        //Get Material
                        Elements pAsignatura = doc.select("div.col-sm-7 > select[id=pAsignatura] > option");
                        for(int c = 1; c < pAsignatura.size(); c++) {
                            String data = pAsignatura.eq(c).attr("value");
                            String name = pAsignatura.eq(c).text().substring(pAsignatura.eq(c).text().lastIndexOf("-") + 1);
                            arraypAsignatura.add(data);
                            namepAsignatura.add(name.substring(0, 2).toUpperCase() + name.substring(2).toLowerCase());
                        }
                        defAsignatura = arraypAsignatura.get(0);
                        NotasActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setAdapters();
                                dialog.cancel();
                            }
                        });

                    } else {
                        Intent intent = new Intent(getApplication(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAdapters() {
        Spinner sp_sign = (Spinner)findViewById(R.id.sp_sign);
        sp_sign.setAdapter(namepAsignatura);
        sp_sign.getBackground().setColorFilter(getResources().getColor(R.color.black_overlay), PorterDuff.Mode.SRC_ATOP);
        sp_sign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                try {
                    defAsignatura = arraypAsignatura.get(position).toString();
                    doRequest();
                } catch (Exception ex) {
                    defAsignatura = "";
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
    }

    public void doRequest() {
        dialog.show();
        try {
            String json = "asignatura=" + defAsignatura + "&caca=" + defSelection;
            WebApi.post(Common.NOTAS_SIG, json, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    dialog.cancel();
                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    dialog.cancel();
                    if (response.isSuccessful()) {
                        try {
                            final JSONObject jdata = new JSONObject(response.body().string());
                            NotasActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        JSONArray notas = jdata.getJSONArray("notas");
                                        int aptos = 0;
                                        int noaptos = 0;
                                        int sum = 0;
                                        for (int c = 0; c < notas.length(); c++) {
                                            JSONObject jnota = notas.getJSONObject(c);
                                            if (jnota.getInt("NOTANUM") >= 5) {
                                                aptos++;
                                            } else {
                                                noaptos++;
                                            }
                                            sum += jnota.getInt("NOTANUM");
                                        }
                                        if (sum>0) {
                                            sum = (sum/notas.length())*10;
                                        }
                                        ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar);
                                        bar.setProgress(sum);
                                        TextView textpor = (TextView)findViewById(R.id.porcentaje);
                                        textpor.setText(String.valueOf(sum) + "%");
                                        TextView ap = (TextView)findViewById(R.id.ap);
                                        TextView sp = (TextView)findViewById(R.id.sp);
                                        ap.setText(" + " + String.valueOf(aptos) + " aprobados");
                                        sp.setText(" - " + String.valueOf(noaptos) + " suspendidos");
                                        GridView gridView = (GridView) findViewById(R.id.gridview);
                                        Adapter = new NotasAdapter(getBaseContext(), notas);
                                        gridView.setVisibility(View.VISIBLE);
                                        gridView.setAdapter(Adapter);
                                        gridView.deferNotifyDataSetChanged();
                                    } catch (JSONException e) {
                                        Toast.makeText(getBaseContext(), e.getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.cancel();
                                }
                            });
                        } catch (JSONException e) {
                            Toast.makeText(getBaseContext(), e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Intent intent = new Intent(getApplication(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
