package com.onelio.connectu.Helpers;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.onelio.connectu.R;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(Context context) {
        this.space = context.getResources().getDimensionPixelSize(R.dimen.spacing);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        //outRect.top = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) < 3) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}
