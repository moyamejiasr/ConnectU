package com.onelio.connectu.Apps.Anuncios;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.widget.TextView;

import com.onelio.connectu.Common;
import com.onelio.connectu.R;

public class AnunciosViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios_view);

        TextView htexto = (TextView)findViewById(R.id.htexto);
        TextView badge = (TextView)findViewById(R.id.badge);
        TextView titulo1 = (TextView)findViewById(R.id.titulo1);
        TextView fecha = (TextView)findViewById(R.id.fecha);
        TextView asignatura = (TextView)findViewById(R.id.asignatura);
        TextView profesor = (TextView)findViewById(R.id.profesor);

        SpannableString content = new SpannableString(Common.announce.getTitulo1() + " " + Common.announce.getTitulo2());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        titulo1.setText(content);
        badge.setText(Common.announce.getBadge());
        if(Common.announce.getState()) {
            titulo1.setTextColor(Color.parseColor("#00695C"));
        } else {
            titulo1.setTextColor(Color.parseColor("#000000"));
        }
        fecha.setText(Common.announce.getFecha());
        asignatura.setText("(" + Common.announce.getAsignatura() + ")");
        profesor.setText(Common.announce.getProfesor());
        htexto.setText((CharSequence) Html.fromHtml(Uri.parse(Common.announce.getHTexto()).toString()));
        htexto.setMovementMethod(LinkMovementMethod.getInstance());


    }
}
