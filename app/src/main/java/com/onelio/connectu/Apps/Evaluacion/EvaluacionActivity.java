package com.onelio.connectu.Apps.Evaluacion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Common;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.LoginActivity;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class EvaluacionActivity extends AppCompatActivity {

    ProgressDialog dialog;
    ArrayAdapter<String> namepTipo;
    List<String> arraypTipo;
    ArrayAdapter<String> namepAsignatura;
    List<String> arraypAsignatura;
    ArrayAdapter<String> namepOrden;
    List<String> arraypOrden;
    ArrayAdapter<String> arrayoSel;
    String defSelection;
    String defAsignatura;
    String defOrden;
    String defExamen;
    String oVariable;
    String pIdOpc;
    String pFiltroCombo;
    String pCodprs;
    JSONArray jtest;
    TestAdapter Adapter;

    View dialoglayout;
    AlertDialog alert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluacion);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.title_activity_test));

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_waite));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        //View filter
        final LayoutInflater inflater = getLayoutInflater();
        dialoglayout = inflater.inflate(R.layout.dialog_eva, null);
        //Temp changer
        final AlertDialog.Builder builder = new AlertDialog.Builder(EvaluacionActivity.this);
        builder.setView(dialoglayout);
        builder.setTitle(getString(R.string.filter_search));
        builder.setPositiveButton(getString(R.string.search), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog1, int id) {
                //DO
                setCointainers();
                alert.hide();
                dialog.show();
                doRequest();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Cancel View
            }
        });
        alert = builder.create();

        //Set
        arrayoSel  = new ArrayAdapter<String>(EvaluacionActivity.this,  R.layout.spinner_item);
        arrayoSel.setDropDownViewResource(R.layout.spinner_dropdown_item);
        namepAsignatura  = new ArrayAdapter<String>(EvaluacionActivity.this,  R.layout.spinner_item);
        namepAsignatura.setDropDownViewResource(R.layout.spinner_dropdown_item);
        namepTipo  = new ArrayAdapter<String>(EvaluacionActivity.this,  R.layout.spinner_item);
        namepTipo.setDropDownViewResource(R.layout.spinner_dropdown_item);
        namepOrden  = new ArrayAdapter<String>(EvaluacionActivity.this,  R.layout.spinner_item);
        namepOrden.setDropDownViewResource(R.layout.spinner_dropdown_item);
        arraypTipo = new ArrayList<String>();
        arraypAsignatura = new ArrayList<String>();
        arraypOrden = new ArrayList<String>();
        setCointainers();

        setYears();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.evaluacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.filter) {
            alert.show();
        } else {
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCointainers() {
        jtest = new JSONArray();
    }

    public void setYears() {
        UAWebService.HttpWebGetRequest(EvaluacionActivity.this, UAWebService.EVALUACION_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
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
                    EvaluacionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setTitle(getString(R.string.title_activity_test) + " " + defSelection);
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
    }

    public void setDelim() {
        String json = "oVariable=" + oVariable + "&pIdOpc=" + pIdOpc +"&pFiltroCombo=" + pFiltroCombo + "&oSel=" + defSelection;
        UAWebService.HttpWebPostRequest(EvaluacionActivity.this, UAWebService.EVALUACION_URL_2, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Year
                    //Get Material
                    Elements pAsignatura = doc.select("div.col-sm-7 > select[id=pAsignatura] > option");
                    for(int c = 0; c < pAsignatura.size(); c++) {
                        String data = pAsignatura.eq(c).attr("value");
                        arraypAsignatura.add(data);
                        namepAsignatura.add(pAsignatura.eq(c).text().substring(pAsignatura.eq(c).text().lastIndexOf("-") + 1));
                    }
                    defAsignatura = "";
                    //Get Test
                    Elements pTipo = doc.select("select[id=pTipo] > option");
                    for(int c = 0; c < pTipo.size(); c++) {
                        String data = pTipo.eq(c).attr("value");
                        arraypTipo.add(data);
                        namepTipo.add(DeviceManager.capFirstLetter(pTipo.eq(c).text()));
                    }
                    defExamen = "";
                    pCodprs = doc.select("input[name=pCodprs]").attr("value");
                    //Manual Add
                    arraypOrden.add("P");
                    arraypOrden.add("A");
                    arraypOrden.add("T");
                    namepOrden.add(getString(R.string.incoming));
                    namepOrden.add(getString(R.string.previous));
                    namepOrden.add(getString(R.string.all));
                    defOrden = "P";
                    EvaluacionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setAdapters();
                            doRequest();
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
    }

    public void setAdapters() {
        Spinner sp_year = (Spinner)dialoglayout.findViewById(R.id.sp_year);
        sp_year.setAdapter(arrayoSel);
        sp_year.getBackground().setColorFilter(getResources().getColor(R.color.black_overlay), PorterDuff.Mode.SRC_ATOP);
        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                defSelection = parent.getItemAtPosition(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        Spinner sp_type = (Spinner)dialoglayout.findViewById(R.id.sp_type);
        sp_type.setAdapter(namepTipo);
        sp_type.getBackground().setColorFilter(getResources().getColor(R.color.black_overlay), PorterDuff.Mode.SRC_ATOP);
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                defExamen = arraypTipo.get(position).toString();
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        Spinner sp_sign = (Spinner)dialoglayout.findViewById(R.id.sp_sign);
        sp_sign.setAdapter(namepAsignatura);
        sp_sign.getBackground().setColorFilter(getResources().getColor(R.color.black_overlay), PorterDuff.Mode.SRC_ATOP);
        sp_sign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                try {
                    defAsignatura = arraypAsignatura.get(position).toString();
                } catch (Exception ex) {
                    defAsignatura = "";
                }
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        Spinner sp_order = (Spinner)dialoglayout.findViewById(R.id.sp_order);
        sp_order.setAdapter(namepOrden);
        sp_order.getBackground().setColorFilter(getResources().getColor(R.color.black_overlay), PorterDuff.Mode.SRC_ATOP);
        sp_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                try {
                    defOrden = arraypOrden.get(position).toString();
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
        String url = UAWebService.EVALUACION_URL_3 + "caca=" + defSelection + "&codper=" + pCodprs + "&codasi=" + defAsignatura + "&ver=" + defOrden + "&tipo=" + defExamen;
        String json = "iDisplayLength=-1";
        UAWebService.HttpWebPostRequest(EvaluacionActivity.this, url, json, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        final JSONObject jdata = new JSONObject(body);
                        EvaluacionActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray jobj = jdata.getJSONArray("aaData");
                                    for (int c = 0; c < jobj.length(); c++) {
                                        JSONObject jtget = new JSONObject();
                                        jtget.put("tipo", jobj.getJSONArray(c).getString(1));
                                        jtget.put("nombre", jobj.getJSONArray(c).getString(2));
                                        jtget.put("lugar", jobj.getJSONArray(c).getString(3));
                                        jtget.put("asig_id", jobj.getJSONArray(c).getString(4));
                                        jtget.put("subnombre", jobj.getJSONArray(c).getString(5));
                                        jtget.put("id", jobj.getJSONArray(c).getString(8));
                                        jtget.put("inicio", jobj.getJSONArray(c).getString(9));
                                        jtget.put("final", jobj.getJSONArray(c).getString(10));
                                        jtget.put("asignatura", jobj.getJSONArray(c).getString(13));
                                        jtget.put("abierto", jobj.getJSONArray(c).getString(14));
                                        jtget.put("completado", jobj.getJSONArray(c).getString(15));
                                        jtget.put("nota", jobj.getJSONArray(c).getString(16));
                                        jtest.put(jtget);
                                    }
                                    GridView gridView = (GridView) findViewById(R.id.gridview);
                                    Adapter = new TestAdapter(getBaseContext(), jtest);
                                    gridView.setVisibility(View.VISIBLE);
                                    gridView.setAdapter(Adapter);
                                    gridView.deferNotifyDataSetChanged();
                                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            try {
                                                Common.evid = jtest.getJSONObject(position).getString("id");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            startActivity(new Intent(EvaluacionActivity.this, EvaluacionViewActivity.class));
                                        }
                                    });
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
            }
        });
    }

}
