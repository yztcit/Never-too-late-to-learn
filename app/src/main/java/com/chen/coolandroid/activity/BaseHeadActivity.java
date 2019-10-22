package com.chen.coolandroid.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chen.coolandroid.R;

/**
 * 公共头部基类
 * 思路：重写 setContentView, 把 customView 通过addView()添加进去
 */
public abstract class BaseHeadActivity extends AppCompatActivity implements View.OnClickListener {
    protected Context mContext;
    private Toolbar toolbar;
    private Toolbar.OnMenuItemClickListener onMenuItemClickListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //customView layout resId
        setContentView(setContentViewId());
        //******* customView & data ***********
        initView();
        initData();
        //******* customView & data ***********
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_base_head);
        iniTitle();
        initContentView(layoutResID);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        }
    }

    /**
     *  init common title, load resId
     *
     *  @see #setTitleResId()
     */
    private void iniTitle() {
        toolbar = findViewById(R.id.toolbar);
        int menuId = setMenuId();
        if (menuId != 0) {
            toolbar.inflateMenu(menuId);
        }
        TextView title = findViewById(R.id.title);

        int titleResId = setTitleResId();
        if (titleResId != 0) {
            title.setText(titleResId);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     *  add customView layoutId to common container
     *
     * @param layoutResID customView layoutId
     *
     * @see #setContentViewId()
     */
    private void initContentView(int layoutResID){
        ViewGroup rootView = findViewById(R.id.rootView);
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
        rootView.removeAllViews();
        rootView.addView(contentView);
    }

    /**
     *  set title resId
     *
     * @return resId
     */
    protected abstract int setTitleResId();

    /**
     *  set customView layoutId
     *
     * @return layoutId
     */
    protected abstract int setContentViewId();

    /**
     * separate findView and load data
     */
    protected abstract void initView();

    protected abstract void initData();

    protected int setMenuId(){
        return 0;
    }

    public Toolbar.OnMenuItemClickListener getOnMenuItemClickListener() {
        return onMenuItemClickListener;
    }

    public void setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
        toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }
}
