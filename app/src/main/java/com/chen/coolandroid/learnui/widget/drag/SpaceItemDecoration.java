package com.chen.coolandroid.learnui.widget.drag;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int row;
    private int space;

    public SpaceItemDecoration(int row, int space) {
        this.row = row;
        this.space = space;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.left = space;
        outRect.bottom = space;

        if (row == 1) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = 0;
            }
        } else {
            if (parent.getChildAdapterPosition(view) % row == 0) {
                outRect.left = 0;
            }
        }

    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

    }

}
