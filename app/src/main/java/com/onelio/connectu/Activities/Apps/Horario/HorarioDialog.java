package com.onelio.connectu.Activities.Apps.Horario;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.API.HorarioRequest;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.Helpers.ObjectHelper;
import com.onelio.connectu.Helpers.TimeParserHelper;
import com.onelio.connectu.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class HorarioDialog extends Dialog {

    private CalendarEvent event;
    private Activity activity;

    public HorarioDialog(Activity activity, CalendarEvent event) {
        super(activity);
        this.activity = activity;
        this.event = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_dialog_horario);
        //Set content
        CardView typeCard = (CardView) findViewById(R.id.typecardD);
        final CardView card = (CardView) findViewById(R.id.cardMap);
        TextView type = (TextView) findViewById(R.id.typeD);
        TextView title = (TextView) findViewById(R.id.titleD);
        TextView text = (TextView) findViewById(R.id.textD);
        TextView subtitle = (TextView) findViewById(R.id.subtitleD);
        LinearLayout lExit = (LinearLayout) findViewById(R.id.llExitD);
        title.setText(event.getTitle());
        subtitle.setText(event.getSubtitle());
        if (event.getType().equals(HorarioRequest.CALENDAR_DOCENCIA)) {
            typeCard.setCardBackgroundColor(Color.parseColor("#0091EA"));
            type.setText(getContext().getString(R.string.dialog_horario_docencia));
        }
        if (event.getType().equals(HorarioRequest.CALENDAR_EVALUACION)) {
            typeCard.setCardBackgroundColor(Color.parseColor("#009688"));
            type.setText(getContext().getString(R.string.dialog_horario_evaluacion));
        }
        if (event.getType().equals(HorarioRequest.CALENDAR_EXAMENES)) {
            typeCard.setCardBackgroundColor(Color.parseColor("#F50057"));
            type.setText(getContext().getString(R.string.dialog_horario_examen));
        }
        if (event.getType().equals(HorarioRequest.CAlENDAR_FESTIVOS)) {
            typeCard.setCardBackgroundColor(Color.parseColor("#FFEB3B"));
            type.setText(getContext().getString(R.string.dialog_horario_festivo));
        }

        String rloc = ObjectHelper.getPlace(getContext(), event.getSigua());
        if (rloc.isEmpty())
            rloc = event.getLoc();

        String instant = TimeParserHelper.getDifference(getContext(), event.getStart(), event.getEnd());
        String content = getContext().getString(R.string.view_horario_last) + " " + instant
                + "\n" + getContext().getString(R.string.view_horario_loc) + ": " + rloc;
        text.setText(content);

        HorarioRequest request = new HorarioRequest(getContext());
        request.getSIGUA(event, new HorarioRequest.HorarioCallback() {
            @Override
            public void onCompleted(final boolean onResult, final String message) {
                if(activity == null)
                    return;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (onResult) {
                            try {
                                card.setAlpha(1);
                                JSONObject data = new JSONObject(message);
                                final JSONObject object = data.getJSONArray("features").getJSONObject(0).getJSONObject("properties");
                                card.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //Event action
                                        try {
                                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f?z=21", Double.valueOf(object.getString("lat")), Double.valueOf(object.getString("lon")));
                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                            if(intent.resolveActivity(getContext().getPackageManager()) != null) {
                                                activity.startActivityForResult(intent, 0);
                                            } else {
                                                Toast.makeText(getContext(), getContext().getString(R.string.error_no_maps), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            FirebaseCrash.report(e);
                                            Toast.makeText(getContext(), getContext().getString(R.string.error_external_app), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });

        lExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HorarioDialog.super.onBackPressed();
            }
        });

    }

}