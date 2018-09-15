package com.onelio.connectu.Adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.onelio.connectu.Containers.CalendarEvent;
import com.onelio.connectu.Helpers.ObjectHelper;
import com.onelio.connectu.Helpers.TimeParserHelper;
import com.onelio.connectu.R;
import java.io.IOException;
import java.util.List;

public class TimeLineAdapter extends RecyclerView.Adapter<TimeLineAdapter.ViewHolder> {
  private List<CalendarEvent> values;
  private Context context;
  private String rawPlaces;

  public interface OnItemClickListener {
    void onItemClick(int item, CalendarEvent event);
  }

  private final OnItemClickListener listener;

  public class ViewHolder extends RecyclerView.ViewHolder {
    //  each data item is just a string in this case
    public LinearLayout llayout;
    public AppCompatTextView hour;
    public AppCompatTextView subject;
    public AppCompatTextView location;
    public View layout;

    public ViewHolder(View v) {
      super(v);
      layout = v;
      llayout = (LinearLayout) v.findViewById(R.id.timeline_linear_layout);
      hour = (AppCompatTextView) v.findViewById(R.id.text_timeline_hour);
      subject = (AppCompatTextView) v.findViewById(R.id.text_timeline_subject);
      location = (AppCompatTextView) v.findViewById(R.id.text_timeline_location);
    }
  }

  public TimeLineAdapter(
      Context context, OnItemClickListener listener, List<CalendarEvent> myDataset) {
    values = myDataset;
    this.context = context;
    this.listener = listener;
    // Load future raw for places(aulas)
    try {
      rawPlaces = ObjectHelper.LoadFile("places", context);
    } catch (IOException e) {
      rawPlaces = "";
    }
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //  create a new view
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View v = inflater.inflate(R.layout.view_timeline_item, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, final int position) {
    final CalendarEvent event = values.get(position);
    String loc = ObjectHelper.getPlace(rawPlaces, event.getSigua());
    if (!loc.isEmpty()) {
      holder.subject.setText(context.getString(R.string.view_horario_loc) + " " + loc);
    } else {
      holder.subject.setText(context.getString(R.string.view_horario_loc) + " " + event.getLoc());
    }

    holder.location.setText(event.getTitle() + " " + event.getText());

    String time =
        TimeParserHelper.parseTime(event.getStart().getHours())
            + ":"
            + TimeParserHelper.parseTime(event.getStart().getMinutes())
            + "h - "
            + TimeParserHelper.parseTime(event.getEnd().getHours())
            + ":"
            + TimeParserHelper.parseTime(event.getEnd().getMinutes())
            + "h";
    holder.hour.setText(time);

    holder.llayout.setOnTouchListener(
        new View.OnTouchListener() {
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
