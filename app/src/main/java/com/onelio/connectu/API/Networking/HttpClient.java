package com.onelio.connectu.API.Networking;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.onelio.connectu.App;
import com.onelio.connectu.Managers.AppManager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private OkHttpClient client;
    private App app;
    //SINCE UACLOUD FILTHY TEAM BLOCKED MY USER-AGENT NOW I NEED A NEW ONE
    private static String strUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.81 Safari/537.36";

    public class UserAgentInterceptor implements Interceptor {

        private final String userAgent;

        public UserAgentInterceptor(String userAgent) {
            this.userAgent = userAgent;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .header("User-Agent", userAgent)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }

    private void cookieMaker(Context context) { //Method to prevent exception caused by cookieJar being null even if there is no login after
        app.cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
    }

    public HttpClient(Context context) {
        app = (App) context.getApplicationContext();
        if (app.cookieJar==null)
            cookieMaker(context);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .cookieJar(app.cookieJar);

        builder.addInterceptor(new UserAgentInterceptor(strUserAgent));
        client = builder.build();
    }

    public HttpClient(Context context, boolean big) { //Evaluacion case (Cause Evaluacion is reaaaaaaaaallllyyy sloooow)
        app = (App) context.getApplicationContext();
        if (app.cookieJar==null)
            cookieMaker(context);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .cookieJar(app.cookieJar)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);

        builder.addInterceptor(new UserAgentInterceptor(strUserAgent));
        client = builder.build();
    }

    //Normal get
    public Call get(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-Requested-With", "es.ua.uacloud")
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //Normal post
    public Call post(String url, String fdata, Callback callback) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, fdata);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //JSON post
    public Call jpost(String url, String json, Callback callback) throws IOException {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("cache-control", "no-cache")
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //Multipart post
    public Call mpost(String url, RequestBody body, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .method("POST", RequestBody.create(null, new byte[0]))
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;
    }

    //Normal Download
    public Call download(String url, Callback callback) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        return call;

    }

}

