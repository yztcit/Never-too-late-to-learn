package com.nttn.coolandroid.tool;

import android.util.Log;

import com.nttn.coolandroid.BuildConfig;

/**
 * Created by Chen on 2018/4/17.
 */

public class LogUtil {
    private static final String TAG = "LogUtil";
    private static final String DEFAULT_STR = "please check your LogUtil,the state is error";

    private static final int VERBOSE = 1;
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    //if you want turn off the LogUtil,set verbal LOG_OFF;
    private static final int LOG_OFF = 6;
    private static final int LOG_ON = 0;
    private static int VERBAL = BuildConfig.IS_SHOW_LOG ? LOG_ON : LOG_OFF;

    public static void v(String tag, String msg) {
        if (VERBOSE > VERBAL) {
            Log.v(tag, msg);
        } else {
            logOff();
        }
    }

    public static void d(String tag, String msg) {
        if (DEBUG > VERBAL) {
            Log.d(tag, msg);
        } else {
            logOff();
        }
    }

    public static void i(String tag, String msg) {
        if (INFO > VERBAL) {
            Log.i(tag, msg);
        } else {
            logOff();
        }
    }

    public static void w(String tag, String msg) {
        if (WARN > VERBAL) {
            Log.w(tag, msg);
        } else {
            logOff();
        }
    }

    public static void e(String tag, String msg) {
        if (ERROR > VERBAL) {
            Log.e(tag, msg);
        } else {
            logOff();
        }
    }

    private static void logOff(){
        if (VERBAL == LOG_OFF){
            return;
        }
        Log.e(TAG, DEFAULT_STR);
    }
}
