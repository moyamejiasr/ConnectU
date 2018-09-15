package com.onelio.connectu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.onelio.connectu.Containers.EvaluacionData;
import com.onelio.connectu.Helpers.ColorHelper;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;
import java.util.Date;
import java.util.List;

public class EvaluacionAdapter extends RecyclerView.Adapter<EvaluacionAdapter.ViewHolder> {
  private List<EvaluacionData> events;
  private Context context;

  public interface OnItemClickListener {
    void onItemClick(int item);
  }

  private final OnItemClickListener listener;

  public class ViewHolder extends RecyclerView.ViewHolder {
    //  each data item is just a string in this case
    LinearLayout llayout;
    public CardView card;
    public TextView type;
    public TextView date;
    public TextView title;
    public TextView text;
    public TextView subtitle;
    public TextView step;
    public ImageView icon;
    public View layout;

    public ViewHolder(View v) {
      super(v);
      layout = v;
      llayout = (LinearLayout) v.findViewById(R.id.evaluacion_linear_layout);
      card = (CardView) v.findViewById(R.id.cardD);
      type = (TextView) v.findViewById(R.id.typeD);
      date = (TextView) v.findViewById(R.id.dateD);
      title = (TextView) v.findViewById(R.id.titleD);
      text = (TextView) v.findViewById(R.id.textD);
      subtitle = (TextView) v.findViewById(R.id.subtitleD);
      step = (TextView) v.findViewById(R.id.stepD);
      icon = (ImageView) v.findViewById(R.id.iconD);
    }
  }

  public EvaluacionAdapter(
      Context context, OnItemClickListener listener, List<EvaluacionData> myDataset) {
    events = myDataset;
    this.context = context;
    this.listener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //  create a new view
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View v = inflater.inflate(R.layout.view_evaluacion_tarjeta, parent, false);
    EvaluacionAdapter.ViewHolder vh = new EvaluacionAdapter.ViewHolder(v);
    return vh;
  }

  @Override
  public void onBindViewHolder(final EvaluacionAdapter.ViewHolder holder, final int position) {
    EvaluacionData data = events.get(position);

    holder.title.setText(AppManager.capAfterSpace(Html.fromHtml(data.getName()).toString()));
    String text = data.getText();
    if (text.isEmpty()) {
      holder.text.setText(context.getText(R.string.evaluacion_no_description));
    } else {
      holder.text.setText(Html.fromHtml(text));
    }
    holder.card.setCardBackgroundColor(
        Color.parseColor(ColorHelper.getColor(AppManager.after(data.getType(), " ").charAt(0))));
    holder.type.setText(AppManager.capAfterSpace(data.getType()));
    holder.subtitle.setText(
        context.getString(R.string.evaluacion_published_in)
            + " "
            + AppManager.capAfterSpace(data.getSubject()));
    String date =
        context.getString(R.string.evaluacion_from)
            + " "
            + DateFormat.format("dd", data.getStart())
            + "/"
            + DateFormat.format("MM", data.getStart())
            + "/"
            + (data.getStart().getYear() + 1900)
            + " "
            + context.getString(R.string.evaluacion_to)
            + " "
            + DateFormat.format("dd", data.getEnd())
            + "/"
            + DateFormat.format("MM", data.getStart())
            + "/"
            + (data.getEnd().getYear() + 1900);
    holder.date.setText(date);

    if (data.getTypeID().equals("2")) {
      if (!data.isOpen()) {
        Date now = new Date();
        if (now.getTime() < data.getStart().getTime()) {
          // Before
          holder
              .icon
              .getDrawable()
              .mutate()
              .setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.SRC_IN);
          holder.step.setText(context.getString(R.string.evaluacion_card_not_open));
        } else {
          // After
          holder
              .icon
              .getDrawable()
              .mutate()
              .setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.SRC_IN);
          holder.step.setText(context.getString(R.string.evaluacion_card_not_open_sended));
        }
      } else {
        if (data.isCompleted()) {
          // Opened and sended
          holder
              .icon
              .getDrawable()
              .mutate()
              .setColorFilter(Color.parseColor("#00BFA5"), PorterDuff.Mode.SRC_IN);
          holder.step.setText(context.getString(R.string.evaluacion_card_open_sended));
        } else {
          // Opened but not sended
          holder
              .icon
              .getDrawable()
              .mutate()
              .setColorFilter(Color.parseColor("#0091EA"), PorterDuff.Mode.SRC_IN);
          holder.step.setText(context.getString(R.string.evaluacion_card_open_not_sended));
        }
      }
    } else {
      holder
          .icon
          .getDrawable()
          .mutate()
          .setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.SRC_IN);
      holder.step.setText(context.getString(R.string.evaluacion_card_not_need_send));
    }

    holder.llayout.setOnTouchListener(
        new View.OnTouchListener() {
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
    return events.size();
  }
}
