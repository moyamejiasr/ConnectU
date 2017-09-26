package com.onelio.connectu.Activities.Apps.Anuncios;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.onelio.connectu.API.AnunciosRequest;
import com.onelio.connectu.Containers.AnuncioData;
import com.onelio.connectu.Helpers.ColorHelper;
import com.onelio.connectu.Helpers.TimeParserHelper;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;

public class AnunciosViewActivity extends AppCompatActivity {

    AnuncioData data;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        final Gson gson = new Gson();
        final String content = getIntent().getStringExtra("JDATA");
        boolean needsLoad = getIntent().getBooleanExtra("LOAD", false);
        setContentView(R.layout.activity_anuncios_view);
        //Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (needsLoad) {
            setProgress();
            progress.show();
            final AnunciosRequest request = new AnunciosRequest(this);
            request.loadAnuncio(content, new AnunciosRequest.AnunciosCallback() {
                @Override
                public void onResult(final boolean onResult, final String message) {
                    AnunciosViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.hide();
                            if (onResult) {
                                data = gson.fromJson(request.getAnuncio(0).toString(), AnuncioData.class);
                                initializeControls();
                            } else {
                                ErrorManager error = new ErrorManager(getBaseContext());
                                if (!error.handleError(message)) {
                                    Toast.makeText(getBaseContext(), getString(R.string.error_unkown_response_format), Toast.LENGTH_SHORT).show();
                                }
                                AnunciosViewActivity.super.onBackPressed();
                            }
                        }
                    });
                }
            });
        } else {
            data = gson.fromJson(content, AnuncioData.class);
            initializeControls();
        }
    }

    public void setProgress() {
        progress = new ProgressDialog(this);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setMessage(getString(R.string.alert_loading));
        progress.setIndeterminate(true);
        progress.setCanceledOnTouchOutside(false);
    }

    public void initializeControls() {
        setData();
        AnunciosRequest request = new AnunciosRequest(this);
        request.markRead(data.getId(), new AnunciosRequest.AnunciosCallback() {
            @Override
            public void onResult(boolean onResult, String message) {

            }
        });
    }

    void setData() {
        TextView title = (TextView) findViewById(R.id.a_title_view);
        TextView type = (TextView) findViewById(R.id.a_type_view);
        TextView subject = (TextView) findViewById(R.id.a_location_view);
        TextView time = (TextView) findViewById(R.id.a_time_view);
        TextView text = (TextView) findViewById(R.id.htexto);
        TextView teacher = (TextView) findViewById(R.id.profesor);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(Color.parseColor(ColorHelper.getColor(data.getType().charAt(0))));
            getWindow().setStatusBarColor(Color.parseColor(ColorHelper.getColor(data.getType().charAt(0))));
        }

        title.setText(data.getTitle());
        type.setText(data.getType());
        subject.setText(data.getSubject());
        text.setText((CharSequence) Html.fromHtml(Uri.parse(data.getText()).toString()));
        text.setMovementMethod(LinkMovementMethod.getInstance());
        teacher.setText(data.getTeacher());

        String ndate = TimeParserHelper.parseTimeDate(getBaseContext(), data.getDate(true));
        if (ndate.length() != 0) {
            time.setText(ndate);
        } else {
            time.setText(data.getDate());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.anuncios_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        AnunciosRequest request = new AnunciosRequest(this);
        if (id == R.id.action_markRead) {
            request.markRead(data.getId(), listener);
            return super.onOptionsItemSelected(item);
        } else if (id == R.id.action_markUnread) {
            request.markUnread(data.getId(), listener);
            return super.onOptionsItemSelected(item);
        } else {
            super.onBackPressed();
            return true;
        }

    }

    AnunciosRequest.AnunciosCallback listener = new AnunciosRequest.AnunciosCallback() {
        @Override
        public void onResult(final boolean onResult, final String message) {
            AnunciosViewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (onResult && message.equals("True")) {
                        Toast.makeText(getBaseContext(), getString(R.string.message_marked), Toast.LENGTH_SHORT).show();
                    } else {
                        ErrorManager error = new ErrorManager(getBaseContext());
                        if (!error.handleError(message)) {
                            Toast.makeText(getBaseContext(), getString(R.string.error_unkown_response_format), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }
    };

}
