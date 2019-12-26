package com.chen.coolandroid.learnthreads;

import android.content.Intent;
import android.view.View;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;

/**
 *  编号11.多线程与通信学习
 */
public class ThreadsActivity extends BaseHeadActivity implements View.OnClickListener {

    @Override
    public int getTitleResId() {
        return R.string.learn_threads;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_threads;
    }

    @Override
    public void initView() {
        findViewById(R.id.btn_ipc_test).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ipc_test) {
            Intent intent = new Intent(mContext, AIDLTestActivity.class);
            startActivity(intent);
        }
    }
}
