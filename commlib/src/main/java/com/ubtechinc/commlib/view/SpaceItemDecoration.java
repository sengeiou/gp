package com.ubtechinc.commlib.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    int mSpace;
    private boolean isAround;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (isAround) {
            outRect.left = mSpace;
            outRect.right = mSpace;
        }else {
            outRect.left = 0;
            outRect.right = 0;
        }
        outRect.bottom = mSpace;
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = mSpace;
        }

    }

    public SpaceItemDecoration(int space,boolean aroundable) {
        this.mSpace = space;
        this.isAround=aroundable;
    }

    public void setAround(boolean aroundable) {
        isAround = aroundable;
    }
}