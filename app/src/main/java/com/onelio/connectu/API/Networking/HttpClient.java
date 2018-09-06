package com.onelio.connectu.API.Networking;

import android.content.Context;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.onelio.connectu.App;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {
    private OkHttpClient client = null;
    private App app;
    private static String UserAgent = "";

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

    private void initClient(Context context, boolean big) {
        if(client != null){
            return;
        }
        app = (App) context.getApplicationContext();
        if (app.cookieJar == null)
            cookieMaker(context);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder().cookieJar(app.cookieJar);
        if (big) {
            builder.connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS);
        }

        if (this.UserAgent.length() == 0) {
            this.UserAgent = UserAgentSwitcher.Get();
        }
        builder.addInterceptor(new UserAgentInterceptor(this.UserAgent));
        client = builder.build();
    }

    public HttpClient(Context context) {
        initClient(context, false);
    }

    public HttpClient(Context context, boolean big) { //Evaluacion case (Cause Evaluacion is reaaaaaaaaallllyyy sloooow)
        initClient(context, true);
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

