package com.onelio.connectu.Activities.Apps.Profesores;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
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
import android.widget.TextView;
import com.google.gson.Gson;
import com.onelio.connectu.Adapters.ProfesoresAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Containers.SubjectData;
import com.onelio.connectu.Containers.TeacherData;
import com.onelio.connectu.Helpers.ObjectHelper;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.R;
import java.util.ArrayList;
import java.util.List;

public class ProfesoresActivity extends AppCompatActivity {
  App app;

  // RecyclerView
  private RecyclerView recyclerView;
  private RecyclerView.Adapter mAdapter;
  private LinearLayoutManager mLayoutManager;
  private LinearLayout emptyView;
  TextView title;
  List<TeacherData> teachers;
  List<SubjectData> subjects;

  ListAdapter yearAdapter;
  ListAdapter subjectAdapter;

  int year = 0;
  String subjectId = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    setContentView(R.layout.activity_profesores);
    // Action bar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
    }
    app = (App) this.getApplication();
    year = app.getPublicPreferenceI(Common.GLOBAL_FILTER_YEAR);

    recyclerView = (RecyclerView) findViewById(R.id.profesoresRecycler);
    emptyView = (LinearLayout) findViewById(R.id.profesores_blank);
    recyclerView.setHasFixedSize(true);
    mLayoutManager = new LinearLayoutManager(getBaseContext());
    recyclerView.setLayoutManager(mLayoutManager);

    setYearAdapters();
    setSubjectsAdapter();
    updateYear(year);
  }

  private void setYearAdapters() {
    List<String> years = new ArrayList<>();
    for (AcademicYear year : app.academicYears) {
      years.add(year.getYear());
    }
    yearAdapter = new ArrayAdapter<>(this, R.layout.view_dialog_select, years);
  }

  private void setSubjectsAdapter() {
    List<String> subjects = ObjectHelper.getSubjectsName(getBaseContext(), year, true);
    subjectAdapter = new ArrayAdapter<>(this, R.layout.view_dialog_select, subjects);
  }

  private void updateYear(int year) {
    title = (TextView) findViewById(R.id.toolbar_title);
    this.year = year;
    title.setText(
        getString(R.string.title_activity_profesores)
            + " ("
            + app.academicYears.get(year).getYear()
            + ")");
    subjectId = "";
    setSubjectsAdapter();
    loadProfesores();
  }

  private void filterSubject(int subject) {
    this.subjectId = app.academicYears.get(year).getSubjectsData().get(subject).getId();
    loadProfesores();
  }

  public void onYearClick(View v) {
    // Select Year
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
            updateYear(which);
          }
        });
    alert.show();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.profesores, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_year) {
      // Select Year
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
              updateYear(which);
            }
          });
      alert.show();
      return super.onOptionsItemSelected(item);
    } else if (id == R.id.action_subject) {
      // Select Subject
      final AlertManager alert = new AlertManager(this);
      alert.setIcon(R.drawable.ic_filter_black_24dp);
      alert.setMessage(getString(R.string.subject));
      alert.setNegativeButton(
          "CANCEL",
          new AlertManager.AlertCallBack() {
            @Override
            public void onClick(boolean isPositive) {
              alert.cancel();
            }
          });
      alert.setAdapter(
          subjectAdapter,
          new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if (which == 0) {
                subjectId = "";
                loadProfesores();
              } else {
                filterSubject(which - 1);
              }
            }
          });
      alert.show();
      return super.onOptionsItemSelected(item);
    } else {
      super.onBackPressed();
      return true;
    }
  }

  private void loadProfesores() { // subjectId = "" means all
    subjects = app.academicYears.get(year).getSubjectsData();
    teachers = new ArrayList<>();
    for (SubjectData subject : subjects) {
      if (!subjectId.isEmpty()) {
        if (subject.getId().equals(subjectId)) {
          teachers = subject.getTeachers();
        }
      } else {
        for (TeacherData teacher : subject.getTeachers()) {
          teachers.add(teacher);
        }
      }
    }

    mAdapter = new ProfesoresAdapter(getBaseContext(), onClick, teachers, subjects);
    recyclerView.setAdapter(mAdapter);
    if (teachers.isEmpty()) {
      recyclerView.setVisibility(View.GONE);
      emptyView.setVisibility(View.VISIBLE);
    } else {
      recyclerView.setVisibility(View.VISIBLE);
      emptyView.setVisibility(View.GONE);
    }
  }

  ProfesoresAdapter.OnItemClickListener onClick =
      new ProfesoresAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item, ProfesoresAdapter.ViewHolder view) {

          View imageView = view.profile;
          Intent intent = new Intent(ProfesoresActivity.this, ProfesoresViewActivity.class);
          Gson gson = new Gson();
          intent.putExtra("JDATA", gson.toJson(teachers.get(item)));
          overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Pair<View, String> pair1 =
                Pair.create(imageView, getString(R.string.activity_profesores_image_trans));
            ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(ProfesoresActivity.this, pair1);
            startActivity(intent, options.toBundle());
          } else {
            startActivity(intent);
          }
        }
      };
}
