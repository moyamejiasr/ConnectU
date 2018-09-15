package com.onelio.connectu.Activities.Apps.Evaluacion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.EvaluacionRequest;
import com.onelio.connectu.Adapters.EvaluacionAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Containers.EvaluacionData;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;
import java.util.ArrayList;
import java.util.List;

public class EvaluacionActivity extends AppCompatActivity {

  private App app;
  private EvaluacionRequest request;
  private TextView title;

  // RecyclerView
  private RecyclerView recyclerView;
  private RecyclerView.Adapter mAdapter;
  private LinearLayoutManager mLayoutManager;
  private LinearLayout emptyView;
  private ProgressBar progress;
  List<EvaluacionData> events;
  SwipeRefreshLayout layout;

  private ListAdapter yearAdapter;
  private int year = -1;
  private boolean isUpdating = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    setContentView(R.layout.activity_evaluacion);
    // Action bar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
    }
    app = (App) this.getApplication();
    // SetView
    mLayoutManager = new LinearLayoutManager(this);
    mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
    recyclerView = (RecyclerView) findViewById(R.id.evaluacionRecycler);
    emptyView = (LinearLayout) findViewById(R.id.evaluacion_blank);
    recyclerView.setLayoutManager(mLayoutManager);
    progress = (ProgressBar) findViewById(R.id.evaluacion_progressBar);
    title = (TextView) findViewById(R.id.toolbar_title);
    setYearAdapters();
    layout = (SwipeRefreshLayout) findViewById(R.id.evaluacionSwipe);
    layout.setOnRefreshListener(onRefresh);
    request = new EvaluacionRequest(this);
    // Default request filters initialization
    updateYear(true, app.getPublicPreferenceI(Common.GLOBAL_FILTER_YEAR)); // Saved year
    request.setSubject(0); // All
    request.setFilter(2); // P
    // Do first request
    loginIn();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.evaluacion, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_year) {
      // Select Year
      if (year != -1 && !isUpdating) { // Handle loading cases
        final AlertManager alert = new AlertManager(this);
        alert.setIcon(R.drawable.ic_filter_black_24dp);
        alert.setMessage(getString(R.string.year));
        alert.setNegativeButton(
            "CANCEL",
            new AlertManager.AlertCallBack() {
              @Override
              public void onClick(boolean isPositive) {
                alert.cancel();
              }
            });
        alert.setAdapter(
            yearAdapter,
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                updateYear(false, which);
              }
            });
        alert.show();
      }
      return super.onOptionsItemSelected(item);
    } else if (id == R.id.action_filter) {
      EvaluacionFilterDialog dialog = new EvaluacionFilterDialog(this);
      dialog.setOnUpdated(
          new EvaluacionFilterDialog.EvResponse() {
            @Override
            public void onUpdated() {
              EvaluacionActivity.this.runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      doRequest();
                    }
                  });
            }
          });
      dialog.show();
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

  private void loginIn() {
    request.loginService(
        new EvaluacionRequest.EvaluacionCallback() {
          @Override
          public void onResult(final boolean onResult, final String message) {
            EvaluacionActivity.this.runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    if (onResult) {
                      doRequest();
                    } else {
                      ErrorManager error = new ErrorManager(getBaseContext());
                      if (!error.handleError(message)) {
                        FirebaseCrash.log("Error found trying to login in Evaluacion");
                        FirebaseCrash.report(new Exception(message));
                        Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                      }
                    }
                  }
                });
          }
        });
  }

  private void updateYear(boolean isFirst, int year) {
    title.setText(
        getString(R.string.title_activity_evaluacion)
            + " ("
            + app.academicYears.get(year).getYear()
            + ")");
    request.setYear(year);
    this.year = year;
    if (!isFirst) {
      progress.setVisibility(View.VISIBLE);
      doRequest();
    }
  }

  private void doRequest() {
    isUpdating = true;
    progress.setVisibility(View.VISIBLE);
    events = new ArrayList<>();
    mAdapter = new EvaluacionAdapter(getBaseContext(), onClick, events);
    recyclerView.setAdapter(mAdapter);
    emptyView.setVisibility(View.GONE);
    request.fetchEvents(
        new EvaluacionRequest.EvaluacionCallback() {
          @Override
          public void onResult(final boolean onResult, final String message) {
            EvaluacionActivity.this.runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    progress.setVisibility(View.GONE);
                    isUpdating = false;
                    layout.setRefreshing(false);
                    if (onResult) {
                      events = request.getEvents();
                      mAdapter = new EvaluacionAdapter(getBaseContext(), onClick, events);
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

  public void onFilterClick(View v) {
    if (!isUpdating) {
      EvaluacionFilterDialog dialog = new EvaluacionFilterDialog(this);
      dialog.setOnUpdated(
          new EvaluacionFilterDialog.EvResponse() {
            @Override
            public void onUpdated() {
              EvaluacionActivity.this.runOnUiThread(
                  new Runnable() {
                    @Override
                    public void run() {
                      doRequest();
                    }
                  });
            }
          });
      dialog.show();
    }
  }

  public void onYearClick(View v) {
    // Select Year
    if (year != -1 && !isUpdating) { // Handle loading cases
      final AlertManager alert = new AlertManager(this);
      alert.setIcon(R.drawable.ic_filter_black_24dp);
      alert.setMessage(getString(R.string.year));
      alert.setNegativeButton(
          "CANCEL",
          new AlertManager.AlertCallBack() {
            @Override
            public void onClick(boolean isPositive) {
              alert.cancel();
            }
          });
      alert.setAdapter(
          yearAdapter,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              updateYear(false, which);
            }
          });
      alert.show();
    }
  }

  SwipeRefreshLayout.OnRefreshListener onRefresh =
      new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          if (!isUpdating) {
            doRequest();
          } else {
            layout.setRefreshing(false);
          }
        }
      };

  EvaluacionAdapter.OnItemClickListener onClick =
      new EvaluacionAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item) {
          final EvaluacionData data = events.get(item);
          if (!data.getTypeID().equals("9")) {
            final ProgressDialog progress =
                ProgressDialog.show(
                    EvaluacionActivity.this,
                    getString(R.string.app_name),
                    getString(R.string.evaluacion_loading),
                    true);
            request.getFullDataFrom(
                data,
                new EvaluacionRequest.EvaluacionCallback() {
                  @Override
                  public void onResult(final boolean onResult, final String message) {
                    EvaluacionActivity.this.runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {
                            progress.dismiss();
                            if (onResult) {
                              data.setTable(message);
                              EvaluacionViewDialog dialog =
                                  new EvaluacionViewDialog(EvaluacionActivity.this, data);
                              dialog.show();
                            } else {
                              ErrorManager error = new ErrorManager(getBaseContext());
                              if (!error.handleError(message)) {
                                FirebaseCrash.log("Error found trying to update in Evaluacion");
                                FirebaseCrash.report(new Exception(message));
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT)
                                    .show();
                              }
                            }
                          }
                        });
                  }
                });
          }
        }
      };
}
