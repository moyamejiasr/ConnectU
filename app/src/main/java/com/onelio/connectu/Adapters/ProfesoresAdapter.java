package com.onelio.connectu.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onelio.connectu.Containers.SubjectData;
import com.onelio.connectu.Containers.TeacherData;
import com.onelio.connectu.Helpers.ObjectHelper;
import com.onelio.connectu.Managers.AppManager;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfesoresAdapter extends RecyclerView.Adapter<ProfesoresAdapter.ViewHolder> {

    private List<TeacherData> data;
    private List<SubjectData> subjects;
    private Context context;

    //Group Algoritm
    private String aSubjectText = "";
    private String aSubjectId = "";
    private int aLoc = 0;

    public interface OnItemClickListener {
        void onItemClick(int item, ProfesoresAdapter.ViewHolder view);
    }
    private final ProfesoresAdapter.OnItemClickListener listener;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout llayout;
        public CircleImageView profile;
        public TextView title;
        public TextView type;
        public TextView typeName;
        public TextView typeDeliminer;
        public TextView extra;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            llayout = (LinearLayout) v.findViewById(R.id.profesores_lineal_layout);
            profile = (CircleImageView) v.findViewById(R.id.pictureD);
            title = (TextView) v.findViewById(R.id.titleD);
            type = (TextView) v.findViewById(R.id.typeD);
            typeName = (TextView) v.findViewById(R.id.typeNameD);
            typeDeliminer = (TextView) v.findViewById(R.id.typeDelimiterD);
            extra = (TextView) v.findViewById(R.id.extraD);
        }
    }

    public ProfesoresAdapter(Context context, OnItemClickListener listener, List<TeacherData> myDataset, List<SubjectData> subjects) {
        data = myDataset;
        this.subjects = subjects;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ProfesoresAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.view_profesores_profesor, parent, false);
        ProfesoresAdapter.ViewHolder vh = new ProfesoresAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ProfesoresAdapter.ViewHolder holder, final int position) {
        TeacherData teacher = data.get(position);

        holder.title.setText(AppManager.capAfterSpace(teacher.getName()));
        holder.type.setText(teacher.getSubject());
        SubjectData subject = ObjectHelper.getSubjectById(subjects, teacher.getSubject());
        if (subject != null) {
            String sname = AppManager.after(subject.getName(), teacher.getYear() + " ");
            sname = AppManager.before(sname, "(" + teacher.getSubject());
            holder.typeName.setText(AppManager.capFirstLetter(sname));
            holder.extra.setText(teacher.getEmail());
            //Set Deliminer
            if (aLoc <= position) {
                if (teacher.getSubject().equals(aSubjectId)) {
                    holder.typeDeliminer.setVisibility(View.GONE);
                } else {
                    aSubjectId = teacher.getSubject();
                    aSubjectText = sname;
                    holder.typeDeliminer.setVisibility(View.VISIBLE);
                    holder.typeDeliminer.setText(AppManager.capFirstLetter(sname));
                }
            } else {
                if (position - 1 < 0) {
                    aSubjectId = teacher.getSubject();
                    holder.typeDeliminer.setVisibility(View.VISIBLE);
                    if (position + 1 < data.size() && teacher.getSubject().equals(data.get(position + 1).getSubject())) {
                        holder.typeDeliminer.setText(AppManager.capFirstLetter(aSubjectText));
                    } else {
                        holder.typeDeliminer.setText(AppManager.capFirstLetter(sname));
                    }
                } else if (teacher.getSubject().equals(data.get(position - 1).getSubject())) {
                    holder.typeDeliminer.setVisibility(View.GONE);
                    aSubjectText = sname;
                } else {
                    aSubjectId = teacher.getSubject();
                    holder.typeDeliminer.setVisibility(View.VISIBLE);
                    if (position + 1 < data.size() && teacher.getSubject().equals(data.get(position + 1).getSubject())) {
                        holder.typeDeliminer.setText(AppManager.capFirstLetter(aSubjectText));
                    } else {
                        holder.typeDeliminer.setText(AppManager.capFirstLetter(sname));
                    }
                }
            }
            aLoc = position;
        } else {
            holder.typeName.setText("");
            holder.typeDeliminer.setVisibility(View.GONE);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Picasso.with(context).load(teacher.getPicture()).placeholder(R.drawable.ic_placeholder).into(holder.profile);
        } else {
            Picasso.with(context).load(teacher.getPicture()).placeholder(R.drawable.logo_launcher).into(holder.profile);
        }


        holder.llayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    listener.onItemClick(position, holder);
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
