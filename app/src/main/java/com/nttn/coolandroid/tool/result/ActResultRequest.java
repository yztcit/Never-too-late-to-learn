package com.nttn.coolandroid.tool.result;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;

/**
 * 实现在当前方法中完成回调：
 * 把 Activity.startActivityForResult() 转为一个没有UI的Fragment的 Fragment.startActivityForResult()
 *
 * 知其然：https://blog.csdn.net/wx19930125/article/details/71176503}
 */
public class ActResultRequest {
    private static volatile ActResultRequest instance = null;

    private ActResultRequest() {

    }

    public static ActResultRequest init(){
        if (instance == null) {
            synchronized (ActResultRequest.class) {
                if (instance == null) {
                    instance = new ActResultRequest();
                }
            }
        }
        return instance;
    }

    // TODO: 处理权限
    public void requestPermissions(Activity activity, int requestCode, String... permissions) {
        getEventDispatchFragment(activity).requestPermissions(requestCode, new Callback() {
            @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data) {

            }
        }, permissions);
    }

    public void startForResult(Activity activity, Intent intent, int requestCode, Callback callback) {
        getEventDispatchFragment(activity).startForResult(intent, requestCode, callback);
    }

    public interface Callback {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    private OnActResultEventDispatcherFragment getEventDispatchFragment(Activity activity) {
        FragmentManager fragmentManager = activity.getFragmentManager();

        OnActResultEventDispatcherFragment fragment = findEventDispatchFragment(fragmentManager);
        if (fragment == null) {
            fragment = new OnActResultEventDispatcherFragment();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, OnActResultEventDispatcherFragment.TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    private OnActResultEventDispatcherFragment findEventDispatchFragment(FragmentManager manager) {
        return (OnActResultEventDispatcherFragment) manager.findFragmentByTag(OnActResultEventDispatcherFragment.TAG);
    }
}
