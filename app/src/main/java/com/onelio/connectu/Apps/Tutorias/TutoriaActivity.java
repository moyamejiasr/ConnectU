package com.onelio.connectu.Apps.Tutorias;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Apps.NTAdapter;
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

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class TutoriaActivity extends AppCompatActivity {

    ProgressDialog dialog;
    String date;
    List<String> sname = new ArrayList<>();
    List<String> sid = new ArrayList<>();
    List<TutoriaList> elem;
    TutoriaAdapter Adapter;
    String url = "";
    boolean showNews = true;

    void startGuide(Menu menu) {
        new MaterialIntroView.Builder(this)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .enableDotAnimation(true)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .setInfoText(getString(R.string.tut_card_1))
                .setTarget(findViewById(R.id.gridview))
                .setUsageId("tut_grid") //THIS SHOULD BE UNIQUE ID
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String s) {
                        new MaterialIntroView.Builder(TutoriaActivity.this)
                                .enableIcon(false)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.MINIMUM)
                                .setDelayMillis(500)
                                .enableFadeAnimation(true)
                                .setInfoText(getString(R.string.tut_card_2))
                                .setTarget(findViewById(R.id.fab))
                                .setUsageId("tut_add") //THIS SHOULD BE UNIQUE ID
                                .performClick(true)
                                .show();
                    }
                })
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutoria);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNew();
            }
        });
        //Set

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_wait));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        emptyText.setText(getString(R.string.notnoread));
        url = Common.TUTORIAS_UNSEEN;
        requestConn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tutorias, menu);
        startGuide(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        elem.clear();
        int id = item.getItemId();
        if (id == R.id.action_funseen) {
            showNews = true;
            url = Common.TUTORIAS_UNSEEN;
        } else if (id == R.id.action_fseen) {
            showNews = false;
            url = Common.TUTORIAS_SEEN;
        } else if (id == R.id.funseen) {
            showNews = true;
            url = Common.TUTORIAS_UNSEEN;
        } else if (id == R.id.fseen) {
            showNews = false;
            url = Common.TUTORIAS_SEEN;
        } else {
            super.onBackPressed();
            return true;
        }

        dialog.show();
        loadSeen();

        return super.onOptionsItemSelected(item);
    }

    public void addNew() {
        dialog.show();
        UAWebService.HttpWebGetRequest(TutoriaActivity.this, UAWebService.TUTORIAS_G_MAKE, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Asignaturas
                    Elements elements = doc.select("select[id=ddlAsignatura] > option");
                    final List<String> signatures = new ArrayList<String>();
                    final List<String> sigid = new ArrayList<String>();
                    for (int i = 0; i < elements.size(); i++) {
                        if(elements.eq(i).attr("value").length() > 0) {
                            signatures.add(DeviceManager.capFirstLetter(elements.eq(i).text()));
                            sigid.add(elements.eq(i).attr("value"));
                        }
                    }

                    TutoriaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.cancel();
                            addNewonSinatures(signatures, sigid);
                        }
                    });
                } else {
                    AlertManager alert = new AlertManager(TutoriaActivity.this);
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

    public void addNewonSinatures(List<String> signatures, final List<String> sigid) {
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(TutoriaActivity.this);
        builderSingle.setIcon(R.drawable.ic_menu_send);
        builderSingle.setTitle(getString(R.string.ssubject));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TutoriaActivity.this, android.R.layout.select_dialog_singlechoice);
        for(int d = 0; d < signatures.size(); d++) {
            arrayAdapter.add(signatures.get(d));
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface idialog, int which) {
                final String id = sigid.get(which);
                dialog.show();
                dialog.setMessage(getString(R.string.loading_waitt));
                String json = "{\"Cod\":\"" + id + "\",\"Curso\":\"" + date + "\"}";
                UAWebService.HttpWebJSONPostRequest(TutoriaActivity.this, UAWebService.TUTORIAS_G_DES, json, new UAWebService.WebCallBack() {
                    @Override
                    public void onNavigationComplete(boolean isSuccessful, String body) {
                        if (isSuccessful) {
                            Document doc = Jsoup.parse(body);
                            //Get Material
                            Elements elements = doc.select("option");
                            final List<String> ttext = new ArrayList<String>();
                            final List<String> tid = new ArrayList<String>();
                            for (int i = 0; i < elements.size(); i++) {
                                if(elements.eq(i).attr("value").length() > 0) {
                                    ttext.add(DeviceManager.capFirstLetter(elements.eq(i).text()));
                                    tid.add(elements.eq(i).attr("value"));
                                }
                            }
                            //GET Images
                            String json = "{\"Cod\":\"" + id + "\",\"Curso\":\"" + date + "\"}";
                            UAWebService.HttpWebJSONPostRequest(TutoriaActivity.this, UAWebService.TUTORIAS_G_SIGN, json, new UAWebService.WebCallBack() {
                                @Override
                                public void onNavigationComplete(boolean isSuccessful, String body) {
                                    TutoriaActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.setMessage(getString(R.string.loading_wait));
                                            dialog.cancel();
                                        }
                                    });
                                    if (isSuccessful) {
                                        Document doc = Jsoup.parse(body);
                                        //Get Material
                                        Elements elements = doc.select("div.well");
                                        final List<String> timg = new ArrayList<String>();
                                        for (int c = 0; c < ttext.size(); c++) {
                                            for (int i = 0; i < elements.size(); i++) {
                                                String name = DeviceManager.capFirstLetter(elements.eq(i).select("h4").text());
                                                if (ttext.get(c).contains(name)) {
                                                    timg.add(elements.eq(i).select("img").attr("src"));
                                                }
                                            }
                                        }
                                        if (ttext.size() == timg.size()) {
                                            TutoriaActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loadNewTutWithIMG(ttext, timg, tid, id);
                                                }
                                            });
                                        } else {
                                            TutoriaActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loadNewTutWithoutIMG(ttext, tid, id);
                                                }
                                            });
                                        }
                                    } else {
                                        AlertManager alert = new AlertManager(TutoriaActivity.this);
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
                        } else {
                            AlertManager alert = new AlertManager(TutoriaActivity.this);
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
        });
        builderSingle.show();
    }

    public void loadNewTutWithoutIMG(List<String> ttext, final List<String> tid, final String id) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(TutoriaActivity.this);
        builderSingle.setIcon(R.drawable.ic_menu_send);
        builderSingle.setTitle(getString(R.string.steacher));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TutoriaActivity.this, android.R.layout.select_dialog_singlechoice);
        for(int d = 0; d < ttext.size(); d++) {
            arrayAdapter.add(ttext.get(d));
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface idialog, int which) {
                final String stid = tid.get(which);
                AlertDialog.Builder builder = new AlertDialog.Builder(TutoriaActivity.this);
                builder.setTitle(getString(R.string.ititle));

                final EditText input = new EditText(TutoriaActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Common.jdata = new JSONObject();
                        try {
                            Common.jdata.put("ddlCurso", date);
                            Common.jdata.put("ddlAsignatura", id);
                            Common.jdata.put("ddlDestinatario", stid);
                            Common.jdata.put("ckDestinatarios", "1");
                            Common.jdata.put("ckDestinatarios1", "false");
                            Common.jdata.put("txtAsunto", input.getText().toString());
                            Common.cTitle = input.getText().toString();
                            Common.isNewChat = true;
                            startActivity(new Intent(TutoriaActivity.this, TutoriaViewActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        builderSingle.show();
    }

    public void loadNewTutWithIMG(List<String> ttext, List<String> timg, final List<String> tid, final String id) {
        // Prepare grid view
        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(TutoriaActivity.this);
        GridView gridView = new GridView(TutoriaActivity.this);
        gridView.setAdapter(new NTAdapter(getBaseContext(), ttext, timg));
        gridView.setNumColumns(1);


        builderSingle.setIcon(R.drawable.ic_menu_send);
        builderSingle.setTitle(getString(R.string.steacher));
        builderSingle.setView(gridView);
        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = builderSingle.show();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long lid) {
                alert.dismiss();
                final String stid = tid.get(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(TutoriaActivity.this);
                builder.setTitle(getString(R.string.ititle));

                final EditText input = new EditText(TutoriaActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Common.jdata = new JSONObject();
                        try {
                            Common.jdata.put("ddlCurso", date);
                            Common.jdata.put("ddlAsignatura", id);
                            Common.jdata.put("ddlDestinatario", stid);
                            Common.jdata.put("ckDestinatarios", "1");
                            Common.jdata.put("ckDestinatarios1", "false");
                            Common.jdata.put("txtAsunto", input.getText().toString());
                            Common.cTitle = input.getText().toString();
                            Common.isNewChat = true;
                            startActivity(new Intent(TutoriaActivity.this, TutoriaViewActivity.class));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    public void loadTutorias() {
        String post = "oVariable=SelAnyAcaAlu&pFiltroCombo=S&oSel=" + date;
        UAWebService.HttpWebPostRequest(TutoriaActivity.this, UAWebService.TUTORIAS_ALL, post, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Material
                    Elements elements = doc.select("div.panel.panel-info");
                    for (int i = 0; i < elements.size(); i++) {
                        String subject = elements.eq(i).select("div.panel-heading").text();
                        String signame = DeviceManager.capFirstLetter(DeviceManager.after(DeviceManager.before(subject, " ("), date + "&nbsp;"));
                        String signid = DeviceManager.after(DeviceManager.before(subject, ")"), "(");
                        sname.add(signame);
                        sid.add(signid);
                    }
                    TutoriaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadSeen();
                        }
                    });
                } else {
                    AlertManager alert = new AlertManager(TutoriaActivity.this);
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

    public void showData() {
        if (Adapter!=null) {
            Adapter.notifyDataSetChanged();
        }
        ListView gridView = (ListView) findViewById(R.id.gridview);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        gridView.setEmptyView(emptyText);
        Adapter = new TutoriaAdapter(getBaseContext(), elem);
        gridView.setVisibility(View.VISIBLE);
        gridView.setAdapter(Adapter);
        gridView.deferNotifyDataSetChanged();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Common.isNewChat = false;
                Common.tutId = elem.get(position).getId();
                Common.cTitle = elem.get(position).getName();
                startActivity(new Intent(TutoriaActivity.this, TutoriaViewActivity.class));
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        dialog.cancel();
    }

    public void loadSeen(){
        elem = new ArrayList<TutoriaList>();
        elem.clear();
        for(int i = 0; i < sid.size(); i++) {
            final String sgname = sname.get(i);
            UAWebService.HttpWebPostRequest(TutoriaActivity.this, url, "sEcho=1" +
                    "&iColumns=6" +
                    "&sColumns=" +
                    "&iDisplayStart=0" +
                    "&iDisplayLength=10" +
                    "&sSearch=" +
                    "&bRegex=false" +
                    "&sSearch_0=" +
                    "&bRegex_0=false" +
                    "&bSearchable_0=true" +
                    "&sSearch_1=" +
                    "&bRegex_1=false" +
                    "&bSearchable_1=false" +
                    "&sSearch_2=" +
                    "&bRegex_2=false" +
                    "&bSearchable_2=false" +
                    "&sSearch_3=" +
                    "&bRegex_3=false" +
                    "&bSearchable_3=false" +
                    "&sSearch_4=" +
                    "&bRegex_4=false" +
                    "&bSearchable_4=true" +
                    "&sSearch_5=" +
                    "&bRegex_5=false" +
                    "&bSearchable_5=true" +
                    "&iSortCol_0=2" +
                    "&sSortDir_0=desc" +
                    "&iSortingCols=1" +
                    "&bSortable_0=true" +
                    "&bSortable_1=true" +
                    "&bSortable_2=true" +
                    "&bSortable_3=true" +
                    "&bSortable_4=true" +
                    "&bSortable_5=false" +
                    "&AssCodnum=" + sid.get(i) +
                    "&AnyCaca=" + date +
                    "&sRangeSeparator=~" + date, new UAWebService.WebCallBack() {
                @Override
                public void onNavigationComplete(boolean isSuccessful, String body) {
                    if (isSuccessful) {
                        try {
                            JSONObject jdata = new JSONObject(body);
                            int size = jdata.getInt("iTotalDisplayRecords");
                            JSONArray jobj = jdata.getJSONArray("aaData");
                            if (size==0) {
                                TutoriaActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.cancel();
                                    }
                                });
                            }
                            if (size < 1) {
                                TutoriaActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (Adapter != null) {
                                            Adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                            for (int n = 0; n < size; n++) {
                                JSONArray jitem = jobj.getJSONArray(n);
                                String name;
                                String startdate;
                                String state;
                                String html;
                                String id;
                                if (showNews) {
                                    name = jitem.getString(0);
                                    startdate = jitem.getString(1);
                                    state = getString(R.string.pending);
                                    html = jitem.getString(3);
                                    id = jitem.getString(4);
                                } else {
                                    name = jitem.getString(0);
                                    startdate = jitem.getString(1);
                                    state = jitem.getString(3);
                                    html = jitem.getString(4);
                                    id = jitem.getString(5);
                                }
                                Document doc = Jsoup.parse(html);
                                Elements elements = doc.select("img");
                                String user = elements.attr("title");
                                String src = elements.attr("src");
                                TutoriaList item = new TutoriaList();
                                item.setName(name);
                                item.setStartdate(startdate);
                                item.setState(state);
                                item.setUser(user);
                                item.setSrc(src);
                                item.setId(id);
                                item.setSign(sgname);
                                elem.add(item);
                                TutoriaActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        showData();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void requestConn() {
        UAWebService.HttpWebGetRequest(TutoriaActivity.this, UAWebService.TUTORIAS, new UAWebService.WebCallBack() {
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
                    TutoriaActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadTutorias();
                        }
                    });
                } else {
                    AlertManager alert = new AlertManager(TutoriaActivity.this);
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

}
