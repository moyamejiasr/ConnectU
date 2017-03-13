package com.onelio.connectu.Apps.WebView;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.onelio.connectu.Common;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

public class WebViewActivity extends AppCompatActivity {

    ProgressDialog dialog;
    boolean isLoaded = false;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (!isLoaded){
            isLoaded = true;
            setTitle(Common.webName);
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getString(R.string.loading_wait));
            dialog.setIndeterminate(true);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            webView = (WebView)findViewById(R.id.webView);
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl(Common.webURL);
            WebSettings settings = webView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setUserAgentString("IEMobile");
            final String js = "javascript:document.getElementById('username').value = '"+Common.loginUsername+"';document.getElementById('password').value='"+Common.loginPassword+"';document.getElementsByName('submit')[0].click();";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webView.setWebViewClient(new WebViewClient() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if (url.contains("autentica.cpd.ua.es")) {
                            dialog.show();
                            webView.setVisibility(View.INVISIBLE);
                            view.evaluateJavascript(js, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                }
                            });
                        } else {
                            dialog.hide();
                            webView.setVisibility(View.VISIBLE);
                        }
                        super.onPageFinished(view, url);
                    }
                });
            } else {
                dialog.hide();
                webView.setVisibility(View.VISIBLE);
            }
            webView.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    if (DeviceManager.isStoragePermissionGranted(WebViewActivity.this)) {
                        DownloadManager.Request request = new DownloadManager.Request(
                                Uri.parse(url));
                        request.setMimeType(mimetype);
                        String cookies = CookieManager.getInstance().getCookie(url);
                        request.addRequestHeader("cookie", cookies);
                        request.addRequestHeader("User-Agent", userAgent);
                        request.setDescription("Downloading file...");
                        request.setTitle(URLUtil.guessFileName(url, contentDisposition,
                                mimetype));
                        request.allowScanningByMediaScanner();
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        request.setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS + "/ConnectU/", URLUtil.guessFileName(
                                        url, contentDisposition, mimetype));
                        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        dm.enqueue(request);
                        Toast.makeText(getApplicationContext(), getString(R.string.file_will_save_in), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (webView.canGoBack()) {
            webView.goBack();
            return true;
        } else {
            super.onBackPressed();
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
}


