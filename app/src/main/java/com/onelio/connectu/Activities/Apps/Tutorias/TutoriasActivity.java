package com.onelio.connectu.Activities.Apps.Tutorias;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.TutoriasRequest;
import com.onelio.connectu.Activities.Apps.Profesores.ProfesoresActivity;
import com.onelio.connectu.Adapters.ViewMaterialesPagerAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Fragments.TutoriasAllMenuFragment;
import com.onelio.connectu.Fragments.TutoriasMenuFragment;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;
import com.wang.avi.AVLoadingIndicatorView;
import java.util.ArrayList;
import java.util.List;

public class TutoriasActivity extends AppCompatActivity {

  private App app;
  private TutoriasRequest request;

  private TabLayout tabLayout;
  private ViewPager viewPager;
  private FloatingActionButton fabTut;
  private TextView title;
  private AVLoadingIndicatorView progress;

  private ListAdapter yearAdapter;
  private int year = -1;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    setContentView(R.layout.activity_tutorias);
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
    viewPager = (ViewPager) findViewById(R.id.viewpager);
    tabLayout = (TabLayout) findViewById(R.id.tabs);
    progress = (AVLoadingIndicatorView) findViewById(R.id.material_progress);
    title = (TextView) findViewById(R.id.toolbar_title);
    fabTut = (FloatingActionButton) findViewById(R.id.fabTut);
    setYearAdapters();
    request = new TutoriasRequest(this);
    updateYear(true, app.getPublicPreferenceI(Common.GLOBAL_FILTER_YEAR));
    doRequest();
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

  private void updateYear(boolean isFirst, int year) {
    title.setText(
        getString(R.string.title_activity_tutorias)
            + " ("
            + app.academicYears.get(year).getYear()
            + ")");
    request.setYear(year);
    this.year = year;
    if (!isFirst) {
      progress.show();
      tabLayout.setVisibility(View.GONE);
      viewPager.setVisibility(View.GONE);
      fabTut.setVisibility(View.GONE);
      doRequest();
    }
  }

  private void doRequest() {
    request.loginService(
        new TutoriasRequest.TutoriasCallback() {
          @Override
          public void onResult(final boolean onResult, final String message) {
            TutoriasActivity.this.runOnUiThread(
                new Runnable() {
                  @Override
                  public void run() {
                    if (onResult) {
                      setupViewPager(viewPager);
                      tabLayout.setVisibility(View.VISIBLE);
                      viewPager.setVisibility(View.VISIBLE);
                      fabTut.setVisibility(View.VISIBLE);
                      progress.hide();
                    } else {
                      ErrorManager error = new ErrorManager(getBaseContext());
                      if (!error.handleError(message)) {
                        FirebaseCrash.log("Error found trying to update in Tutorias");
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
    // Select Year
    if (year != -1) { // Handle loading cases
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

  public void onAddTutoria(View v) {
    Intent intent = new Intent(this, ProfesoresActivity.class);
    if (year != -1) {
      startActivity(intent);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.materiales, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_year) {
      // Select Year
      if (year != -1) { // Handle loading cases
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
      } else {
        Toast.makeText(getBaseContext(), getString(R.string.error_year_loading), Toast.LENGTH_SHORT)
            .show();
      }
      return super.onOptionsItemSelected(item);
    } else {
      onBackPressed();
      return true;
    }
  }

  private void setupViewPager(ViewPager viewPager) {
    ViewMaterialesPagerAdapter adapter =
        new ViewMaterialesPagerAdapter(getSupportFragmentManager());
    adapter.addFrag(new TutoriasMenuFragment(), getString(R.string.title_fragment_tutorias_recent));
    adapter.addFrag(new TutoriasAllMenuFragment(), getString(R.string.title_fragment_tutorias_all));
    viewPager.setAdapter(adapter);
    tabLayout.setupWithViewPager(viewPager);
  }
}
