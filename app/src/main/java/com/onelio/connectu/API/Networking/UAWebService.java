package com.onelio.connectu.API.Networking;

import android.content.Context;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.App;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.Managers.ErrorManager;
import com.onelio.connectu.R;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class UAWebService {

    //define callback interface
    public interface WebCallBack {

        void onNavigationComplete(boolean isSuccessful, String body);
    }

    //Logged in confirmation
    private static boolean isStillLoggedIn(Response response, String prevURL) {
        String url = response.networkResponse().request().url().toString();
        //If Contains login(now so logged out) page but not previously(requested in) == Logged out!
        return (!url.contains("autentica.cpd.ua.es") || prevURL.contains("autentica"));
    }

    //Response is not error
    private static boolean isNotError(Response response) {
        //If Contains >= 400 means one of those -> 403, 500, 503...
        return response.code() < 400;
    }

    public static void HttpWebGetRequest(final Context context, final String stringUrl, final WebCallBack callback) {
        final App app = (App) context.getApplicationContext();
        HttpClient client = new HttpClient(context, true);
        try {
            client.get(stringUrl, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //Couldn't connect with UACloud
                    callback.onNavigationComplete(false, ErrorManager.FAILED_CONNECTION);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (app.account != null && app.account.isLogged() && !isStillLoggedIn(response, stringUrl)) {
                        callback.onNavigationComplete(false, ErrorManager.LOGIN_REJECTED);
                    } else {
                        if (isNotError(response)) {
                            String body = response.body().string();
                            if (!body.isEmpty()) {
                                callback.onNavigationComplete(response.isSuccessful(), body);
                            } else {
                                callback.onNavigationComplete(false, ErrorManager.EMPTY_RESPONSE);
                            }
                        } else {
                            callback.onNavigationComplete(false, ErrorManager.BAD_RESPONSE);
                        }
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            //Unexpected error
            FirebaseCrash.report(e);
            AlertManager alert = new AlertManager(context);
            alert.setMessage(context.getString(R.string.error_unexpected), e.getMessage());
            alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    AppManager.appClose();
                }
            });
            alert.show();
        }
    }

    //Post Method
    public static void HttpWebPostRequest(final Context context, final String stringUrl, String json, final WebCallBack callback) {
        final App app = (App) context.getApplicationContext();
        HttpClient client = new HttpClient(context, true); //Finally UA fucked up their service, now it's f slow too
        try {
            client.post(stringUrl, json, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //Couldn't connect with UACloud
                    callback.onNavigationComplete(false, ErrorManager.FAILED_CONNECTION);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (app.account != null && app.account.isLogged() && !isStillLoggedIn(response, stringUrl)) {
                        callback.onNavigationComplete(false, ErrorManager.LOGIN_REJECTED);
                    } else {
                        if (isNotError(response)) {
                            String body = response.body().string();
                            if (!body.isEmpty()) {
                                callback.onNavigationComplete(response.isSuccessful(), body);
                            } else {
                                callback.onNavigationComplete(false, ErrorManager.EMPTY_RESPONSE);
                            }
                        } else {
                            callback.onNavigationComplete(false, ErrorManager.EMPTY_RESPONSE);
                        }
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            //Unexpected error
            FirebaseCrash.report(e);
            AlertManager alert = new AlertManager(context);
            alert.setMessage(context.getString(R.string.error_unexpected), e.getMessage());
            alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    AppManager.appClose();
                }
            });
            alert.show();
        }
    }

    //JSONPost Method
    public static void HttpWebJSONPostRequest(final Context context, final String stringUrl, String json, final WebCallBack callback) {
        final App app = (App) context.getApplicationContext();
        HttpClient client = new HttpClient(context, true);
        try {
            client.jpost(stringUrl, json, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //Couldn't connect with UACloud
                    callback.onNavigationComplete(false, ErrorManager.FAILED_CONNECTION);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (app.account != null && app.account.isLogged() && !isStillLoggedIn(response, stringUrl)) {
                        callback.onNavigationComplete(false, ErrorManager.LOGIN_REJECTED);
                    } else {
                        if (isNotError(response)) {
                            String body = response.body().string();
                            if (!body.isEmpty()) {
                                callback.onNavigationComplete(response.isSuccessful(), body);
                            } else {
                                callback.onNavigationComplete(false, ErrorManager.EMPTY_RESPONSE);
                            }
                        } else {
                            callback.onNavigationComplete(false, ErrorManager.BAD_RESPONSE);
                        }
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            //Unexpected error
            FirebaseCrash.report(e);
            AlertManager alert = new AlertManager(context);
            alert.setMessage(context.getString(R.string.error_unexpected), e.getMessage());
            alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    AppManager.appClose();
                }
            });
            alert.show();
        }
    }

    //MultipartPost Method
    public static void HttpWebMultiPartPostRequest(final Context context, final String stringUrl, RequestBody body, final WebCallBack callback) {
        final App app = (App) context.getApplicationContext();
        HttpClient client = new HttpClient(context, true);
        try {
            client.mpost(stringUrl, body, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //Couldn't connect with UACloud
                    callback.onNavigationComplete(false, ErrorManager.FAILED_CONNECTION);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (app.account != null && app.account.isLogged() && !isStillLoggedIn(response, stringUrl)) {
                        callback.onNavigationComplete(false, ErrorManager.LOGIN_REJECTED);
                    } else {
                        if (isNotError(response)) {
                            String body = response.body().string();
                            if (!body.isEmpty()) {
                                callback.onNavigationComplete(response.isSuccessful(), body);
                            } else {
                                callback.onNavigationComplete(false, ErrorManager.EMPTY_RESPONSE);
                            }
                        } else {
                            callback.onNavigationComplete(false, ErrorManager.BAD_RESPONSE);
                        }
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            //Unexpected error
            FirebaseCrash.report(e);
            AlertManager alert = new AlertManager(context);
            alert.setMessage(context.getString(R.string.error_unexpected), e.getMessage());
            alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    AppManager.appClose();
                }
            });
            alert.show();
        }
    }

    //Normal Download
    public static void HttpFileDownloadRequest(final Context context, final String stringUrl, final File file, final WebCallBack callback) {
        final App app = (App) context.getApplicationContext();
        HttpClient client = new HttpClient(context, true);
        file.deleteOnExit();
        try {
            client.download(stringUrl, new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    //Couldn't connect with UACloud
                    callback.onNavigationComplete(false, ErrorManager.FAILED_CONNECTION);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (app.account != null && app.account.isLogged() && !isStillLoggedIn(response, stringUrl)) {
                        callback.onNavigationComplete(false, ErrorManager.LOGIN_REJECTED);
                    } else {
                        BufferedSink sink = Okio.buffer(Okio.sink(file));
                        sink.writeAll(response.body().source());
                        sink.close();
                        callback.onNavigationComplete(response.isSuccessful(), ErrorManager.FILE_DONT_EXIST);
                    }
                    response.close();
                }
            });
        } catch (IOException e) {
            //Unexpected error
            FirebaseCrash.report(e);
            AlertManager alert = new AlertManager(context);
            alert.setMessage(context.getString(R.string.error_unexpected), e.getMessage());
            alert.setPositiveButton("OK", new AlertManager.AlertCallBack() {
                @Override
                public void onClick(boolean isPositive) {
                    AppManager.appClose();
                }
            });
            alert.show();
        }
    }

}
