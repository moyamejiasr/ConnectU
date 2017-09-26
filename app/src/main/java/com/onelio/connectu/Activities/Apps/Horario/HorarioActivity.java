package com.onelio.connectu.Activities.Apps.Horario;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.github.badoualy.datepicker.DatePickerTimeline;
import com.onelio.connectu.API.HorarioRequest;
import com.onelio.connectu.Adapters.HorarioAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HorarioActivity extends AppCompatActivity {

    //Data
    List<CalendarEvent> events;
    App app;
    DatePickerTimeline calendar;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_horario);
        //Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
        }
        app = (App) getApplication();
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.horarioRecycler);
        emptyView = (LinearLayout) findViewById(R.id.horario_blank);
        recyclerView.setLayoutManager(mLayoutManager);

        initializePicker();
        getDateData(new Date());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.horario, menu);
        MenuItem cdoc = menu.findItem(R.id.action_docente);
        MenuItem ceva = menu.findItem(R.id.action_evaluacion);
        MenuItem cexa = menu.findItem(R.id.action_examenes);
        MenuItem cfest = menu.findItem(R.id.action_festivo);

        //Try to get filter option or use default true
        cdoc.setChecked(app.getPublicPreferenceB(Common.HORARIO_FILTER_DOCENCIA));
        ceva.setChecked(app.getPublicPreferenceB(Common.HORARIO_FILTER_EVALUACION));
        cexa.setChecked(app.getPublicPreferenceB(Common.HORARIO_FILTER_EXAMENES));
        cfest.setChecked(app.getPublicPreferenceB(Common.HORARIO_FILTER_FESTIVO));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Date date = new Date(calendar.getSelectedYear() - 1900, calendar.getSelectedMonth(), calendar.getSelectedDay()); //Fix 1900 start
        if (id == R.id.action_docente) {
            //Calendario Docente
            item.setChecked(!item.isChecked());
            app.savePublicPreference(Common.HORARIO_FILTER_DOCENCIA, item.isChecked());
            getDateData(date);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.action_evaluacion) {
            //Calendario Evaluacion
            item.setChecked(!item.isChecked());
            app.savePublicPreference(Common.HORARIO_FILTER_EVALUACION, item.isChecked());
            getDateData(date);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.action_examenes) {
            //Calendario Examenes
            item.setChecked(!item.isChecked());
            app.savePublicPreference(Common.HORARIO_FILTER_EXAMENES, item.isChecked());
            getDateData(date);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.action_festivo) {
            //Calendario Festivo
            item.setChecked(!item.isChecked());
            app.savePublicPreference(Common.HORARIO_FILTER_FESTIVO, item.isChecked());
            getDateData(date);
            return super.onOptionsItemSelected(item);
        } else {
            super.onBackPressed();
            return true;
        }

    }

    private void initializePicker() {
        /* end after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.setTimeInMillis(app.lastUpdateTime);
        endDate.add(Calendar.MONTH, 2);
        /* start before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.setTimeInMillis(app.lastUpdateTime);
        /* today **/
        Calendar today = Calendar.getInstance();

        calendar = (DatePickerTimeline) findViewById(R.id.calendarView);
        calendar.setFirstVisibleDate(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
        calendar.setLastVisibleDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
        calendar.setSelectedDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        //Set monthView size to 0
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) calendar.getMonthView().getLayoutParams();
        params.height = 0;
        calendar.getMonthView().setLayoutParams(params);
        //Set callback selector
        calendar.setOnDateSelectedListener(new DatePickerTimeline.OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int index) {
                Date date = new Date(year - 1900, month, day); //Fix 1900 start
                getDateData(date);
            }
        });
    }

    private void getDateData(Date date) {
        HorarioRequest request = new HorarioRequest(getBaseContext());
        events = request.getDateEvents(date);
        mAdapter = new HorarioAdapter(getBaseContext(), onClick, events);
        recyclerView.setAdapter(mAdapter);
        if (events.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    HorarioAdapter.OnItemClickListener onClick = new HorarioAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item) {
            HorarioDialog dialog = new HorarioDialog(HorarioActivity.this, events.get(item));
            dialog.show();
        }
    };

}
