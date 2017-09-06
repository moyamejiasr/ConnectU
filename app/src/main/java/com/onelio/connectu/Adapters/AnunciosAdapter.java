package com.onelio.connectu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.Helpers.ColorHelper;
import com.onelio.connectu.Helpers.TimeParserHelper;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnunciosAdapter extends RecyclerView.Adapter<AnunciosAdapter.ViewHolder> {
    private JSONArray jdata;
    private Context context;



    public interface OnItemClickListener {
        void onItemClick(int item, View v);
    }
    private final AnunciosAdapter.OnItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout clayout;
        public ImageView profile;
        public TextView type;
        public TextView date;
        public TextView title;
        public TextView textD;
        public ImageView newD;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            clayout = (ConstraintLayout) v.findViewById(R.id.anuncios_constr_layout);
            profile = (ImageView) v.findViewById(R.id.imageD);
            type = (TextView) v.findViewById(R.id.typeD);
            date = (TextView) v.findViewById(R.id.dateD);
            title = (TextView) v.findViewById(R.id.titleD);
            textD = (TextView) v.findViewById(R.id.textD);
            newD = (ImageView) v.findViewById(R.id.newD);
        }
    }

    public AnunciosAdapter(Context context, AnunciosAdapter.OnItemClickListener listener, JSONArray myDataset) {
        jdata = myDataset;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public AnunciosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.view_anuncios_anuncio, parent, false);
        AnunciosAdapter.ViewHolder vh = new AnunciosAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final AnunciosAdapter.ViewHolder holder, final int position) {
        JSONObject jobject;
        String type = "Error";
        String title = "We couldn't display this Anuncio";
        String text = "error";
        String date = "0/0/0000";
        String adate = "";
        String teacher = "Error";
        boolean isNew = false;
        TextDrawable drawable = TextDrawable.builder().buildRound("E", Color.RED);
        try {
            jobject = jdata.getJSONObject(position);
            type = jobject.getString("type");
            title = jobject.getString("title");
            text = jobject.getString("text");
            date = jobject.getString("date");
            adate = jobject.getString("adate");
            teacher = jobject.getString("teacher");
            isNew = jobject.getBoolean("isNew");
            drawable = TextDrawable.builder().buildRound(String.valueOf(type.charAt(0)), Color.parseColor(ColorHelper.getColor(type.charAt(0))));
        } catch (JSONException e) {
            FirebaseCrash.report(e);
        }
        holder.profile.setImageDrawable(drawable);
        holder.type.setText(AppManager.capFirstLetter(teacher));
        holder.title.setText(title);
        holder.textD.setText(Html.fromHtml(Uri.parse(text).toString()));
        if (isNew) {
            holder.newD.setVisibility(View.VISIBLE);
        } else {
            holder.newD.setVisibility(View.INVISIBLE);
        }

        String ndate = TimeParserHelper.parseTime(context, adate);
        if (ndate.length() != 0) {
            holder.date.setText(ndate);
        } else {
            holder.date.setText(date);
        }

        holder.clayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    try {
                        jdata.getJSONObject(position).put("isNew", false);
                        holder.newD.setVisibility(View.INVISIBLE);
                        listener.onItemClick(position, v);
                    } catch (JSONException e) {
                        Toast.makeText(context, context.getString(R.string.error_casting_action), Toast.LENGTH_SHORT).show();
                        FirebaseCrash.log(jdata.toString());
                        FirebaseCrash.report(e);
                    }
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return jdata.length();
    }

}
