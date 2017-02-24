package com.onelio.connectu.Apps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Onelio on 05/02/2017.
 */

public class NTAdapter extends BaseAdapter {
    Context context;
    List<String> names;
    List<String> imgs;

    public NTAdapter(Context context, List<String> names, List<String> imgs) {
        this.context = context;
        this.names = names;
        this.imgs = imgs;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.list_teac, null);
        }

        final CircularImageView tipo = (CircularImageView)convertView.findViewById(R.id.type);
        final TextView titulo = (TextView)convertView.findViewById(R.id.title);

        Picasso.with(context).load(imgs.get(position)).into(tipo);
        titulo.setText(names.get(position));

        return convertView;
    }
    @Override
    public int getCount() {
        return names.size();
    }
    @Override
    public Object getItem(int position) {
        return names.get(position);
    }
    @Override
    public long getItemId(int position) {
        return names.indexOf(getItem(position));
    }
}
