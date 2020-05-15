package com.nttn.coolandroid.learnui.widget.drag;

import android.support.v7.widget.RecyclerView;

public interface IItemTouchHelper {

    void onItemMove(RecyclerView.ViewHolder holder, int fromPosition, int targetPosition);

    void onItemSelect(RecyclerView.ViewHolder holder);

    void onItemClear(RecyclerView.ViewHolder holder);

    void onItemSwipe(RecyclerView.ViewHolder holder);
}
