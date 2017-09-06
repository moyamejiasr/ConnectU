package com.onelio.connectu.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onelio.connectu.Helpers.ColorHelper;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {
    private List<String> values;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int item, MotionEvent event, View v);
    }
    private final OnItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout clayout;
        public TextView appName;
        public ImageView appImage;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            clayout = (LinearLayout) v.findViewById(R.id.apps_lineal_layout);
            appName = (TextView) v.findViewById(R.id.appName);
            appImage = (ImageView) v.findViewById(R.id.appImage);
        }
    }

    public AppsAdapter(Context context, OnItemClickListener listener, List<String> myDataset) {
        values = myDataset;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.view_apps_app, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final String name = values.get(position);
        holder.appName.setText(name);
        Picasso.with(context).load(ColorHelper.appGetSrc(position)).into(holder.appImage);
        holder.clayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    listener.onItemClick(position, event, v);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}