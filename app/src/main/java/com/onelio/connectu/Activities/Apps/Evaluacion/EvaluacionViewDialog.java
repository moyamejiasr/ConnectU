package com.onelio.connectu.Activities.Apps.Evaluacion;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onelio.connectu.Containers.EvaluacionData;
import com.onelio.connectu.Helpers.ColorHelper;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;

public class EvaluacionViewDialog extends Dialog {

    private EvaluacionData data;
    private Activity activity;

    public EvaluacionViewDialog(Activity activity, EvaluacionData data) {
        super(activity);
        this.activity = activity;
        this.data = data;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_dialog_evaluacion_view);
        //Set content
        LinearLayout lExit = (LinearLayout) findViewById(R.id.llExitD);
        TextView text = (TextView) findViewById(R.id.textD);
        TextView title = (TextView) findViewById(R.id.titleD);
        TextView subtitle = (TextView) findViewById(R.id.subtitleD);
        TextView type = (TextView) findViewById(R.id.typeD);
        TextView date = (TextView) findViewById(R.id.dateD);
        CardView card = (CardView) findViewById(R.id.cardD);

        title.setText(AppManager.capAfterSpace(Html.fromHtml(data.getName()).toString()));
        card.setCardBackgroundColor(Color.parseColor(ColorHelper.getColor(AppManager.after(data.getType(), " ").charAt(0))));
        type.setText(AppManager.capAfterSpace(data.getType()));
        subtitle.setText(getContext().getString(R.string.evaluacion_published_in) + " " + AppManager.capAfterSpace(data.getSubject()));
        String sdate = getContext().getString(R.string.evaluacion_from) + " " + DateFormat.format("dd",   data.getStart()) + "/" + DateFormat.format("MM",   data.getStart()) + "/" + (data.getStart().getYear() + 1900)
                + " " + getContext().getString(R.string.evaluacion_to) + " " + DateFormat.format("dd",   data.getEnd()) + "/" + DateFormat.format("MM",   data.getStart()) + "/" + (data.getEnd().getYear() + 1900);
        date.setText(sdate);
        text.setText(Html.fromHtml(data.getTable()));

        lExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EvaluacionViewDialog.super.onBackPressed();
            }
        });

    }

}
