package com.onelio.connectu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onelio.connectu.API.HorarioRequest;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.CustomViews.VerticalTextView;
import com.onelio.connectu.Helpers.TimeParserHelper;
import com.onelio.connectu.R;

import java.util.Date;
import java.util.List;

public class HorarioAdapter  extends RecyclerView.Adapter<HorarioAdapter.ViewHolder> {

    private List<CalendarEvent> data;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(int item);
    }
    private final OnItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public RelativeLayout llayout;
        public RelativeLayout lastL;
        public ImageView left_lineD;
        public VerticalTextView lastT;
        public TextView hour;
        public TextView title;
        public TextView subtitle;
        public TextView text;
        public TextView loc;
        public LinearLayout lloc;
        public TextView instant;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            llayout = (RelativeLayout) v.findViewById(R.id.horario_relative_layout);
            left_lineD = (ImageView) v.findViewById(R.id.left_lineD);
            hour = (TextView) v.findViewById(R.id.hourD);
            title = (TextView) v.findViewById(R.id.titleD);
            subtitle = (TextView) v.findViewById(R.id.subtitleD);
            text = (TextView) v.findViewById(R.id.textD);
            loc = (TextView) v.findViewById(R.id.locD);
            lloc = (LinearLayout) v.findViewById(R.id.llLocD);
            instant = (TextView) v.findViewById(R.id.instD);
            lastL = (RelativeLayout) v.findViewById(R.id.lastD);
            lastT = (VerticalTextView) v.findViewById(R.id.lastTD);
        }
    }

    public HorarioAdapter(Context context, OnItemClickListener listener, List<CalendarEvent> myDataset) {
        data = myDataset;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public HorarioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.view_horario_actividad, parent, false);
        HorarioAdapter.ViewHolder vh = new HorarioAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final HorarioAdapter.ViewHolder holder, final int position) {
        CalendarEvent event = data.get(position);
        holder.title.setText(event.getTitle());
        holder.subtitle.setText(event.getSubtitle());
        holder.text.setText(event.getText());

        String minutes = TimeParserHelper.parseTime(event.getStart().getMinutes());
        String hours = TimeParserHelper.parseTime(event.getStart().getHours());
        holder.hour.setText(hours + ":" + minutes);

        if (!event.getLoc().isEmpty()) {
            holder.lloc.setVisibility(View.VISIBLE);
            holder.loc.setText(context.getString(R.string.view_horario_loc) + " " + event.getLoc());
        } else {
            holder.lloc.setVisibility(View.GONE);
        }

        if(!event.isAllDay()) {
            //Has started?
            String instant = TimeParserHelper.getDifference(context, new Date(), event.getStart());
            if (!instant.isEmpty()) {
                holder.instant.setText(context.getString(R.string.view_horario_in) + " " + instant);
            } else {
                //Is there some time until finish?
                instant = TimeParserHelper.getDifference(context, new Date(), event.getEnd());
                if (!instant.isEmpty()) {
                    holder.instant.setText(context.getString(R.string.view_horario_started) + " " + instant);
                } else {
                    instant = TimeParserHelper.parseTimeDate(context, event.getEnd());
                    holder.instant.setText(context.getString(R.string.view_horario_ended) + " " + instant);
                }
            }
        } else {
            holder.instant.setText(context.getString(R.string.view_horario_allday));
        }


        if (event.getType().equals(HorarioRequest.CALENDAR_DOCENCIA)) {
            holder.left_lineD.setBackgroundColor(Color.parseColor("#0091EA"));
            String duration = TimeParserHelper.getDifference(context, event.getStart(), event.getEnd());
            if (!duration.isEmpty()) {
                holder.lastL.setVisibility(View.VISIBLE);
                holder.lastT.setText(duration);
            } else {
                holder.lastL.setVisibility(View.INVISIBLE);
            }
        }
        if (event.getType().equals(HorarioRequest.CALENDAR_EVALUACION)) {
            holder.left_lineD.setBackgroundColor(Color.parseColor("#009688"));
            holder.lastL.setVisibility(View.INVISIBLE);
        }
        if (event.getType().equals(HorarioRequest.CALENDAR_EXAMENES)) {
            holder.left_lineD.setBackgroundColor(Color.parseColor("#F50057"));
            holder.lastL.setVisibility(View.INVISIBLE);
        }
        if (event.getType().equals(HorarioRequest.CAlENDAR_FESTIVOS)) {
            holder.left_lineD.setBackgroundColor(Color.parseColor("#FFEB3B"));
            holder.lastL.setVisibility(View.INVISIBLE);
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
        return data.size();
    }

}
