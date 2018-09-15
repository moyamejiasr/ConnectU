package com.onelio.connectu.Activities.Apps.Profesores;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.gson.Gson;
import com.onelio.connectu.Activities.Apps.Tutorias.TutoriasViewActivity;
import com.onelio.connectu.Common;
import com.onelio.connectu.Containers.TeacherData;
import com.onelio.connectu.Helpers.BlurTransform;
import com.onelio.connectu.Managers.AlertManager;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfesoresViewActivity extends AppCompatActivity {

  TeacherData data;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    setContentView(R.layout.activity_profesores_view);
    Gson gson = new Gson();
    data = gson.fromJson((String) getIntent().getSerializableExtra("JDATA"), TeacherData.class);
    Window window = getWindow();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.setStatusBarColor(getResources().getColor(R.color.colorAccent));
    }

    CircleImageView img = (CircleImageView) findViewById(R.id.pictureD);
    ImageView imgb = (ImageView) findViewById(R.id.imgb);
    TextView name = (TextView) findViewById(R.id.name);
    TextView subject = (TextView) findViewById(R.id.signature);
    TextView text = (TextView) findViewById(R.id.view_content);

    Picasso.with(getBaseContext()).load(data.getPicture()).into(img);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Picasso.with(getBaseContext())
          .load(data.getPicture())
          .transform(new BlurTransform(ProfesoresViewActivity.this))
          .into(imgb);
    } else {
      Picasso.with(getBaseContext()).load(data.getPicture()).into(imgb);
    }
    name.setText(data.getName());
    subject.setText(data.getSubject());
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      text.setText(
          Html.fromHtml(data.getDescription(), Html.FROM_HTML_MODE_COMPACT)
              .toString()
              .replace("&nbsp", " "));
    } else {
      text.setText(Html.fromHtml(data.getDescription()).toString().replace("&nbsp", " "));
    }
  }

  public void onBackClick(View view) {
    super.onBackPressed();
  }

  public void onEmailClick(View view) {
    Intent emailIntent =
        new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", data.getEmail(), null));
    emailIntent.putExtra(
        Intent.EXTRA_SUBJECT, "Mensaje de alumno de asigantura " + data.getSubject());
    emailIntent.putExtra(Intent.EXTRA_TEXT, "Escribe aquí tu mensaje...");
    startActivity(Intent.createChooser(emailIntent, "Send email..."));
  }

  public void onTutoriaClick(View view) {

    final AlertManager alert = new AlertManager(this);
    alert.setMessage(getString(R.string.tutoria_insert_topic));
    alert.enableDataRequest(this);
    alert.setPositiveButton(
        getString(R.string.tutoria_create),
        new AlertManager.AlertCallBack() {
          @Override
          public void onClick(boolean isPositive) {
            Intent intent = new Intent(ProfesoresViewActivity.this, TutoriasViewActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(Common.TUTORIAS_STRING_AUTHOR, data.getName());
            intent.putExtra(Common.TUTORIAS_STRING_AUTHOR_IMG, data.getPicture());
            intent.putExtra(Common.TUTORIAS_STRING_YEAR, data.getYear());
            intent.putExtra(Common.TUTORIAS_STRING_SUBJECTID, data.getSubject());
            intent.putExtra(Common.TUTORIAS_STRING_TITLE, alert.getInputResult());
            startActivity(intent);
          }
        });
    alert.setNegativeButton(
        getString(R.string.tutoria_cancel),
        new AlertManager.AlertCallBack() {
          @Override
          public void onClick(boolean isPositive) {}
        });
    alert.show();
  }
}
