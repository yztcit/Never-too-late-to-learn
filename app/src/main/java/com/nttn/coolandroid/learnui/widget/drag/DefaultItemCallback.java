package com.nttn.coolandroid.learnui.widget.drag;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;


public class DefaultItemCallback extends ItemTouchHelper.Callback {

    private IItemTouchHelper touchHelper;
    /**
     *  默认值为true，即支持侧滑删除和长按拖拽
     */
    private boolean itemViewSwipeEnabled = true;
    private boolean longPressDragEnabled = true;

    public void setLongPressDragEnabled(boolean isLongPressDragEnabled) {
        longPressDragEnabled = isLongPressDragEnabled;
    }

    public boolean getLongPressDragEnabled(){
        return longPressDragEnabled;
    }

    public void setItemViewSwipeEnabled(boolean isItemViewSwipeEnabled){
        itemViewSwipeEnabled = isItemViewSwipeEnabled;
    }

    public boolean getItemViewSwipeEnabled(){
        return itemViewSwipeEnabled;
    }

    public DefaultItemCallback(IItemTouchHelper touchHelperAdapter) {
        this.touchHelper = touchHelperAdapter;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT; //允许上下左右的拖动
        int swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        touchHelper.onItemMove(viewHolder, viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            touchHelper.onItemSelect(viewHolder);
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //拖动完成之后会调用，用来清除背景状态。
        if (!recyclerView.isComputingLayout())
            touchHelper.onItemClear(viewHolder);
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //对数据进行删除操作
        touchHelper.onItemSwipe(viewHolder);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        //默认返回true就是不屏蔽拖动效果，所有的Item都能被拖动。
        return longPressDragEnabled;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        //默认返回true，不对Item进行屏蔽滑动删除。
        // 如果要屏蔽的话也是重写方法再返回false，
        // 最后在通过对应的startSwipe(ViewHolder)方法实现滑动删除
        return itemViewSwipeEnabled;
    }
}
