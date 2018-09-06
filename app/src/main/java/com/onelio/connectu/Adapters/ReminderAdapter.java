package com.onelio.connectu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onelio.connectu.API.ScheduleRequest;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.R;

import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private List<CalendarEvent> values;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int item, CalendarEvent event);
    }
    private final OnItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // Public objects
        public LinearLayout llayout;
        public View stroke;
        public ImageView icon;
        public CardView cType;
        public TextView tType;
        public TextView title;
        public TextView subtitle;
        public TextView text;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            llayout = (LinearLayout) v.findViewById(R.id.reminder_linear_layout);
            stroke = v.findViewById(R.id.strokeD);
            icon = (ImageView) v.findViewById(R.id.iconD);
            cType = (CardView) v.findViewById(R.id.cardD);
            tType = (TextView) v.findViewById(R.id.typeD);
            title = (TextView) v.findViewById(R.id.titleD);
            subtitle = (TextView) v.findViewById(R.id.subtitleD);
            text = (TextView) v.findViewById(R.id.textD);
        }
    }

    public ReminderAdapter(Context context, OnItemClickListener listener, List<CalendarEvent> myDataset) {
        values = myDataset;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.view_reminders_reminder, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final CalendarEvent event = values.get(position);
        int color = Color.parseColor("#009688");
        String type = "Undefnied";
        if (event.getType().equals(ScheduleRequest.CALENDAR_EVALUACION)) {
            color = Color.parseColor("#00BFA5");
            type = context.getString(R.string.dialog_horario_evaluacion);
        }
        if (event.getType().equals(ScheduleRequest.CALENDAR_EXAMENES)) {
            color = Color.parseColor("#F44336");
            type = context.getString(R.string.dialog_horario_examen);
        }
        holder.stroke.setBackgroundColor(color);
        holder.icon.getDrawable().mutate().setColorFilter( color, PorterDuff.Mode.SRC_IN );
        holder.cType.setCardBackgroundColor(color);
        holder.tType.setText(type);
        holder.title.setText(event.getSubtitle());
        holder.subtitle.setText(event.getTitle());
        holder.text.setText(event.getText());
        holder.llayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent pevent) {
                if (pevent.getAction() == MotionEvent.ACTION_UP) {
                    listener.onItemClick(position, event);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

}
