package com.nttn.coolandroid.learnui.uiadvance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.learnui.widget.drag.DefaultItemCallback;
import com.nttn.coolandroid.learnui.widget.drag.SwipeItemLayout;
import com.nttn.coolandroid.tool.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class DragDemoActivity extends BaseHeadActivity {
    private static final String TAG = "DragDemoActivity";
    public static final String INTENT_KEY = "menuSettings";
    private RecyclerView rcv_drag_demo;
    /**
     * 管理拖拽、侧滑删除；默认不支持
     *
     * @see DefaultItemCallback#setLongPressDragEnabled(boolean)
     * @see DefaultItemCallback#setItemViewSwipeEnabled(boolean)
     */
    private DefaultItemCallback mItemCallback;

    /**
     * 自定义OnItemTouchListener，实现 RecyclerView 侧滑
     *
     * @see RecyclerView#addOnItemTouchListener(RecyclerView.OnItemTouchListener)
     * @see RecyclerView#removeOnItemTouchListener(RecyclerView.OnItemTouchListener)
     */
    private SwipeItemLayout.OnSwipeItemTouchListener mItemTouchListener;

    public static void actionStart(Context context, boolean...menuSettings){
        Intent intent = new Intent(context, DragDemoActivity.class);
        intent.putExtra(INTENT_KEY, menuSettings);
        context.startActivity(intent);
    }

    @Override
    public int getTitleResId() {
        return R.string.ui_drag;
    }

    @Override
    public int setMenuId() {
        return R.menu.menu_ui_advance;
    }

    @Override
    public Toolbar.OnMenuItemClickListener setOnMenuItemClickListener() {
        return new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return dealMenuItem(menuItem);
            }
        };
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_drag_demo;
    }

    @Override
    public void initView() {
        rcv_drag_demo = findViewById(R.id.rcv_drag_demo);
    }

    @Override
    public void initData() {
        DragAdapter adapter = new DragAdapter(mContext, dataFactory());
        rcv_drag_demo.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_drag_demo.setAdapter(adapter);
        //通过RecycleView的itemTouchHelper实现拖拽
        mItemCallback = new DefaultItemCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mItemCallback);
        itemTouchHelper.attachToRecyclerView(rcv_drag_demo);
        //初始化 默认不支持
        mItemCallback.setItemViewSwipeEnabled(false);
        mItemCallback.setLongPressDragEnabled(false);
        //通过自定义布局实现侧滑
        mItemTouchListener = new SwipeItemLayout.OnSwipeItemTouchListener(mContext);
        //menu check setting
        boolean[] arrayExtra = getIntent().getBooleanArrayExtra(INTENT_KEY);
        Menu menu = getToolbar().getMenu();
        for (int i = 0, len = arrayExtra.length; i < len; i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setChecked(!arrayExtra[i]);
            dealMenuItem(menuItem);
        }
    }

    private List<String> dataFactory(){
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(String.format("测试数据 %s", i));
        }
        return data;
    }

    private boolean dealMenuItem(MenuItem menuItem) {
        boolean itemChecked = menuItem.isChecked();
        LogUtil.d("dragDemo", "item: " + menuItem.getTitle() + "__checked: " + itemChecked);
        menuItem.setChecked(!itemChecked);
        switch (menuItem.getItemId()) {
            case R.id.item_drag:
                mItemCallback.setLongPressDragEnabled(!itemChecked);
                return true;
            case R.id.item_swipe_delete:
                mItemCallback.setItemViewSwipeEnabled(!itemChecked);
                return true;
            case R.id.item_swipe:
                if (!itemChecked) {
                    rcv_drag_demo.addOnItemTouchListener(mItemTouchListener);
                } else {
                    rcv_drag_demo.removeOnItemTouchListener(mItemTouchListener);
                }
                return true;
            default:
                return false;
        }
    }
}
