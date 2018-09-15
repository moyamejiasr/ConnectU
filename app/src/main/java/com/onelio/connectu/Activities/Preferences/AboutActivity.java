package com.onelio.connectu.Activities.Preferences;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import com.onelio.connectu.R;

public class AboutActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about);
    // Action bar
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    if (Build.VERSION.SDK_INT >= 21) {
      getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDarkLight));
      getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkLight));
    }
    TextView text = (TextView) findViewById(R.id.textView);
    text.setText(Html.fromHtml(getString(R.string.about)));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    super.onBackPressed();
    return true;
  }
}
