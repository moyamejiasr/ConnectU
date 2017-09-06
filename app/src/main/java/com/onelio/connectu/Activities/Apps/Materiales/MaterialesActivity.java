package com.onelio.connectu.Activities.Apps.Materiales;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.MaterialesRequest;
import com.onelio.connectu.Adapters.ViewMaterialesPagerAdapter;
import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.AcademicYear;
import com.onelio.connectu.Fragments.MaterialesAllMenuFragment;
import com.onelio.connectu.Fragments.MaterialesMenuFragment;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

public class MaterialesActivity extends AppCompatActivity {

    private App app;
    private MaterialesRequest request;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView title;
    private AVLoadingIndicatorView progress;

    private ListAdapter yearAdapter;
    private int year = -1;

    public static OnBackPressed mOnBackPressed;

    public interface OnBackPressed {
        boolean shouldLeave();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materiales);
        //Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
        }
        app = (App) this.getApplication();
        //SetView
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        progress = (AVLoadingIndicatorView) findViewById(R.id.material_progress);
        title = (TextView) findViewById(R.id.toolbar_title);
        request = new MaterialesRequest(this);
        request.loginService(new MaterialesRequest.MaterialsCallback() {
            @Override
            public void onResult(final boolean onResult, final String message) {
                MaterialesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onResult) {
                            setYearAdapters();
                            year = app.getPublicPreferenceI(Common.GLOBAL_FILTER_YEAR);
                            updateYear(true, app.getPublicPreferenceI(Common.GLOBAL_FILTER_YEAR));
                        } else {
                            ErrorManager error = new ErrorManager(getBaseContext());
                            if (!error.handleError(message)) {
                                FirebaseCrash.log("Error found trying to login in Materials");
                                FirebaseCrash.report(new Exception(message));
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (tabLayout.getSelectedTabPosition() == 0) {
            if (mOnBackPressed == null || mOnBackPressed.shouldLeave()) {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        /*request.logoutService(new MaterialesRequest.MaterialsCallback() {
            @Override
            public void onResult(boolean onResult, String message) {
            }
        });*/
        super.onDestroy();
    }

    private void setYearAdapters() {
        List<String> years = new ArrayList<>();
        for (AcademicYear year : app.academicYears) {
            years.add(year.getYear());
        }
        yearAdapter = new ArrayAdapter<>(this, R.layout.view_dialog_select, years);
    }

    private void updateYear(boolean isFirst, final int year) {
        this.year = -1;
        progress.show();
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        title.setText(getString(R.string.title_activity_materiales) + " (" + app.academicYears.get(year).getYear() + ")");
        request.changeDate(app.academicYears.get(year).getYear(), isFirst, new MaterialesRequest.MaterialsCallback() {
            @Override
            public void onResult(final boolean onResult, final String message) {
                MaterialesActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onResult) {
                            //Success
                            MaterialesActivity.this.year = year;
                            setupViewPager(viewPager);
                            tabLayout.setVisibility(View.VISIBLE);
                            viewPager.setVisibility(View.VISIBLE);
                            progress.hide();
                        }
                    }
                });
            }
        });
    }

    public void onYearClick(View v) {
        //Select Year
        if (year != -1) { //Handle loading cases
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
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.error_year_loading), Toast.LENGTH_SHORT).show();
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
            //Select Year
            if (year != -1) { //Handle loading cases
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
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.error_year_loading), Toast.LENGTH_SHORT).show();
            }
            return super.onOptionsItemSelected(item);
        } else {
            onBackPressed();
            return true;
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewMaterialesPagerAdapter adapter = new ViewMaterialesPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MaterialesMenuFragment(), getString(R.string.title_fragment_files_all));
        adapter.addFrag(new MaterialesAllMenuFragment(), getString(R.string.title_fragment_files_recent));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

}
