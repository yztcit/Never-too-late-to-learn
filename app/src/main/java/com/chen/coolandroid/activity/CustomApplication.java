package com.chen.coolandroid.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.blankj.utilcode.util.Utils;
import com.chen.coolandroid.tool.LogUtil;

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

        registerActivityLifecycleCallbacks(lifecycleCallbacks);
    }

    @Override
    public void onTerminate() {
        unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
        super.onTerminate();
    }

    private void setContext(Context context){
        mApplicationContext = context;
    }

    public static Context getAppContext(){
        return mApplicationContext;
    }

    private ActivityLifecycleCallbacks lifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            LogUtil.i(TAG, "onCreated: " + activity.getLocalClassName());
        }

        @Override
        public void onActivityStarted(Activity activity) {
            LogUtil.i(TAG, "onStarted: " + activity.getLocalClassName());
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
        }
    };
}
