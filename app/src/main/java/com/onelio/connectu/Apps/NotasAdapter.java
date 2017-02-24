package com.onelio.connectu.Apps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Onelio on 05/02/2017.
 */

public class NotasAdapter extends BaseAdapter {

    private final Context mContext;
    JSONArray names;

    // 1
    public NotasAdapter(Context context, JSONArray names) {
        this.mContext = context;
        this.names = names;
    }

    // 2
    @Override
    public int getCount() {
        return names.length();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 2
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.list_notas, null);
        }

        // 3
        final TextView tipo = (TextView)convertView.findViewById(R.id.tipo);
        final TextView titulo = (TextView)convertView.findViewById(R.id.titulo);
        final TextView fecha = (TextView)convertView.findViewById(R.id.fecha);
        final TextView observ = (TextView)convertView.findViewById(R.id.sended);
        final TextView nota = (TextView)convertView.findViewById(R.id.nota);

        try {
            tipo.setText(names.getJSONObject(position).getString("TIPO"));
            titulo.setText(names.getJSONObject(position).getString("TITULO"));
            String date = names.getJSONObject(position).getString("FECHA");
            date = date.substring(date.indexOf("(") + 1, date.indexOf(")"));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fecha.setText(sdf.format(new Date(Long.valueOf(date))));
            String fnota = convertView.getResources().getString(R.string.nota) + ": " + String.valueOf(names.getJSONObject(position).getDouble("NOTANUM"));
            nota.setText(fnota);
            observ.setText(String.valueOf(names.getJSONObject(position).getInt("OBSERVACIONES")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

}