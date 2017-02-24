package com.onelio.connectu.Apps.Expediente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onelio.connectu.Apps.Tutorias.TutoriaList;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onelio on 24/02/2017.
 */

public class ExpedienteAdapter extends BaseAdapter {

    private final Context mContext;
    List<JSONObject> jarray = new ArrayList<>();

    // 1
    public ExpedienteAdapter(Context context, List<JSONObject> jarray) {
        this.mContext = context;
        this.jarray = jarray;
    }

    @Override
    public int getCount() {
        return jarray.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            view = layoutInflater.inflate(R.layout.list_exp, null);
        }

        final TextView titulo = (TextView)view.findViewById(R.id.title);
        final TextView subtitle = (TextView)view.findViewById(R.id.subtitle);
        final TextView nota = (TextView)view.findViewById(R.id.note);

        try {
            titulo.setText(jarray.get(i).getString("sig_name"));
            subtitle.setText(jarray.get(i).getString("convocat"));
            nota.setText(jarray.get(i).getString("nota"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return view;
    }
}
