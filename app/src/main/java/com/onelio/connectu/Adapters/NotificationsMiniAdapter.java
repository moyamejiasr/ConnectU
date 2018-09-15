package com.onelio.connectu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.onelio.connectu.Helpers.ColorHelper;
import com.onelio.connectu.Helpers.NotificationsParser;
import com.onelio.connectu.R;
import java.util.List;

public class NotificationsMiniAdapter
    extends RecyclerView.Adapter<NotificationsMiniAdapter.ViewHolder> {
  private List<String> values;
  private Context context;

  public interface OnItemClickListener {
    void onItemClick(int item, String id, View v);
  }

  private final OnItemClickListener listener;

  public class ViewHolder extends RecyclerView.ViewHolder {
    //  each data item is just a string in this case
    LinearLayout llayout;
    public TextView objName;
    public ImageView objImage;
    public View layout;

    public ViewHolder(View v) {
      super(v);
      layout = v;
      llayout = (LinearLayout) v.findViewById(R.id.notifications_mini_lineal_layout);
      objName = (TextView) v.findViewById(R.id.objName);
      objImage = (ImageView) v.findViewById(R.id.objIcon);
    }
  }

  public NotificationsMiniAdapter(
      Context context, OnItemClickListener listener, List<String> myDataset) {
    values = myDataset;
    this.context = context;
    this.listener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //  create a new view
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View v = inflater.inflate(R.layout.view_notifications_mini, parent, false);
    ViewHolder vh = new ViewHolder(v);
    return vh;
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, final int position) {
    String name = NotificationsParser.getName(values.get(position));
    if (!name.isEmpty()) {
      holder.objName.setText(name);
      Drawable drawable =
          TextDrawable.builder()
              .buildRound(
                  String.valueOf(name.charAt(0)),
                  Color.parseColor(ColorHelper.getColor(name.charAt(0))));
      holder.objImage.setImageDrawable(drawable);
      holder.llayout.setOnTouchListener(
          new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              if (event.getAction() == MotionEvent.ACTION_UP) {
                listener.onItemClick(position, values.get(position), v);
              }
              return true;
            }
          });
    }
  }

  @Override
  public int getItemCount() {
    return values.size();
  }
}
