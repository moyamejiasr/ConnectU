package com.onelio.connectu.CustomViews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import com.onelio.connectu.Helpers.ObjectHelper;

public class CustomScrollView  extends ScrollView {

    private Context context;

    public CustomScrollView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxHeight = ObjectHelper.dpToPx(context, 350);
        if(MeasureSpec.getSize(heightMeasureSpec) > maxHeight) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}