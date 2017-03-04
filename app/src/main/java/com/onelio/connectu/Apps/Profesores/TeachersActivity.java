package com.onelio.connectu.Apps.Profesores;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.onelio.connectu.API.UAWebService;
import com.onelio.connectu.Common;
import com.onelio.connectu.Device.AlertManager;
import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class TeachersActivity extends AppCompatActivity {

    JSONArray teachers = new JSONArray();
    TeachersAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        try {
            teachers  = Common.data.getJSONArray("teachers");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        showGrid();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onBackPressed();
        return true;
    }

    public void showGrid() {
        GridView gridView = (GridView) findViewById(R.id.gridview);
        Adapter = new TeachersAdapter(getBaseContext(), teachers);
        gridView.setAdapter(Adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Common.teacher = teachers.getJSONObject(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                View imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(TeachersActivity.this, TeachersViewActivity.class);
                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Pair<View, String> pair1 = Pair.create(imageView, getString(R.string.activity_image_trans));
                    Pair<View, String> pair2 = Pair.create(imageView, getString(R.string.activity_text_trans));
                    Pair<View, String> pair3 = Pair.create(imageView, getString(R.string.activity_mixed_trans));
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(TeachersActivity.this, pair1, pair2, pair3);
                    startActivity(intent, options.toBundle());
                }
                else {
                    startActivity(intent);
                }
            }
        });
    }

}
