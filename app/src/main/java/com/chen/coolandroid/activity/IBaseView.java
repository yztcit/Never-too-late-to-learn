package com.chen.coolandroid.activity;

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
     * separate findView and load data
     */
    void initView();
    void initData();
}
