package com.onelio.connectu.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onelio.connectu.Containers.TutoriaData;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutoriasAdapter extends RecyclerView.Adapter<TutoriasAdapter.ViewHolder> {
    private List<TutoriaData> tutorias;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int item);
    }
    private final OnItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout llayout;
        public CircleImageView tpicture;
        public TextView date;
        public TextView title;
        public TextView subject;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            llayout = (LinearLayout) v.findViewById(R.id.tutorias_lineal_layout);
            tpicture = (CircleImageView) v.findViewById(R.id.typeD);
            date = (TextView) v.findViewById(R.id.dateD);
            title = (TextView) v.findViewById(R.id.titleD);
            subject = (TextView) v.findViewById(R.id.autorD);
        }
    }

    public TutoriasAdapter(Context context, OnItemClickListener listener, List<TutoriaData> myDataset) {
        tutorias = myDataset;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.view_tutorias_tutoria, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TutoriaData tutoria = tutorias.get(position);

        holder.title.setText(tutoria.getTitle());

        if (!tutoria.getLastModify().isEmpty()) {
            holder.date.setText(tutoria.getLastModify());
        } else {
            holder.date.setText("");
        }

        Picasso.with(context).load(tutoria.getTeacherPicture()).into(holder.tpicture);
        holder.subject.setText(AppManager.capFirstLetter(tutoria.getSubjectName()));

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
        return tutorias.size();
    }

}
