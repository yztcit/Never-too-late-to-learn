package com.chen.coolandroid.learnui.uiadvance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;
import com.chen.coolandroid.learnui.widget.drag.DefaultItemCallback;
import com.chen.coolandroid.learnui.widget.drag.SwipeItemLayout;

import java.util.ArrayList;
import java.util.List;

public class DragDemoActivity extends BaseHeadActivity {
    private RecyclerView rcv_drag_demo;
    /**
     *  管理拖拽、侧滑删除；默认不支持
     * 使用 setLongPressDragEnabled(boolean)
     * 使用 setItemViewSwipeEnabled(boolean)
     */
    private DefaultItemCallback mItemCallback;
    private SwipeItemLayout.OnSwipeItemTouchListener mItemTouchListener;

    public static void actionStart(Context context){
        Intent intent = new Intent(context, DragDemoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int setTitleResId() {
        return R.string.ui_drag;
    }

    @Override
    protected int setContentViewId() {
        return R.layout.activity_drag_demo;
    }

    @Override
    protected void initView() {
        rcv_drag_demo = findViewById(R.id.rcv_drag_demo);
    }

    @Override
    protected void initData() {
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
    }

    @Override
    protected int setMenuId() {
        setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                boolean itemChecked = menuItem.isChecked();
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
        });
        return R.menu.menu_ui_advance;
    }

    private List<String> dataFactory(){
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add(String.format("测试数据 %s", i));
        }
        return data;
    }
}
