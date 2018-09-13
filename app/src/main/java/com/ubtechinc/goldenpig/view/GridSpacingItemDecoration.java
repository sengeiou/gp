package com.ubtechinc.goldenpig.view;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 */
public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;

    private int heightSpacing, widthSpacing;

    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int widthSpacing, int heightSpacing,
                                     boolean includeEdge) {
        this.spanCount = spanCount;
        this.widthSpacing = widthSpacing;
        this.heightSpacing = heightSpacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column
        if (includeEdge) {
            outRect.left = widthSpacing - column * widthSpacing
                    / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * widthSpacing
                    / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = heightSpacing;
            }
            outRect.bottom = heightSpacing; // item bottom
        } else {
            outRect.left = column * widthSpacing
                    / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = widthSpacing - (column + 1) * widthSpacing
                    / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            outRect.top = heightSpacing; // item top
            outRect.bottom = 0;
        }
    }
}
