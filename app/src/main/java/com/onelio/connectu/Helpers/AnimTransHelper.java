package com.onelio.connectu.Helpers;

import android.app.ActivityOptions;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.onelio.connectu.R;

public class AnimTransHelper {

    public static Bundle circleSlideUp(Context context, View v) {
        Bundle optsBundle;
        ActivityOptions opts = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int left = 0, top = 0;
            int width = v.getMeasuredWidth(), height = v.getMeasuredHeight();
            opts = ActivityOptions.makeClipRevealAnimation(v, left, top, width, height);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Below L, we use a scale up animation
            opts = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            // On L devices, we use the device default slide-up transition.
            // On L MR1 devices, we use a custom version of the slide-up transition which
            // doesn't have the delay present in the device default.
            opts = ActivityOptions.makeCustomAnimation(context, R.anim.task_open_enter, R.anim.no_anim);
        }
        optsBundle = opts != null ? opts.toBundle() : null;
        return optsBundle;
    }

}
