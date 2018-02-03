package com.onelio.connectu.Activities.Apps.Notas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.EvaluacionRequest;
import com.onelio.connectu.API.NotasRequest;
import com.onelio.connectu.Activities.Apps.Evaluacion.EvaluacionActivity;
import com.onelio.connectu.Activities.Apps.Evaluacion.EvaluacionFilterDialog;
import com.onelio.connectu.Activities.Apps.Evaluacion.EvaluacionViewDialog;
import com.onelio.connectu.Adapters.EvaluacionAdapter;
import com.onelio.connectu.Adapters.NotasAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Containers.EvaluacionData;
import com.onelio.connectu.Containers.NotaData;
import com.onelio.connectu.Helpers.ObjectHelper;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;

import java.util.ArrayList;
import java.util.List;

public class NotasActivity extends AppCompatActivity {

    private App app;
    private NotasRequest request;
    private TextView title;

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout emptyView;
    private ProgressBar progress;
    List<NotaData> events;
    SwipeRefreshLayout layout;

    private ListAdapter yearAdapter;
    ListAdapter subjectAdapter;
    private int year = 0;
    private boolean isUpdating = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_notas);
        //Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkBlue));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkBlue));
        }
        app = (App) this.getApplication();
        //SetView
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.evaluacion_notas_Recycler);
        emptyView = (LinearLayout) findViewById(R.id.evaluacion_notas_blank);
        recyclerView.setLayoutManager(mLayoutManager);
        progress = (ProgressBar)findViewById(R.id.evaluacion_notas_progressBar);
        title = (TextView) findViewById(R.id.toolbar_title);
        setYearAdapters();
        setSubjectsAdapter();
        layout = (SwipeRefreshLayout)findViewById(R.id.evaluacion_notas_Swipe);
        layout.setOnRefreshListener(onRefresh);
        request = new NotasRequest(this);
        //Default request filters initialization
        updateYear(true, app.getPublicPreferenceI(Common.GLOBAL_FILTER_YEAR)); //Saved year
        //Do first request
        loginIn();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notas, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_year) {
            //Select Year
            if (!isUpdating) { //Handle loading cases
                final AlertManager alert = new AlertManager(this);
                alert.setIcon(R.drawable.ic_filter_black_24dp);
                alert.setMessage(getString(R.string.year));
                alert.setNegativeButton("CANCEL", new AlertManager.AlertCallBack() {
                    @Override
                    public void onClick(boolean isPositive) {
                        alert.cancel();
                    }
                });
                alert.setAdapter(yearAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateYear(false, which);
                    }
                });
                alert.show();
            }
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.action_subject) {
            onSubjectClick(null);
            return super.onOptionsItemSelected(item);
        } else {
            onBackPressed();
            return true;
        }

    }

    private void setYearAdapters() {
        List<String> years = new ArrayList<>();
        if (app.academicYears == null) {
            ErrorManager error = new ErrorManager(getBaseContext());
            error.handleError(ErrorManager.UNABLE_DISPLAY);
            onBackPressed();
        }
        for (AcademicYear year : app.academicYears) {
            years.add(year.getYear());
        }
        yearAdapter = new ArrayAdapter<>(this, R.layout.view_dialog_select, years);
    }

    private void setSubjectsAdapter() {
        List<String> subjects = ObjectHelper.getSubjectsName(getBaseContext(), year, false);
        subjectAdapter = new ArrayAdapter<>(this, R.layout.view_dialog_select, subjects);
    }

    private void loginIn() {
        request.loginService(new NotasRequest.NotasCallback() {
            @Override
            public void onResult(final boolean onResult, final String message) {
                NotasActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onResult) {
                            progress.setVisibility(View.INVISIBLE);
                            isUpdating = false;
                            onSubjectClick(null);
                        } else {
                            ErrorManager error = new ErrorManager(getBaseContext());
                            if (!error.handleError(message)) {
                                FirebaseCrash.log("Error found trying to login in Evaluacion-Notas");
                                FirebaseCrash.report(new Exception(message));
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void filterSubject(int subject) {
        request.setSubject(subject + 1);
        doRequest();
    }

    private void updateYear(boolean isFirst, int year) {
        title.setText(getString(R.string.title_activity_evaluacion_notas) + " (" + app.academicYears.get(year).getYear() + ")");
        request.setYear(year);
        this.year = year;
        if (!isFirst) {
            setSubjectsAdapter();
            onSubjectClick(null);
        }
    }

    private void doRequest() {
        isUpdating = true;
        progress.setVisibility(View.VISIBLE);
        events = new ArrayList<>();
        mAdapter = new NotasAdapter(getBaseContext(), events);
        recyclerView.setAdapter(mAdapter);
        emptyView.setVisibility(View.GONE);
        request.fetchGrades(new NotasRequest.NotasCallback() {
            @Override
            public void onResult(final boolean onResult, final String message) {
                NotasActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        isUpdating = false;
                        layout.setRefreshing(false);
                        if (onResult) {
                            events = request.getGrades();
                            mAdapter = new NotasAdapter(getBaseContext(), events);
                            recyclerView.setAdapter(mAdapter);
                            if (events.isEmpty()) {
                                emptyView.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            ErrorManager error = new ErrorManager(getBaseContext());
                            if (!error.handleError(message)) {
                                FirebaseCrash.log("Error found trying to update in Evaluacion");
                                FirebaseCrash.report(new Exception(message));
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    public void onYearClick(View v) {
        //Select Year
        if (year != -1 && !isUpdating) { //Handle loading cases
            final AlertManager alert = new AlertManager(this);
            alert.setIcon(R.drawable.ic_filter_black_24dp);
            alert.setMessage(getString(R.string.year));
            alert.setNegativeButton("CANCEL", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    alert.cancel();
                }
            });
            alert.setAdapter(yearAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateYear(false, which);
                }
            });
            alert.show();
        }
    }

    public void onSubjectClick(View v) {
        //Select Year
        if (!isUpdating) { //Handle loading cases
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            //Select Subject
            final AlertManager alert = new AlertManager(this);
            alert.setIcon(R.drawable.ic_filter_black_24dp);
            alert.setMessage(getString(R.string.subject));
            alert.setNegativeButton("CANCEL", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    alert.cancel();
                }
            });
            alert.setAdapter(subjectAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    filterSubject(which);
                }
            });
            try {
                alert.show();
            }catch (WindowManager.BadTokenException e) {
                //Prevent crash when activity closed
            }
        }
    }

    SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!isUpdating) {
                doRequest();
            } else {
                layout.setRefreshing(false);
            }
        }
    };

}
