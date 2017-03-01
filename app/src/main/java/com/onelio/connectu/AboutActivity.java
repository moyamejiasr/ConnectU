package com.onelio.connectu;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.onelio.connectu.API.WebApi;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void onClick(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Onelio/ConnectU"));
        startActivity(browserIntent);
    }

    public void testing() {
        try {
            WebApi.download("https://cvnet.cpd.ua.es/uaHorarios/Export/Download?hash=1050780860800870860570981030521150660851061001130680770880690970821030830510430980490661170750851181071010730560540691011061050500711160781160811120770651220721090481021150811010881081211190820891181201180470701130801080531210510710811211001051080740651150500550650730770530731120820831060721060770890561210521131091111030617", new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final File folderd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()+"/MyUACloud");
                    if (!folderd.exists()) {
                        folderd.mkdirs();
                    }
                    File downloadedFile = new File(folderd,"calendario.ics");
                    BufferedSink sink = Okio.buffer(Okio.sink(downloadedFile));
                    sink.writeAll(response.body().source());
                    sink.close();
                    if (downloadedFile.exists()) {
                        Intent myIntent = new Intent(Intent.ACTION_VIEW);
                        String mime= "text/calendar";
                        myIntent.setDataAndType(Uri.fromFile(downloadedFile), mime);
                        startActivity(myIntent);
                    } else {
                        AboutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "No se ha podido descargar el archivo", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
