package com.onelio.connectu.CustomViews;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.onelio.connectu.App;
import com.onelio.connectu.R;
import com.squareup.picasso.Picasso;

public class ProfilePreference extends Preference {

    public ProfilePreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ProfilePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProfilePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfilePreference(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        App app = (App) getContext().getApplicationContext();
        TextView user = (TextView) holder.findViewById(R.id.user_Name);
        ImageView picture = (ImageView) holder.findViewById(R.id.user_Profile);
        String name = app.account.getName();
        if (name == null) { //TODO SOLVE THIS ERROR
            name = "User";
        }
        user.setText(name);
        Picasso.with(getContext()).load(app.account.getPictureURL()).placeholder(R.drawable.ic_placeholder).into(picture);
    }

}