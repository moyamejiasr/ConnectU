package com.onelio.connectu.Activities.Apps.Anuncios;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.AnunciosRequest;
import com.onelio.connectu.Adapters.AnunciosAdapter;
import com.onelio.connectu.Helpers.AnimTransHelper;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;

public class AnunciosActivity extends AppCompatActivity {

    //RecyclerView
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout emptyView;
    JSONArray jdata;
    SwipeRefreshLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);
        //Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkGreen));
        }

        layout = (SwipeRefreshLayout)findViewById(R.id.anunciosSwipe);
        layout.setOnRefreshListener(onRefresh);

        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = (RecyclerView) findViewById(R.id.anunciosRecycler);
        emptyView = (LinearLayout) findViewById(R.id.anuncios_blank);
        recyclerView.setLayoutManager(mLayoutManager);

        reloadAnuncios();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anuncios, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reload) {
            //Do reload
            reloadAnuncios();
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.action_readall) {
            markReadAll();
            return super.onOptionsItemSelected(item);
        } else {
            super.onBackPressed();
            return true;
        }

    }

    private void markReadAll() {
        AnunciosRequest request = new AnunciosRequest(getBaseContext());
        request.markAllRead(new AnunciosRequest.AnunciosCallback() {
            @Override
            public void onResult(final boolean onResult, final String message) {
                AnunciosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onResult && message.equals("True")) {
                            Toast.makeText(getBaseContext(), getString(R.string.message_marked), Toast.LENGTH_SHORT).show();
                            reloadAnuncios();
                        } else {
                            ErrorManager error = new ErrorManager(getBaseContext());
                            if (!error.handleError(message)) {
                                Toast.makeText(getBaseContext(), getString(R.string.error_unkown_response_format), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    private void reloadAnuncios() {
        final ProgressBar progress = (ProgressBar)findViewById(R.id.anuncios_progressBar);
        progress.setVisibility(View.VISIBLE);
        final AnunciosRequest request = new AnunciosRequest(getBaseContext());
        request.loadAnuncios(new AnunciosRequest.AnunciosCallback() {
            @Override
            public void onResult(final boolean onResult, final String message) {
                AnunciosActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        if (onResult) {
                            layout.setRefreshing(false);
                            jdata = request.getAnuncios();
                            mAdapter = new AnunciosAdapter(getBaseContext(), onClick, jdata);
                            recyclerView.setAdapter(mAdapter);
                            if (jdata.length() == 0) {
                                recyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            }
                            else {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                            }
                        } else {
                            ErrorManager error = new ErrorManager(getBaseContext());
                            if (!error.handleError(message)) {
                                FirebaseCrash.report(new Exception("Exception found in AnunciosActivity while reload: " + message));
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });
    }

    SwipeRefreshLayout.OnRefreshListener onRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            reloadAnuncios();
        }
    };

    AnunciosAdapter.OnItemClickListener onClick = new AnunciosAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(int item, View v) {
            try {
                Intent intent = new Intent(AnunciosActivity.this, AnunciosViewActivity.class);
                intent.putExtra("JDATA", jdata.getJSONObject(item).toString());
                startActivity(intent, AnimTransHelper.circleSlideUp(getBaseContext(), v));
            } catch (JSONException e) {
                FirebaseCrash.report(e);
                AlertManager alertManager = new AlertManager(getBaseContext());
                alertManager.setMessage(getString(R.string.error_unexpected), getString(R.string.error_unable_display));
                alertManager.show();
            }
        }
    };

}
