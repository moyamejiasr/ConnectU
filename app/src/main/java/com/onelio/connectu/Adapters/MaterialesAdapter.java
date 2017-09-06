package com.onelio.connectu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onelio.connectu.Containers.MaterialData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MaterialesAdapter extends RecyclerView.Adapter<MaterialesAdapter.ViewHolder> {
    private List<MaterialData> files;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int item);
    }
    private final OnItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout llayout;
        public CircleImageView type;
        public TextView date;
        public TextView title;
        public TextView autor;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            llayout = (LinearLayout) v.findViewById(R.id.materiales_lineal_layout);
            type = (CircleImageView) v.findViewById(R.id.typeD);
            date = (TextView) v.findViewById(R.id.dateD);
            title = (TextView) v.findViewById(R.id.titleD);
            autor = (TextView) v.findViewById(R.id.autorD);
        }
    }

    public MaterialesAdapter(Context context, OnItemClickListener listener, List<MaterialData> myDataset) {
        files = myDataset;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.view_materiales_material, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        MaterialData material = files.get(position);
        holder.title.setText(material.getFileName());

        if (!material.getDate().isEmpty()) {
            holder.date.setText(AppManager.before(material.getDate(), " "));
        } else {
            holder.date.setText("");
        }

        if (!material.isFolder()) {
            holder.type.setImageResource(R.drawable.ic_file);
            holder.autor.setVisibility(View.VISIBLE);
            holder.title.setTextColor(Color.parseColor("#424242"));
            holder.autor.setText(context.getString(R.string.materiales_by) + " " + AppManager.capAfterSpace(material.getPublisherName()));
        } else {
            holder.autor.setVisibility(View.GONE);
            if (material.isAvailableFolder()) {
                holder.title.setTextColor(Color.parseColor("#424242"));
                holder.type.setImageResource(R.drawable.ic_folder_opened);
            } else {
                holder.title.setTextColor(Color.parseColor("#9E9E9E"));
                holder.type.setImageResource(R.drawable.ic_folder_closed);
            }
        }

        holder.llayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    listener.onItemClick(position);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}
