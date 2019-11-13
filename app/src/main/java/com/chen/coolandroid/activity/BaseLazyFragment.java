package com.chen.coolandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by Chen on 2019/11/13.
 *
 * 自定义一个 BaseLazyFragment 基类，利用 setUserVisibleHint 和 生命周期方法，通过对 Fragment 状态
 * 判断，进行数据加载，并将数据加载的接口提供开放出去，供子类使用。
 * 然后在子类 Fragment 中实现 requestData 方法即可。
 * 这里添加了一个 isDataLoaded 变量，目的是避免重复加载数据。
 * 考虑到有时候需要刷新数据的问题，便提供了一个用于强制刷新的参数判断。
 */
public abstract class BaseLazyFragment extends Fragment {
    protected Context mContext;
    protected Activity mActivity;
    protected boolean isViewInitiated;
    protected boolean isDataLoaded;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = getActivity();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        prepareRequestData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewInitiated = true;
        prepareRequestData();
    }

    public abstract void requestData();

    public boolean prepareRequestData(){
        return prepareRequestData(false);
    }

    public boolean prepareRequestData(boolean forceUpdate){
        if (getUserVisibleHint() && isViewInitiated && (!isDataLoaded || forceUpdate)) {
            requestData();
            isDataLoaded = true;
            return true;
        }
        return false;
    }
}
