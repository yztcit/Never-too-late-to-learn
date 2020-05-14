package com.chen.coolandroid.activity;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * {@link BaseHeadActivity}
 */
public interface IBaseView {
    /**
     * set title resId
     *
     * @return resId
     */
    int getTitleResId();

    /**
     * set customView layoutId
     *
     * @return layoutId
     */
    int getContentViewId();

    /**
     * set menu group id
     *
     * @return menu group id
     */
    int setMenuId();

    /**
     * set OnMenuItemClickListener, override {@link #setMenuId()} first.
     *
     * @return {@link Toolbar.OnMenuItemClickListener}
     */
    Toolbar.OnMenuItemClickListener setOnMenuItemClickListener();

    /**
     * set navigation icon resId
     *
     * @return resId of navigation icon
     */
    int setNavigationIconId();

    /**
     * set navigation icon drawable
     *
     * @return drawable of navigation icon
     */
    Drawable setNavigationIconDrawable();

    /**
     * set navigation icon OnClickListener, override {@link #setNavigationIconId()} or
     * {@link #setNavigationIconDrawable()} first.
     *
     * @return {@link View.OnClickListener}
     */
    View.OnClickListener setNavOnClickListener();

    /**
     * separate findView and load data
     */
    void initView();
    void initData();
}
