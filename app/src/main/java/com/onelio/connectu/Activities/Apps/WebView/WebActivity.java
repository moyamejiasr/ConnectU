package com.onelio.connectu.Activities.Apps.WebView;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.onelio.connectu.App;
import com.onelio.connectu.Common;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;

public class WebActivity extends AppCompatActivity {

    WebView webView;
    ProgressBar progress;
    App app;

    String name;
    String url;
    int color;
    boolean needsLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        setContentView(R.layout.activity_web);
        name = getIntent().getExtras().getString(Common.WEBVIEW_EXTRA_NAME);
        url = getIntent().getExtras().getString(Common.WEBVIEW_EXTRA_URL);
        color = getIntent().getExtras().getInt(Common.WEBVIEW_EXTRA_COLOR);
        needsLogin = getIntent().getExtras().getBoolean(Common.WEBVIEW_EXTRA_NLOGIN);
        //Action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(color);
            getWindow().setStatusBarColor(color);
        }
        app = (App) getApplication();

        TextView title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(name);

        progress = (ProgressBar) findViewById(R.id.web_progressBar);
        webView = (WebView) findViewById(R.id.webView);

        if (savedInstanceState != null) {
            progress.setVisibility(View.INVISIBLE);
            webView.setVisibility(View.VISIBLE);
            webView.restoreState(savedInstanceState);
        }
        else {
            progress.setVisibility(View.VISIBLE);
            webView.setWebViewClient(new WebViewClient());
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setUserAgentString("IEMobile");
            startControllers();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_restart) {
            //Do restart
            progress.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            startControllers();
            return super.onOptionsItemSelected(item);
        } else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                onBackPressed();
            }
            return true;
        }

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
    }

    private void startControllers() {
        webView.loadUrl(url);
        final String js = "javascript:document.getElementById('username').value = '"
                + app.account.getEmail() + "';document.getElementById('password').value='"
                + app.account.getPassword() + "';document.getElementsByName('submit')[0].click();";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setWebViewClient(new WebViewClient() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (url.contains("autentica.cpd.ua.es") || (needsLogin && url.equals(WebActivity.this.url))) {
                        if (needsLogin)
                            needsLogin = false; //Prevent from repeating after login

                        progress.setVisibility(View.VISIBLE);
                        webView.setVisibility(View.GONE);
                        view.evaluateJavascript(js, new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String s) {
                            }
                        });
                    } else {
                        progress.setVisibility(View.INVISIBLE);
                        webView.setVisibility(View.VISIBLE);
                    }
                    super.onPageFinished(view, url);
                }
            });
        } else {
            progress.setVisibility(View.INVISIBLE);
            Toast.makeText(getBaseContext(), getString(R.string.error_web_older), Toast.LENGTH_LONG).show();
            webView.setVisibility(View.VISIBLE);
        }
        //Allow downloads
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
                if (AppManager.isStoragePermissionGranted(WebActivity.this)) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    request.setMimeType(mimeType);
                    //------------------------COOKIE!!------------------------
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    //------------------------COOKIE!!------------------------
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription(getString(R.string.materiales_loading_file));
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), getString(R.string.message_downloading_to_d), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
