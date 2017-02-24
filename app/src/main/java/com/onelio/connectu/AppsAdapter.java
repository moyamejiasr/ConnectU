package com.onelio.connectu;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Onelio on 01/12/2016.
 */
public class AppsAdapter extends BaseAdapter {

    private final Context mContext;
    JSONArray jdata;

    // 1
    public AppsAdapter(Context context, JSONArray jdata) {
        this.mContext = context;
        this.jdata = jdata;
    }

    // 2
    @Override
    public int getCount() {
        return jdata.length();
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
            convertView = layoutInflater.inflate(R.layout.list_apps, null);
        }

        // 3
        final CardView rlayout = (CardView)convertView.findViewById(R.id.card_view);
        final TextView text = (TextView)convertView.findViewById(R.id.textview);
        final ImageView img = (ImageView) convertView.findViewById(R.id.imageView);

        // 4
        try {
            rlayout.setCardBackgroundColor(Color.parseColor(jdata.getJSONObject(position).getString("Color")));
            text.setText(jdata.getJSONObject(position).getString("Name"));
            img.setImageResource(jdata.getJSONObject(position).getInt("Img"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

}
