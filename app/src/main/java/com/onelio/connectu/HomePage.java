package com.onelio.connectu;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.GridLayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Apps.Anuncios.AnunciosActivity;
import com.onelio.connectu.Apps.Evaluacion.EvaluacionActivity;
import com.onelio.connectu.Apps.Expediente.ExpedienteActivity;
import com.onelio.connectu.Apps.Horario.HorarioActivity;
import com.onelio.connectu.Apps.MaterialActivity;
import com.onelio.connectu.Apps.Profesores.TeachersActivity;
import com.onelio.connectu.Apps.Tutorias.TutoriaActivity;
import com.onelio.connectu.Apps.WebView.WebViewActivity;
import com.onelio.connectu.BackgroundService.Coordinator;
import com.onelio.connectu.BackgroundService.UAService;
import com.onelio.connectu.Database.RealmManager;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.Device.RateMeMaybe;
import com.onelio.connectu.Device.UAUpdater;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean isFirsttime = true;
    //MAT
    ListView list;
    List<ItemList> rowData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    AppsAdapter appsAdapter;
    NavigationView navigationView;

    //Rating and Guide
    public void launchMarket()
    {
        //Shortcut market
        if (Common.firstStart && isFirsttime) {
            AlertManager alert = new AlertManager(this);
            alert.setIcon(R.mipmap.ic_launcher);
            alert.setMessage(getString(R.string.app_name), getString(R.string.shortcut));
            alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    DeviceManager.addShortcutToHorario(getApplicationContext());
                    Toast.makeText(getBaseContext(), getString(R.string.created_shortcut), Toast.LENGTH_LONG).show();
                }
            });
            alert.setNegativeButton("NO", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {

                }
            });
            alert.show();
        }
        //Times Market
        RateMeMaybe rmm = new RateMeMaybe(this);
        rmm.setPromptMinimums(5, 3, 10, 4);
        rmm.setDialogMessage(getString(R.string.like_app) + " " + "%totalLaunchCount%" + " " + getString(R.string.like_app_1));
        rmm.setDialogTitle(getString(R.string.rate_app));
        rmm.setIcon(R.mipmap.ic_launcher);
        rmm.setPositiveBtn("Yeehaaa!");
        rmm.setNeutralBtn(getString(R.string.later));
        rmm.setNegativeBtn(getString(R.string.never));
        rmm.run();
    }

    void requestDataUpdate() {
        if (Common.isNotifUOn) {
            final AlertManager alertManager = new AlertManager(HomePage.this);
            alertManager.setMessage(getString(R.string.update_profile_tit), getString(R.string.update_profile));
            alertManager.setPositiveButton("Ok", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {

                }
            });
            alertManager.setNegativeButton(getString(R.string.why), new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    HomePage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            alertManager.cancel();
                            AlertManager alert = new AlertManager(HomePage.this);
                            alert.setMessage("ConnectU Help", getString(R.string.why_update));
                            alert.setPositiveButton("Ok", new AlertManager.AlertCallBack() {
                                @Override
                                public void onClick(boolean isPositive) {
                                }
                            });
                            alert.show();
                        }
                    });
                }
            });
            alertManager.show();
        }
        new UAUpdater.updateDataResult(HomePage.this, new UAUpdater.UpdaterCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, JSONObject data) {
                if (isSuccessful) {
                    RealmManager realm = new RealmManager(HomePage.this);
                    //Update last modify
                    Calendar time = Calendar.getInstance();
                    int week = time.get(Calendar.WEEK_OF_MONTH);
                    int month = time.get(Calendar.MONTH) + 1;
                    int year = time.get(Calendar.YEAR);
                    JSONObject jdate = null;
                    try {
                        jdate = new JSONObject(realm.getOption("launchTimes"));
                        jdate = new JSONObject();
                        jdate.put("week", week);
                        jdate.put("month", month);
                        jdate.put("year", year);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    realm.modifyOption("launchTimes", jdate.toString());
                    //Set data
                    realm.modifyOption("userData", data.toString());
                    Common.data = data;
                    realm.deleteRealmInstance();
                } else {
                    FirebaseCrash.log("Launcher Activity - Failed to edit User profile!");
                    FirebaseCrash.report(new Exception("Failed to profile"));
                    HomePage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertManager alert = new AlertManager(HomePage.this);
                            alert.setMessage("ERROR!", "Error trying to update profile! Pleasy notify to developers at rmoya98@gmail.com");
                            alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                                @Override
                                public void onClick(boolean isPositive) {
                                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                            "mailto","rmoya98@gmail.com", null));
                                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contacto desde ConnectU de " + Common.name);
                                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje y de ser posible tu carrera y nombre. Gracias");
                                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                                }
                            });
                            alert.show();
                        }
                    });
                }
            }
        }).execute();
    }

    void startGuide(Menu menu) {
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        new MaterialIntroView.Builder(this)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .enableDotAnimation(true)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .setInfoText(getString(R.string.home_card_1))
                .setTarget(findViewById(R.id.gridview))
                .setUsageId("home_grid") //THIS SHOULD BE UNIQUE ID
                .setListener(new MaterialIntroListener() {
                    @Override
                    public void onUserClicked(String materialIntroViewId) {
                        new MaterialIntroView.Builder(HomePage.this)
                                .enableIcon(false)
                                .setFocusGravity(FocusGravity.CENTER)
                                .setFocusType(Focus.MINIMUM)
                                .setDelayMillis(500)
                                .enableFadeAnimation(true)
                                .setInfoText(getString(R.string.home_card_2))
                                .setTarget(searchView)
                                .setUsageId("home_searchView") //THIS SHOULD BE UNIQUE ID
                                .show();
                    }
                })
                .show();
    }

    @Override
    public void onDestroy() {
        if (Coordinator.mServiceIntent != null) {
            stopService(Coordinator.mServiceIntent);
        }
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        //Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        //Navigation Right Panel
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.home);

        if (isFirsttime) {
            isFirsttime = false;
            Coordinator.mSensorService = new UAService();
            Coordinator.mServiceIntent = new Intent(getBaseContext(), Coordinator.mSensorService.getClass());
            if (!isMyServiceRunning(Coordinator.mSensorService.getClass()) && Common.isNotifOn) {
                startService(Coordinator.mServiceIntent);
            }
            updateData(); //Alerts Data
            //Count run for like app
            launchMarket();
            //Update Data
            if (Common.updateData) { //Local Stored data
                requestDataUpdate();
            }
        }
        //Swipe to refresh view in Gridview
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    public void gridShow() {
        List<String> strings = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.app_names)));
        final JSONArray array = new JSONArray();
        for (int i = 0; i < strings.size(); i++) {
            if (DeviceManager.capFirstLetter(strings.get(i)).contains(DeviceManager.capFirstLetter(filter))) {
                JSONObject jdata = new JSONObject();
                try {
                    jdata.put("Id", i);
                    jdata.put("Color", DeviceManager.appGetColor(i));
                    jdata.put("Name", strings.get(i));
                    jdata.put("Img", DeviceManager.appGetSrc(i));
                    array.put(jdata);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        final GridView gridView = (GridView) findViewById(R.id.gridview);
        appsAdapter = new AppsAdapter(getBaseContext(), array);
        Animation animation = AnimationUtils.loadAnimation(getBaseContext(),R.anim.grid_item_anim);
        GridLayoutAnimationController controller = new GridLayoutAnimationController(animation, .2f, .2f);
        gridView.setVisibility(View.VISIBLE);
        gridView.setAdapter(appsAdapter);
        gridView.setLayoutAnimation(controller);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                int rpos = 0;
                try {
                    rpos = array.getJSONObject(position).getInt("Id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (rpos) {
                    case 0:
                        startActivity(new Intent(HomePage.this, AnunciosActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(HomePage.this, MaterialActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(HomePage.this, TeachersActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(HomePage.this, TutoriaActivity.class));
                        break;
                    case 4:
                        Common.webName = "UAMoodle";
                        Common.webURL = "https://moodle2016-17.ua.es/moodle/login/"; //TODO CHANGE MOODLE YEAR
                        startActivity(new Intent(HomePage.this, WebViewActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(HomePage.this, HorarioActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(HomePage.this, EvaluacionActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(HomePage.this, ExpedienteActivity.class));
                        break;
                    case 8:
                        Common.webName = "UAProject";
                        Common.webURL = "https://molins.cpd.ua.es/tfc/indexCas.php";
                        startActivity(new Intent(HomePage.this, WebViewActivity.class));
                        break;
                    case 9:
                        Common.webName = "Services";
                        Common.webURL = "https://cv1.cpd.ua.es/webcv/ctrlzonapersonal/LoginCvCAS.asp";
                        startActivity(new Intent(HomePage.this, WebViewActivity.class));
                        break;
                    case 10:
                        Common.webName = "Campus Map";
                        Common.webURL = "https://www.sigua.ua.es";
                        startActivity(new Intent(HomePage.this, WebViewActivity.class));
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        SlidingUpPanelLayout sup = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (sup.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                sup.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED); //to close
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        //Start Guide - once
        startGuide(menu);
        //Start menu
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                filter = newText;
                gridShow();
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                filter = query;
                gridShow();
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getBaseContext(), PreferencesActivity.class));
            return true;
        } else if (id == R.id.action_reload) {
            refreshData();
            return true;
        } else if (id == R.id.reload) {
            refreshData();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.contacto) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","rmoya98@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contacto desde ConnectU de " + Common.name);
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje...");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } else if (id == R.id.about) {
            startActivity(new Intent(getBaseContext(), AboutActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(getBaseContext(), PreferencesActivity.class));

        } else if (id == R.id.acc) {
            startActivity(new Intent(getBaseContext(), AccountActivity.class));

        } else if (id == R.id.logout) {
            AlertManager alert = new AlertManager(HomePage.this);
            alert.setMessage("Account Manager", getResources().getString(R.string.rlogout));
            alert.setIcon(android.R.drawable.ic_dialog_alert);
            alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    RealmManager manager = new RealmManager(HomePage.this);
                    manager.deleteAll();
                    manager.deleteRealmInstance();
                    UAService.active = false;
                    Common.isNotifOn = false;
                    Intent intent = new Intent(getApplication(), LauncherActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            alert.setNegativeButton("CANCEL", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {

                }
            });
            alert.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    String filter = "";

    public void refreshData() {
        final GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setAdapter(null);
        UAWebService.HttpWebGetRequest(HomePage.this, UAWebService.LOGIN_URL, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get User Data
                    Elements name = doc.select("a.dropdown-toggle > span[id=nombre]");
                    if (!name.text().contains("Usuario no validado")) {
                        Elements alertas = doc.select("a.dropdown-toggle > span.numeroAlertas");
                        Elements foto = doc.select("a.dropdown-toggle > span[id=retrato] > img");
                        Common.ANUNCIOS = doc.select("a[target=ANUNCIOS] > span.titulo");
                        Common.SUBANUNCIOS = doc.select("a[target=ANUNCIOS] > span.textoNotificacion");
                        Common.MATDOCENTE = doc.select("a[target=MATDOCENTE] > span.textoNotificacion");
                        Common.SUBMATDOCENTE = doc.select("a[target=MATDOCENTE] > span.numeroNotificacion");
                        Common.UATUTORIAS = doc.select("a[target=TUTORIAS] > span.titulo");
                        Common.SUBUATUTORIAS = doc.select("a[target=TUTORIAS] > span.textoNotificacion");
                        Common.UAEVALUACION = doc.select("a[target=UAEVALUACION] > span.titulo");
                        Common.SUBUAEVALUACION = doc.select("a[target=UAEVALUACION] > span.textoNotificacion");
                        Common.alerts = alertas.text();
                        Common.name = name.text();
                        Common.src = foto.attr("src");
                        HomePage.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSwipeRefreshLayout.setRefreshing(false);
                                updateData();
                            }
                        });
                    } else {
                        Intent intent = new Intent(getApplication(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    HomePage.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), "Couldn't reload because connection failed", Toast.LENGTH_SHORT);
                        }
                    });
                }
            }
        });
    }


    public void updateData() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView usern = (TextView)headerView.findViewById(R.id.usernametab);
        TextView alerts = (TextView)headerView.findViewById(R.id.alerts);
        CircularImageView view = (CircularImageView)headerView.findViewById(R.id.imageView);
        usern.setText(Common.name);
        alerts.setText(getResources().getString(R.string.you_have) + " " + Common.alerts + " " + getResources().getString(R.string.news));
        if (Common.src.length() > 0) {
            Picasso.with(getBaseContext())
                    .load(Common.src)
                    .into(view);
        }
        gridShow();
        setAlerts();
    }

    public void setAlerts() {
        TextView nottext = (TextView)findViewById(R.id.nottext);
        TextView not_big_text = (TextView)findViewById(R.id.not_big_text);
        nottext.setText(Common.alerts);
        not_big_text.setText(getResources().getString(R.string.you_have) + " " + Common.alerts + " " + getResources().getString(R.string.pending_notifications));
        //AUNUNCIOS
        rowData =new ArrayList<ItemList>();
        if (Common.ANUNCIOS != null) {
            for (int i = 0; i < Common.ANUNCIOS.size(); i++) {
                ItemList data = new ItemList();
                data.setTitle(DeviceManager.capFirstLetter(Common.ANUNCIOS.eq(i).text()));
                data.setSubtitle(Common.SUBANUNCIOS.eq(i).text());
                rowData.add(data);
            }
        }
        list = (ListView) findViewById(R.id.anlist);
        ItemListAdapter adapter = new ItemListAdapter(this, rowData);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                startActivity(new Intent(HomePage.this, AnunciosActivity.class));
            }
        });
        justifyListViewHeightBasedOnChildren(list);
        //MATDOCENTE
        rowData =new ArrayList<ItemList>();
        if (Common.MATDOCENTE != null) {
            for (int i = 0; i < Common.MATDOCENTE.size(); i++) {
                ItemList data = new ItemList();
                data.setTitle(DeviceManager.capFirstLetter(Common.MATDOCENTE.eq(i).text().substring(Common.MATDOCENTE.eq(i).text().lastIndexOf(") ") + 2)));
                data.setSubtitle(getResources().getString(R.string.you_have) + Common.SUBMATDOCENTE.eq(i).text() + " " + getResources().getString(R.string.pending_files));
                rowData.add(data);
            }
        }
        list = (ListView) findViewById(R.id.matlist);
        adapter = new ItemListAdapter(this, rowData);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                startActivity(new Intent(HomePage.this, MaterialActivity.class));
            }
        });
        justifyListViewHeightBasedOnChildren(list);
        //UATUTORIAS
        rowData =new ArrayList<ItemList>();
        if (Common.UATUTORIAS != null) {
            for (int i = 0; i < Common.UATUTORIAS.size(); i++) {
                ItemList data = new ItemList();
                data.setTitle(DeviceManager.capFirstLetter(Common.UATUTORIAS.eq(i).text()));
                data.setSubtitle(Common.SUBUATUTORIAS.eq(i).text());
                rowData.add(data);
            }
        }
        list = (ListView) findViewById(R.id.tutlist);
        adapter = new ItemListAdapter(this, rowData);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                startActivity(new Intent(HomePage.this, TutoriaActivity.class));
            }
        });
        justifyListViewHeightBasedOnChildren(list);
        //UAEVALUACION
        rowData =new ArrayList<ItemList>();
        if (Common.UAEVALUACION != null) {
            for (int i = 0; i < Common.UAEVALUACION.size(); i++) {
                ItemList data = new ItemList();
                data.setTitle(DeviceManager.capFirstLetter(Common.UAEVALUACION.eq(i).text()));
                data.setSubtitle(Common.SUBUAEVALUACION.eq(i).text());
                rowData.add(data);
            }
        }
        list = (ListView) findViewById(R.id.evlist);
        adapter = new ItemListAdapter(this, rowData);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                startActivity(new Intent(HomePage.this, EvaluacionActivity.class));
            }
        });
        justifyListViewHeightBasedOnChildren(list);
    }

    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
            totalHeight += 10;
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }

}
