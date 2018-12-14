package com.chen.coolandroid.activity;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.Utils;

/**
 * Created by Chen on 2018/4/25.
 */

public class CustomApplication extends Application {
    private static Context mApplicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        setContext(getApplicationContext());

        Utils.init(this);// a github open util,need init in application first;
    }

    private void setContext(Context context){
        mApplicationContext = context;
    }

    public static Context getAppContext(){
        return mApplicationContext;
    }
}
