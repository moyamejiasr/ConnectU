package com.onelio.connectu.Apps.Profesores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Onelio on 18/02/2017.
 */

public class TeachersAdapter extends BaseAdapter {

    private final Context mContext;
    JSONArray names;

    // 1
    public TeachersAdapter(Context context, JSONArray names) {
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
            convertView = layoutInflater.inflate(R.layout.list_teacher, null);
        }

        // 3
        final TextView nombre = (TextView)convertView.findViewById(R.id.name);
        final TextView asignatura = (TextView)convertView.findViewById(R.id.signature);
        final CircularImageView img = (CircularImageView)convertView.findViewById(R.id.img);

        try {
            nombre.setText(names.getJSONObject(position).getString("name"));
            asignatura.setText(names.getJSONObject(position).getString("signature"));
            Picasso.with(mContext)
                    .load(names.getJSONObject(position).getString("img"))
                    .into(img);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return convertView;
    }

}
