package com.onelio.connectu.Apps.Horario;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.onelio.connectu.API.UAWebService;
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

public class HorarioActivity extends AppCompatActivity {

    WeekView mWeekView;
    Calendar time = Calendar.getInstance();
    ArrayList<WeekViewEvent> mEvents = new ArrayList<>();
    boolean notifyon = false;
    JSONArray doc;
    ProgressDialog dialog;
    boolean first = true;
    ArrayList<String> added = new ArrayList();
    ArrayList<WeekViewEvent> updated = new ArrayList();
    ArrayList<Integer> checked = new ArrayList<>();
    int checkedReally;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Mi Horario");
        mWeekView = (WeekView) findViewById(R.id.weekView);
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.loading_waith));
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        checked.add(1); //CDOC
        checked.add(1); //CEVA
        checked.add(1); //CEXA
        checked.add(0); //CFEST
        checkedReally = 3;

        MonthLoader.MonthChangeListener mMonthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                //First time month??
                String actual =  String.valueOf(newMonth) + String.valueOf(newYear);
                boolean found = false;
                for (int tc = 0; tc < added.size(); tc++) {
                    if (added.get(tc).contains(actual)) {
                        found = true;
                        break;
                    }
                }
                if (!found && (notifyon || first)) { //Get all if not exist month
                    first = false;
                    added.add(actual);
                    getAllEvents(newMonth, newYear);
                    dialog.show();
                }
                ArrayList<WeekViewEvent> data = new ArrayList<>();
                if (mEvents.size() > 0 && notifyon) {
                    for(int c = 0; c < mEvents.size(); c++) {
                        data.add(mEvents.get(c));
                        notifyon = false;
                    }
                    Toast.makeText(getBaseContext(), String.valueOf(mEvents.size())+ " eventos añadidos!", Toast.LENGTH_LONG).show();
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
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.horario, menu);
        MenuItem cdoc = menu.findItem(R.id.cdoc);
        cdoc.setChecked(true);
        MenuItem ceva = menu.findItem(R.id.ceva);
        ceva.setChecked(true);
        MenuItem cexa = menu.findItem(R.id.cexa);
        cexa.setChecked(true);
        MenuItem cfest = menu.findItem(R.id.cfest);
        cfest.setChecked(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setChecked(!item.isChecked());
        SharedPreferences settings = getSharedPreferences("settings", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("checkbox", item.isChecked());
        editor.commit();

        added.clear();
        mEvents.clear();
        first = true;
        notifyon = false;
        request = 0;
        actuallyCompleted = 0;
        doc = new JSONArray();

        if (id == R.id.cdoc && item.isChecked()) {
            checked.set(0, 1);
            checkedReally++;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.cdoc && !item.isChecked()) {
            checked.set(0, 0);
            checkedReally--;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.ceva && item.isChecked()) {
            checked.set(1, 1);
            checkedReally++;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.ceva && !item.isChecked()) {
            checked.set(1, 0);
            checkedReally--;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.cexa && item.isChecked()) {
            checked.set(2, 1);
            checkedReally++;
        } else if (id == R.id.cexa && !item.isChecked()) {
            checked.set(2, 0);
            checkedReally--;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.cfest && item.isChecked()) {
            checked.set(3, 1);
            checkedReally++;
            mWeekView.notifyDatasetChanged();
        } else if (id == R.id.cfest && !item.isChecked()) {
            checked.set(3, 0);
            checkedReally--;
            mWeekView.notifyDatasetChanged();
        } else {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void getAllEvents(int month, int year) {
        //Define range
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 1);
        start.set(Calendar.MONTH, month - 1);
        start.set(Calendar.YEAR, year);
        Calendar end = Calendar.getInstance();
        end.set(Calendar.DATE, end.getActualMaximum(Calendar.DATE));
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MONTH, month);
        end.set(Calendar.YEAR, year);
        //Colors:
        //DOC = 2196F3
        //EXA = E65100

        for (int i = 0; i < 4; i++) {
            String finalPart = "&start=" + String.valueOf(start.getTimeInMillis()/1000) + "&end=" + String.valueOf(end.getTimeInMillis()/1000);
            String url;
            String color;
            if (i == 0 && checked.get(i) == 1) { //CDOC
                url = UAWebService.CAL_DOC;
                color = "#2196F3";
                onNavigate(url + finalPart, color);
            } else if (i == 1 && checked.get(i) == 1) { //CEVA
                url = UAWebService.CAL_EVA;
                color = "#00E676";
                onNavigate(url + finalPart, color);
            } else if (i == 2 && checked.get(i) == 1) { //CEXA
                url = UAWebService.CAL_EXA;
                color = "#E65100";
                onNavigate(url + finalPart, color);
            } else if (i == 3 && checked.get(i) == 1) { //CFEST
                url = UAWebService.CAL_FEST;
                color = "#009688";
                onNavigate(url + finalPart, color);
            }
        }
    }

    int actuallyCompleted = 0;
    long request = 0;

    public void onNavigate(String url, final String color) {
        UAWebService.HttpWebGetRequest(HorarioActivity.this, url , new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    try {
                        doc = new JSONArray(body);
                        for(int c = 0; c < doc.length(); c++) {
                            WeekViewEvent wevent = new WeekViewEvent();
                            JSONObject event = doc.getJSONObject(c);
                            wevent.setId(c+request);
                            wevent.setName(event.getString("title") + " - " + event.getString("uaCacDescac"));
                            wevent.setColor(Color.parseColor(color));
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
                            mEvents.add(wevent);
                        }
                        request += doc.length();
                        actuallyCompleted++;
                        if (actuallyCompleted == checkedReally * 1) {
                            //Got to the final part
                            actuallyCompleted = 0;
                            HorarioActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    notifyon = true;
                                    mWeekView.notifyDatasetChanged();
                                    dialog.cancel();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
