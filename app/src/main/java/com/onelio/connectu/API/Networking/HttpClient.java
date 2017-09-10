package com.onelio.connectu.API.Networking;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.onelio.connectu.App;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpClient {

    private OkHttpClient client;
    private App app;

    private void cookieMaker() { //Method to prevent exception caused by cookieJar being null even if there is no login after
        app.cookieJar = new ClearableCookieJar() {
            @Override
            public void clearSession() {

            }

            @Override
            public void clear() {

            }

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                return null;
            }
        };
    }

    public HttpClient(Context context) {
        app = (App) context.getApplicationContext();
        if (app.cookieJar==null)
            cookieMaker();
        client = new OkHttpClient().newBuilder()
                .cookieJar(app.cookieJar)
                .build();
    }

    public HttpClient(Context context, boolean big) { //Evaluacion case (Cause Evaluacion is reaaaaaaaaallllyyy sloooow)
        app = (App) context.getApplicationContext();
        if (app.cookieJar==null)
            cookieMaker();
        client = new OkHttpClient().newBuilder()
                .cookieJar(app.cookieJar)
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
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

