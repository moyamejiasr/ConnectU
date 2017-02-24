package com.onelio.connectu.Apps.Evaluacion;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onelio.connectu.Device.DeviceManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Onelio on 04/12/2016.
 */
public class TestAdapter extends BaseAdapter {

    private final Context mContext;
    JSONArray names;

    // 1
    public TestAdapter(Context context, JSONArray names) {
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
            convertView = layoutInflater.inflate(R.layout.list_test, null);
        }

        // 3
        final TextView tipo = (TextView)convertView.findViewById(R.id.tipo);
        final TextView titulo = (TextView)convertView.findViewById(R.id.titulo);
        final TextView fecha = (TextView)convertView.findViewById(R.id.fecha);
        final ImageView state = (ImageView)convertView.findViewById(R.id.state);
        final ImageView img = (ImageView)convertView.findViewById(R.id.img);
        final TextView sended = (TextView)convertView.findViewById(R.id.sended);

        try {
            String jtipe = names.getJSONObject(position).getString("tipo");
            tipo.setText(DeviceManager.capFirstLetter(names.getJSONObject(position).getString("tipo")));
            titulo.setText(Html.fromHtml(names.getJSONObject(position).getString("nombre")));
            fecha.setText(convertView.getResources().getString(R.string.from) + " " + names.getJSONObject(position).getString("inicio") + " " + convertView.getResources().getString(R.string.at) + " " + names.getJSONObject(position).getString("final")+ " en " + DeviceManager.capFirstLetter(names.getJSONObject(position).getString("asignatura")));

            if(names.getJSONObject(position).getString("abierto").contains("S")) {
                state.setImageResource(R.drawable.open);
                sended.setText(convertView.getResources().getString(R.string.open));
            } else {
                state.setImageResource(R.drawable.closed);
                sended.setText(convertView.getResources().getString(R.string.closed));
            }

            switch (jtipe) {
                case "LISTADO NOTAS":
                    img.setImageResource(R.drawable.lista_notas);
                    break;
                case "EXAMEN OFICIAL" :
                    img.setImageResource(R.drawable.examen_oficial);
                    break;
                case "CONTROL TEORÍA" :
                    img.setImageResource(R.drawable.control_teoria);
                    break;
                case "CONTROL PRÁCTICA" :
                    img.setImageResource(R.drawable.control_practica);
                    break;
                case "CONTROL CALCULADO" :
                    img.setImageResource(R.drawable.otro_control);
                    break;
                case "CONTROL TEST" :
                    img.setImageResource(R.drawable.otro_control);
                    break;
                case "ENTREGA PRÁCTICA" :
                    img.setImageResource(R.drawable.entrega_practica);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

}

