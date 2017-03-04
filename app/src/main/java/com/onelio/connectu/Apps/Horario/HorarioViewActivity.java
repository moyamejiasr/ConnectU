package com.onelio.connectu.Apps.Horario;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.API.WebApi;
import com.onelio.connectu.Common;
import com.onelio.connectu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class HorarioViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horario_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Mi Evento");
        TextView title = (TextView)findViewById(R.id.title);
        TextView horario = (TextView)findViewById(R.id.horario);
        title.setText(Common.siguaName);
        horario.setText(Common.siguaHorario);

        if (!Common.isLogged) {
            //Build Okhttp
            WebApi.initialize(getBaseContext());
        }

        if (Common.siguaID.length() > 0) {
            UAWebService.HttpWebGetRequest(HorarioViewActivity.this, UAWebService.SIGUA_LOAD + Common.siguaID, new UAWebService.WebCallBack() {
                @Override
                public void onNavigationComplete(final boolean isSuccessful, final String body) {
                    final CardView card = (CardView) findViewById(R.id.cardMap);
                    final TextView subtitle = (TextView) findViewById(R.id.subtitle);
                    if (isSuccessful) {
                        try {
                            JSONObject data = new JSONObject(body);
                            final JSONObject object = data.getJSONArray("features").getJSONObject(0).getJSONObject("properties");
                            HorarioViewActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        subtitle.setText(object.getString("nombre_actividad") + " " + object.getString("denominacion"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    card.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //Event action
                                            String uri = null;
                                            try {
                                                uri = String.format(Locale.ENGLISH, "geo:%f,%f", Double.valueOf(object.getString("lat")), Double.valueOf(object.getString("lon")));
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                startActivity(intent);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

}
