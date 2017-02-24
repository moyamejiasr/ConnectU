package com.onelio.connectu.Apps;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.onelio.connectu.API.WebApi;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class MaterialActivity extends AppCompatActivity {

    ProgressDialog dialog;
    boolean showNews = true;
    boolean isBackPressed = false;
    JSONArray dir = new JSONArray();
    List<MatList> elem;
    MatAdapter Adapter;
    String idmat = "-1";
    String codasi = "-1";

    //Download
    String fname = "";
    String furl = "";

    void startGuide(Menu menu) {
        new MaterialIntroView.Builder(this)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .enableDotAnimation(true)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .setInfoText(getString(R.string.mat_card_1))
                .setTarget(findViewById(R.id.gridview))
                .setUsageId("mat_grid") //THIS SHOULD BE UNIQUE ID
                .performClick(true)
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.mat));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        requestConn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.material, menu);
        startGuide(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_funseen) {
            showNews = true;
        } else if (id == R.id.action_fseen) {
            showNews = false;
        } else if (id == R.id.funseen) {
            showNews = true;
        } else if (id == R.id.fseen) {
            showNews = false;
        } else {
            onBackPressed();
            return true;
        }

        dialog.show();
        codasi = "-1";
        idmat = "-1";
        dir = new JSONArray();
        requestData();

        return super.onOptionsItemSelected(item);
    }

    public void onItemClick() {
        final String folder = Environment.getExternalStorageDirectory().getAbsolutePath();
        new BottomSheet.Builder(MaterialActivity.this).title(fname).sheet(R.menu.blist).listener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case R.id.download:
                        if (DeviceManager.isStoragePermissionGranted(MaterialActivity.this)) {
                            try {
                                WebApi.download(furl, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        final File folderd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/MyUACloud");
                                        if (!folderd.exists()) {
                                            folderd.mkdirs();
                                        }
                                        File downloadedFile = new File(folderd,fname);
                                        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                                        sink.writeAll(response.body().source());
                                        sink.close();
                                        if (downloadedFile.exists()) {
                                            MaterialActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Guardado en Descargas/MyUACloud!", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        } else {
                                            MaterialActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "No se ha podido descargar el archivo", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.share:
                        if (DeviceManager.isStoragePermissionGranted(MaterialActivity.this)) {
                            try {
                                WebApi.download(furl, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        File downloadedFile = new File(folder,fname);
                                        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                                        sink.writeAll(response.body().source());
                                        sink.close();
                                        if (downloadedFile.exists()) {
                                            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                            sharingIntent.setType(getMimeType(downloadedFile.getParent() + "/" + fname));
                                            sharingIntent.putExtra(Intent.EXTRA_STREAM, downloadedFile);
                                            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share)));
                                            downloadedFile.deleteOnExit();
                                        } else {
                                            MaterialActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), getString(R.string.couldnt_download), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case R.id.open:
                        if (DeviceManager.isStoragePermissionGranted(MaterialActivity.this)) {
                            try {
                                WebApi.download(furl, new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        File downloadedFile = new File(folder,fname);
                                        BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                                        sink.writeAll(response.body().source());
                                        sink.close();
                                        if (downloadedFile.exists()) {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.fromFile(downloadedFile), getMimeType(downloadedFile.getParent() + "/" + fname));
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                            startActivity(intent);
                                            downloadedFile.deleteOnExit();
                                        } else {
                                            MaterialActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "No se ha podido descargar el archivo", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        }).show();
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public void onBackPressed() {
        if (dir.length() < 2) {
            super.onBackPressed();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                dir.remove(dir.length() - 1);
            } else {
                Toast.makeText(getBaseContext(), "Unfortunatelly, versions previous than Kitkat doesn't allow that!", Toast.LENGTH_LONG).show();
                finish();
            }
            try {
                codasi = dir.getJSONObject(dir.length() - 1).getString("codasi");
                idmat = dir.getJSONObject(dir.length() - 1).getString("idmat");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isBackPressed = true;
            requestData();
            dialog.show();
        }
    }

    public void showData() {
        ListView gridView = (ListView) findViewById(R.id.gridview);
        Adapter = new MatAdapter(getBaseContext(), elem);
        gridView.setVisibility(View.VISIBLE);
        gridView.setAdapter(Adapter);
        gridView.deferNotifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemPressed(position);
            }
        });
        dialog.cancel();
    }

    public void onItemPressed(int loc) {
        MatList object = elem.get(loc);
        if (object.isFolder() && !object.getisFolderClean()) {
            dialog.show();
            codasi = object.getCodasi();
            idmat = object.getFileid();
            requestData();
        } else if (object.isFolder() && object.getisFolderClean()) {
            Toast.makeText(getBaseContext(), "Closed!", Toast.LENGTH_SHORT);
        } else {
            //fab.setVisibility(View.VISIBLE);
            fname = object.getName();
            furl = "https://cvnet.cpd.ua.es/uamatdocente/Materiales/File?idMat=" + object.getFileid() + "&tipoorigen=" + object.getFiletype();
            onItemClick();
        }
    }

    public void addSubDir() {
        if (!isBackPressed) {
            try {
                JSONObject jdata = new JSONObject();
                jdata.put("codasi", codasi);
                jdata.put("idmat", idmat);
                dir.put(jdata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            isBackPressed = false;
        }
    }

    public void requestData() {
        String url;
        String json;
        addSubDir();
        if (showNews) {
            url = Common.MATERIALES_NEW;
            json = "idmat=" + idmat + "&codasi=" + codasi + "&expresion=&direccion=&filtro=";
        } else {
            if (dir.length() > 1) {
                url = Common.MATERIALES_NAV;
            } else {
                url = Common.MATERIALES_ALL;
            }
            json = "idmat=" + idmat + "&codasi=" + codasi + "&expresion=&direccion=&filtro=&pendientes=N&busquedarapida=N";
        }
        try {
            WebApi.post(url, json, new Callback() {
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
                        //Get Material
                        Elements elements = doc.select("div[data-num]");
                        elem = new ArrayList<>();
                        for (int i = 0; i < elements.size(); i++) {
                            MatList tmat = new MatList();
                            Elements object = elements.eq(i);
                            String type = "";
                            if (object.select("div.archivo").size() > 0) {
                                tmat.setisFolder(false);
                                tmat.setFileid(object.select("div.archivo").attr("data-id"));
                                tmat.setCodasi(object.select("div.archivo").attr("data-codasi"));
                                tmat.setFiletype(object.select("div.archivo > div.columna2").text());
                                type = "div.archivo";
                            } else if (object.select("div.filacarpvacia").size() > 0) {
                                tmat.setisFolder(true);
                                tmat.setisFolderClean(true);
                                tmat.setFileid("-1");
                                tmat.setCodasi(object.select("div.filacarpvacia").attr("data-codasi"));
                                type = "div.filacarpvacia";
                            }
                            else if (object.select("div.carpeta").size() > 0) {
                                tmat.setisFolder(true);
                                tmat.setisFolderClean(false);
                                tmat.setFileid(object.select("div.carpeta").attr("data-id"));
                                tmat.setCodasi(object.select("div.carpeta").attr("data-codasi"));
                                type = "div.carpeta";
                            }
                            if (object.select(type + " > div.columna5").size() > 0) {
                                tmat.setName(object.select(type + " > div.columna5 > span").text());
                            } else {
                                tmat.setName(object.select(type + " > div.columna5aux > span").text());
                            }
                            tmat.setDescription(object.select(type + " > div.columna6 > span.descrip").text());
                            tmat.setSigname(object.select(type + " > div.columna15 > span.asi").text());
                            tmat.setDate(object.select(type + " > div.columna13 > span.fechapubli").text());
                            tmat.setNamemaker(object.select(type + " > div.columna12 > span > img").attr("title"));
                            tmat.setImgmaker(object.select(type + " > div.columna12 > span > img").attr("src"));
                            elem.add(tmat);
                        }
                        MaterialActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            showData();
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

    public void requestConn() {
        try {
            WebApi.get(Common.MATERIALES_LOGIN, new Callback() {
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
                        requestData();
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
