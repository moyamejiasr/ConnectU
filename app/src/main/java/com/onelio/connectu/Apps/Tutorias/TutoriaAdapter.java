package com.onelio.connectu.Apps.Tutorias;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onelio on 16/12/2016.
 */

public class TutoriaAdapter extends BaseAdapter {

    private final Context mContext;
    List<TutoriaList> names = new ArrayList<>();

    // 1
    public TutoriaAdapter(Context context, List<TutoriaList> names) {
        this.mContext = context;
        this.names = names;
    }

    // 2
    @Override
    public int getCount() {
        return names.size();
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
            convertView = layoutInflater.inflate(R.layout.list_mat, null);
        }

        // 3
        final CircularImageView tipo = (CircularImageView)convertView.findViewById(R.id.type);
        final TextView titulo = (TextView)convertView.findViewById(R.id.title);
        final TextView fecha = (TextView)convertView.findViewById(R.id.date);
        final TextView autor = (TextView)convertView.findViewById(R.id.autor);

        if (names.get(position).getSrc().length() > 0) {
            Picasso.with(mContext).load(names.get(position).getSrc()).into(tipo);
        } else {
            tipo.setImageResource(R.mipmap.ic_launcher);
        }

        titulo.setText(names.get(position).getName());
        fecha.setText(names.get(position).getStartdate());
        autor.setText(mContext.getResources().getString(R.string.to) + " " + names.get(position).getUser());

        return convertView;
    }

}
