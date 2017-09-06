package com.onelio.connectu.Activities.Apps.Evaluacion;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.onelio.connectu.API.EvaluacionRequest;
import com.onelio.connectu.Helpers.ObjectHelper;
import com.onelio.connectu.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EvaluacionFilterDialog extends Dialog {

    private ArrayAdapter<String> namepTipo;
    private ArrayAdapter<String> namepAsignatura;

    private Activity activity;
    private EvaluacionRequest request;

    private EvResponse callback;

    public interface EvResponse {
        void onUpdated();
    }

    public EvaluacionFilterDialog(Activity activity) {
        super(activity);
        this.activity = activity;
        this.request = new EvaluacionRequest(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.view_dialog_evaluacion_filter);
        //Set content
        LinearLayout lExit = (LinearLayout) findViewById(R.id.llExitD);

        setAdapters();
        setDefaultData();

        lExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onUpdated();
                }
                EvaluacionFilterDialog.super.onBackPressed();
            }
        });

    }

    public void setOnUpdated(EvResponse response) {
        this.callback = response;
    }

    private void setAdapters() {
        //Subject Filter
        List<String> subjects = ObjectHelper.getSubjectsName(getContext(), request.getYear(), true);
        namepAsignatura  = new ArrayAdapter<>(activity,  R.layout.spinner_item, subjects);
        namepAsignatura.setDropDownViewResource(R.layout.spinner_dropdown_item);

        //Type Filter
        List<String> orden = new ArrayList<>(Arrays.asList(activity.getResources().getStringArray(R.array.type_filters)));
        namepTipo  = new ArrayAdapter<>(activity,  R.layout.spinner_item, orden);
        namepTipo.setDropDownViewResource(R.layout.spinner_dropdown_item);
    }

    private void setDefaultData() {
        Spinner sp_sign = (Spinner) findViewById(R.id.sp_sign);
        sp_sign.setAdapter(namepAsignatura);
        sp_sign.getBackground().setColorFilter(getContext().getResources().getColor(R.color.black_overlay), PorterDuff.Mode.SRC_ATOP);
        sp_sign.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                request.setSubject(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_sign.setSelection(request.getSubject());

        Spinner sp_order = (Spinner) findViewById(R.id.sp_order);
        sp_order.setAdapter(namepTipo);
        sp_order.getBackground().setColorFilter(getContext().getResources().getColor(R.color.black_overlay), PorterDuff.Mode.SRC_ATOP);
        sp_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                request.setFilter(position);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_order.setSelection(request.getFilter());
    }

}
