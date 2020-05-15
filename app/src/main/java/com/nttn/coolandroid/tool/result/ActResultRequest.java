package com.nttn.coolandroid.tool.result;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;

/**
 * 实现在当前方法中完成回调：
 * 把 Activity.startActivityForResult() 转为一个没有UI的Fragment的 Fragment.startActivityForResult()
 *
 * 知其然：https://blog.csdn.net/wx19930125/article/details/71176503}
 */
public class ActResultRequest {
    private static ActResultRequest instance = null;

    private ActResultRequest() {

    }

    public static synchronized ActResultRequest init(){
        if (instance == null) {
            return instance = new ActResultRequest();
        }
        return instance;
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
