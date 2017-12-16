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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.firebase.crash.FirebaseCrash;
import com.onelio.connectu.Containers.NotaData;
import com.onelio.connectu.Helpers.ColorHelper;
import com.onelio.connectu.Helpers.TimeParserHelper;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

public class NotasAdapter extends RecyclerView.Adapter<NotasAdapter.ViewHolder> {

    private List<NotaData> data;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        LinearLayout clayout;
        LinearLayout annl;
        public ImageView count;
        public TextView type;
        public TextView ann;
        public TextView date;
        public TextView title;
        public TextView text;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            clayout = (LinearLayout) v.findViewById(R.id.notas_lineal_layout);
            annl = (LinearLayout) v.findViewById(R.id.annlD);
            ann = (TextView) v.findViewById(R.id.annD);
            count = (ImageView) v.findViewById(R.id.countD);
            type = (TextView) v.findViewById(R.id.typeD);
            date = (TextView) v.findViewById(R.id.dateD);
            title = (TextView) v.findViewById(R.id.titleD);
            text = (TextView) v.findViewById(R.id.descriptionD);
        }
    }

    public NotasAdapter(Context context, List<NotaData> myDataset) {
        data = myDataset;
        this.context = context;
    }

    @Override
    public NotasAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.view_evaluacion_notas_nota, parent, false);
        NotasAdapter.ViewHolder vh = new NotasAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final NotasAdapter.ViewHolder holder, final int position) {
        NotaData grade = data.get(position);

        String type = AppManager.capAfterSpace(grade.getType());
        String date = TimeParserHelper.parseTimeDate(context, grade.getDate());
        String title = grade.getTitle();
        String description = grade.getDescription();
        String annotations = grade.getObservations();

        int scolor;
        if (grade.getNota() < 5L) {
            scolor = Color.RED;
        } else {
            scolor = Color.GREEN;
        }

        String nota;
        if((int)grade.getNota() == grade.getNota()){ //Check if is exact
            nota = String.valueOf((int)grade.getNota());
        } else {
            nota = String.valueOf(grade.getNota());
        }
        TextDrawable drawable = TextDrawable.builder().buildRect(nota, scolor);

        holder.count.setImageDrawable(drawable);
        holder.type.setText(AppManager.capFirstLetter(type));
        holder.date.setText(date);
        holder.title.setText(title);

        if (description != null && !description.equals("null")) {
            holder.text.setVisibility(View.VISIBLE);
            holder.text.setText(description);
        } else {
            holder.text.setVisibility(View.GONE);
        }

        if (annotations != null && !annotations.equals("null")) {
            holder.annl.setVisibility(View.VISIBLE);
            holder.ann.setText(annotations);
        } else {
            holder.annl.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
