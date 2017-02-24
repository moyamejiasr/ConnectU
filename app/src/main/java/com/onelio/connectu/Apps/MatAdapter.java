package com.onelio.connectu.Apps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.onelio.connectu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Onelio on 06/12/2016.
 */
public class MatAdapter extends BaseAdapter {

    private final Context mContext;
    List<MatList> names = new ArrayList<>();

    // 1
    public MatAdapter(Context context, List<MatList> names) {
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
        final ImageView tipo = (ImageView)convertView.findViewById(R.id.type);
        final TextView titulo = (TextView)convertView.findViewById(R.id.title);
        final TextView fecha = (TextView)convertView.findViewById(R.id.date);
        final TextView autor = (TextView)convertView.findViewById(R.id.autor);

        if(names.get(position).isFolder()) {
            if(names.get(position).getisFolderClean()) {
                tipo.setImageResource(R.drawable.cfolder);
            } else {
                tipo.setImageResource(R.drawable.folder);
            }
        } else {
            tipo.setImageResource(R.drawable.file);
        }
        titulo.setText(names.get(position).getName());
        fecha.setText(names.get(position).getDate().substring(0, names.get(position).getDate().lastIndexOf(" ") + 1));
        autor.setText(names.get(position).getNamemaker());

        return convertView;
    }

}

