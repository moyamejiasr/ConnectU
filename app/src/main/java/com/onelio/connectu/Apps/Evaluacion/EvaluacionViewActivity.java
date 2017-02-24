package com.onelio.connectu.Apps.Evaluacion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Common;
import com.onelio.connectu.LoginActivity;
import com.onelio.connectu.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class EvaluacionViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluacion_view);

        UAWebService.HttpWebGetRequest(EvaluacionViewActivity.this, UAWebService.EVAULIACION_VIEW + Common.evid, new UAWebService.WebCallBack() {
            @Override
            public void onNavigationComplete(boolean isSuccessful, String body) {
                if (isSuccessful) {
                    Document doc = Jsoup.parse(body);
                    //Get Material
                    Elements elements = doc.select("td.columna2");
                    Elements elements1 = doc.select("td.columna2gran");
                    final String test = elements.eq(0).select("td.columna2 > span").text();
                    final String test1 = elements.eq(4).select("td.columna2").text();
                    final String test2 = elements1.text();
                    EvaluacionViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView t1 = (TextView) findViewById(R.id.title);
                            t1.setText(test);
                            TextView t2 = (TextView) findViewById(R.id.state);
                            t2.setText(getString(R.string.state) + " " + test1);
                            TextView t3 = (TextView) findViewById(R.id.note);
                            t3.setText(getString(R.string.calification) + " " + test2);
                        }
                    });
                    EvaluacionViewActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //
                        }
                    });
                } else {
                    Intent intent = new Intent(getApplication(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }
}
