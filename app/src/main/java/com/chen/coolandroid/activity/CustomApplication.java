package com.chen.coolandroid.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.chen.coolandroid.tool.ArithmeticUtil;
import com.chen.coolandroid.tool.LogUtil;
import com.chen.coolandroid.tool.networkstate.NetStateMonitor;
import com.chen.coolandroid.tool.networkstate.NetworkState;
import com.chen.coolandroid.tool.networkstate.NetworkStateUtil;

/**
 * Created by Chen on 2018/4/25.
 */

public class CustomApplication extends Application {
    private static final String TAG = "CustomApplication";
    private static Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i(TAG, "onCreate");
        setContext(getApplicationContext());

        Utils.init(this);// a github open util,need init in application first;

        NetworkStateUtil.getInstance().init(this);//global network state listener

        registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
        super.onTerminate();
    }

    public static Context getAppContext(){
        return mApplicationContext;
    }

    private void setContext(Context context){
        mApplicationContext = context;
    }

    private ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LogUtil.i(TAG, "onCreated: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityStarted(Activity activity) {
            LogUtil.i(TAG, "onStarted: " + activity.getLocalClassName());
            NetworkStateUtil.getInstance().register(this);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            LogUtil.i(TAG, "onResumed: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityPaused(Activity activity) {
            LogUtil.i(TAG, "onPaused: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityStopped(Activity activity) {
            LogUtil.i(TAG, "onStopped: " + activity.getLocalClassName());
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            LogUtil.i(TAG, "onSaveInstanceState: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            LogUtil.i(TAG, "onDestroyed: " + activity.getLocalClassName());
            NetworkStateUtil.getInstance().unregister(this);
        }

        @NetStateMonitor(netStates = NetworkState.AUTO)
        public void onNetChanged(NetworkState state) {
            LogUtil.i("onNetChanged>>>", state.getStateName());
        }

        @NetStateMonitor(netStates = {NetworkState.NONE})
        public void onNetLost(NetworkState networkState) {
            ToastUtils.showShort("网络走丢了");
        }

        @NetStateMonitor(netStates = NetworkState.WIFI)
        public void onWifiAvailable(NetworkState networkState) {
            ToastUtils.showShort("正在使用WIFI，不用担心流量了");
        }

        @NetStateMonitor(netStates = {NetworkState.MOBILE})
        public void useMobileNet(NetworkState networkState) {
            ToastUtils.showShort("正在使用移动数据（" + networkState.getStateName() + "），请注意流量");
        }
    };
}
