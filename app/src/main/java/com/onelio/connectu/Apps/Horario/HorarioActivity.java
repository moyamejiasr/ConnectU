package com.onelio.connectu.Apps.Horario;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Common;
import com.onelio.connectu.Database.RealmManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HorarioActivity extends AppCompatActivity {

    WeekView mWeekView;
    Calendar time = Calendar.getInstance();
    ArrayList<Integer> checked = new ArrayList<>();
    int checkedReally;

    //New
    JSONArray jdata;

    void getAllMatch() {
        RealmManager realm = new RealmManager(this);
        if (realm.onOptionExist("cdoc")) {
            int cdoc = Integer.valueOf(realm.getOption("cdoc"));
            int ceva = Integer.valueOf(realm.getOption("ceva"));
            int cexa = Integer.valueOf(realm.getOption("cexa"));
            int cfest = Integer.valueOf(realm.getOption("cfest"));
            checked.add(cdoc);
            checked.add(ceva);
            checked.add(cexa);
            checked.add(cfest);
            checkedReally = cdoc + ceva + cexa + cfest;
        } else {
            realm.createOption("cdoc", "1");
            realm.createOption("ceva", "1");
            realm.createOption("cexa", "1");
            realm.createOption("cfest", "0");
            checked.add(1); //CDOC
            checked.add(1); //CEVA
            checked.add(1); //CEXA
            checked.add(0); //CFEST
            checkedReally = 3;
        }
        realm.deleteRealmInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Mi Horario");
        mWeekView = (WeekView) findViewById(R.id.weekView);

        //Get Options
        getAllMatch();
        try {
            jdata  = Common.data.getJSONArray("calendar");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                //First time month??
                ArrayList<WeekViewEvent> data = new ArrayList<>();
                for (int i = 0; i < jdata.length(); i++) {
                    try {
                        JSONObject event = jdata.getJSONObject(i);
                        WeekViewEvent wevent = new WeekViewEvent();
                        wevent.setName(event.getString("title") + " - " + event.getString("uaCacDescac"));
                        String type = event.getString("uaCalendario");
                        wevent.setLocation(event.getString("uaIdAula"));
                        //Start
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");
                        Date date = sdf.parse(event.getString("start").replace("T", " "));
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        wevent.setStartTime(calendar);
                        //End
                        Date date1 = sdf.parse(event.getString("end").replace("T", " "));
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(date1);
                        wevent.setEndTime(calendar1);

                        if (calendar.get(Calendar.MONTH) + 1 == newMonth) {
                            if (type.equals("docenciaalu")  && checked.get(0) == 1) {
                                wevent.setColor(Color.parseColor("#2196F3"));
                                data.add(wevent);
                            }
                            if (type.equals("evaluaalu") && checked.get(1) == 1) {
                                wevent.setColor(Color.parseColor("#E91E63"));
                                data.add(wevent);
                            }
                            if (type.equals("examenesalu") && checked.get(2) == 1) {
                                wevent.setColor(Color.parseColor("#F44336"));
                                data.add(wevent);
                            }
                            if (type.equals("festivos") && checked.get(3) == 1) {
                                wevent.setColor(Color.parseColor("#FFEB3B"));
                                data.add(wevent);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return data;
            }
        };

        mWeekView.setMonthChangeListener(mMonthChangeListener);
        mWeekView.goToHour(time.get(Calendar.HOUR_OF_DAY));
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                //Event action
                //String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                //startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.horario, menu);
        MenuItem cdoc = menu.findItem(R.id.cdoc);
        MenuItem ceva = menu.findItem(R.id.ceva);
        MenuItem cexa = menu.findItem(R.id.cexa);
        MenuItem cfest = menu.findItem(R.id.cfest);

        if (checked.get(0) == 1) { cdoc.setChecked(true); }
        if (checked.get(1) == 1) { ceva.setChecked(true); }
        if (checked.get(2) == 1) { cexa.setChecked(true); }
        if (checked.get(3) == 1) { cfest.setChecked(true); }

        return true;
    }

    void modifyMatch(String name, int value) {
        RealmManager realm = new RealmManager(this);
        realm.modifyOption(name, String.valueOf(value));
        realm.deleteRealmInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(!item.isChecked());
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("checkbox", item.isChecked());
        editor.commit();

        if (id == R.id.cdoc && item.isChecked()) {
            checked.set(0, 1);
            modifyMatch("cdoc", 1);
            checkedReally++;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.cdoc && !item.isChecked()) {
            checked.set(0, 0);
            modifyMatch("cdoc", 0);
            checkedReally--;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.ceva && item.isChecked()) {
            checked.set(1, 1);
            modifyMatch("ceva", 1);
            checkedReally++;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.ceva && !item.isChecked()) {
            checked.set(1, 0);
            modifyMatch("ceva", 0);
            checkedReally--;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.cexa && item.isChecked()) {
            checked.set(2, 1);
            modifyMatch("cexa", 1);
            checkedReally++;
        } else if (id == R.id.cexa && !item.isChecked()) {
            checked.set(2, 0);
            modifyMatch("cexa", 0);
            checkedReally--;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.cfest && item.isChecked()) {
            checked.set(3, 1);
            modifyMatch("cfest", 1);
            checkedReally++;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.cfest && !item.isChecked()) {
            checked.set(3, 0);
            modifyMatch("cfest", 0);
            checkedReally--;
            mWeekView.notifyDatasetChanged();
        } else {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
