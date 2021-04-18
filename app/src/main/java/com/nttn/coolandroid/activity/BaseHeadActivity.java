package com.nttn.coolandroid.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nttn.coolandroid.R;

/**
 * 公共头部基类
 * 思路：重写 setContentView, 把 customView 通过addView()添加进去
 * @see #setContentView(int),
 * @see #initContentView(int)
 */
public abstract class BaseHeadActivity extends AppCompatActivity implements IBaseView {
    protected Context mContext;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        //customView layout resId
        setContentView(getContentViewId());
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

    /**
     *  init common title, load resId
     *
     *  @see #setNavigationIconId()
     *  @see #setNavigationIconDrawable()
     *  @see #setMenuId()
     *  @see #getTitleResId()
     */
    private void iniTitle() {
        toolbar = findViewById(R.id.toolbar);
        //setNavigationIcon, "X" icon default
        int navigationIconId = setNavigationIconId();
        if (navigationIconId != 0) {
            toolbar.setNavigationIcon(navigationIconId);
        } else {
            Drawable drawable = setNavigationIconDrawable();
            if (drawable != null) toolbar.setNavigationIcon(drawable);
        }
        //set navigation icon click listener, default finish current activity
        View.OnClickListener onNavClickListener = setNavOnClickListener();
        if (onNavClickListener != null) {
            toolbar.setNavigationOnClickListener(onNavClickListener);
        } else {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        //set menu in Toolbar, null default
        int menuId = setMenuId();
        if (menuId != 0) {
            toolbar.inflateMenu(menuId);
        }
        Toolbar.OnMenuItemClickListener onMenuItemClickListener = setOnMenuItemClickListener();
        if (onMenuItemClickListener != null) {
            toolbar.setOnMenuItemClickListener(onMenuItemClickListener);
        }

        //set title, null default
        TextView title = findViewById(R.id.title);
        int titleResId = getTitleResId();
        if (titleResId != 0) {
            title.setText(titleResId);
        }
    }

    /**
     *  add customView layoutId to common container
     *
     * @param layoutResID customView layoutId
     * @see #getContentViewId()
     */
    private void initContentView(int layoutResID){
        ViewGroup rootView = findViewById(R.id.rootView);
        View contentView = LayoutInflater.from(this).inflate(layoutResID, null);
        rootView.removeAllViews();
        rootView.addView(contentView);
    }

    /***********************↓ override ↓*********************/
    @Override
    public int setMenuId(){
        return 0;
    }

    @Override
    public Toolbar.OnMenuItemClickListener setOnMenuItemClickListener() {
        return null;
    }

    @Override
    public int setNavigationIconId() {
        return 0;
    }

    @Override
    public Drawable setNavigationIconDrawable(){
        return null;
    }

    @Override
    public View.OnClickListener setNavOnClickListener(){
        return null;
    }
    /***********************↑ override ↑***********************/

    public Toolbar getToolbar(){
        return toolbar;
    }

    public View getRootView() {
        return ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
    }

    public View getContentView() {
        return ((ViewGroup)findViewById(R.id.rootView)).getChildAt(0);
    }
}
