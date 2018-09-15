package com.onelio.connectu.Activities.Apps.Testing;

import android.content.Context;
import com.onelio.connectu.API.Networking.HttpClient;
import com.onelio.connectu.Managers.AppManager;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Created by rmoya on 12/09/2017. */
public class Test1 {

  public static void HttpWebGetRequest(final Context context) {
    HttpClient client = new HttpClient(context);
    try {
      client.get(
          "https://dev.datos.ua.es/uapi/ugWhFCycs4t6DwNde5Zn/datasets/784/data",
          new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
              String body = response.body().string();
              body = AppManager.after(body, "null(");
              body = body.substring(0, body.length() - 1);
              try {
                JSONArray newer = new JSONArray();
                JSONArray jarray = new JSONArray(body);
                for (int i = 0; i < jarray.length(); i++) {
                  JSONObject jobject = jarray.getJSONObject(i);
                  JSONObject n = new JSONObject();
                  n.put("ID", jobject.getString("ID_SIGUA"));
                  n.put("PLACE", jobject.getString("AUL_DESID1"));
                  newer.put(n);
                }
                String xd = "xd";
              } catch (JSONException e) {
                e.printStackTrace();
              }
              response.close();
            }
          });
    } catch (IOException e) {
      // Unexpected error
    }
  }

  /**
   * * Legends tells that Google is going to add some new intents.. █▀▀ㅤ█ ㅤ█ █ㅤ █ㅤ ▀▀█▀▀ㅤㅤ█ㅤ █ㅤ █▀▀▄
   * 　 ▀▀█ㅤ█▀▀█ㅤ █ㅤ █ㅤ ㅤ █ㅤㅤㅤ █ ㅤ █ㅤ █ ㅤ █ 　 ▀▀▀ㅤ▀ ㅤ▀ㅤ ▀▀▀▀ㅤ ▀ㅤㅤ ㅤ ▀▀▀▀ㅤ █▀▀ 　 ㅤㅤㅤㅤㅤㅤ▄▀▀▄ㅤ█▀▀▄ㅤ █▀▀▄
   * ㅤㅤㅤㅤㅤㅤ█▄▄█ㅤ█ ㅤ █ㅤ█ ㅤ █ ㅤㅤㅤㅤㅤㅤ█ㅤ █ㅤ█ ㅤ █ㅤ█▄▄▀
   *
   * <p>─────███────██ ──────████───███ ────────████──███ ─────────████─█████ ████████──█████████
   * ████████████████████ ████████████████████ █████████████████████ █████████████████████
   * █████████████████████ ██─────██████████████ ███────────█████████ █──█───────────████
   * █──────────────██ ██──────────────█────────▄███████▄ ██───███▄▄──▄▄███──────▄██$█████$██▄
   * ██──█▀───▀███────█───▄██$█████████$██▄ ██──█───█──██───█─█──█$█████████████$█
   * ██──█──────██─────█──█████████████████ ██──██────██▀█───█─────██████████████
   * ─█───██████──▀████───────███████████ ──────────────────█───────█████████
   * ─────────────▀▀████──────███████████ ────────────────█▀──────██───████▀─▀█
   * ────────────────▀█──────█─────▀█▀───█ ──▄▄▄▄▄▄▄────────██────█───████▀───██
   * ─█████████████────▀█──█───███▀──▄▄██ ─█▀██▀██▀████▀█████▀──█───██████▀─▀█
   * ─█────────█▄─────────██───████▀───██ ─██▄████▄──██────────██───██──▄▄▄██
   * ──██▄▄▄▄▄██▀─────────██──█████▀───█ ─────────███────────███████▄────███
   * ────────███████─────█████████████ ───────▄██████████████████████ ████████─██████████████████
   * ─────────██████████████ ────────███████████ ───────█████ ──────████ ─────████
   *
   * <p>ㅤ ㅤ ㅤ ㅤ ㅤ ▀▀█▀▀ㅤ█▀▀█ㅤ█ㅤ▄█ㅤ█▀▀ ㅤㅤ ㅤㅤ ㅤ ㅤㅤ█ ㅤㅤ█▄▄█ㅤ█▀▀▄ ㅤ█▀▀ ㅤ ㅤ ㅤ ㅤ ㅤ ㅤ ▀ㅤ ㅤ▀ㅤ ▀ㅤ▀ㅤㅤ▀ㅤ▀▀▀
   *
   * <p>█▀▄▀█ㅤ█ ㅤ █ㅤ ㅤ█▀▄▀█ ▄▀▀▄ █▀▀▄ █▀▀ █ ㅤ █ㅤ█ █─▀─█ㅤ█▄▄█ㅤㅤ █─▀─█ █ ㅤ █ █ ㅤ █ █▀▀ █▄▄█ㅤ█ █ㅤㅤ
   * █ㅤ▄▄▄█ㅤ ㅤ█ㅤㅤ█ ─▀▀ ─ ▀ ㅤ ▀ ▀▀▀ ▄▄▄█ㅤ▄
   */
}
