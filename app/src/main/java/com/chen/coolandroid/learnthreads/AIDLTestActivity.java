package com.chen.coolandroid.learnthreads;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.widget.TextView;

import com.chen.coolandroid.IRemoteService;
import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;
import com.chen.coolandroid.tool.LogUtil;

/**
 * Created by Apple on 2019/12/9.
 */
public class AIDLTestActivity extends BaseHeadActivity {
    private static final String TAG = "AIDLTestActivity";
    private TextView tvTest;

    @Override
    public int getTitleResId() {
        return R.string.ipc_aidl;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_aidl_test;
    }

    @Override
    public void initView() {
        tvTest = findViewById(R.id.tv_test);
    }

    @Override
    public void initData() {
        Intent intent = new Intent(mContext, RemoteService.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    private IRemoteService remoteService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取AIDL接口实例引用
            remoteService = IRemoteService.Stub.asInterface(service);
            try {
                int clientPid = Process.myPid();
                int remoteServicePid = remoteService.getPid();
                tvTest.setText(
                        String.format("Client pid = %d\nRemoteService pid = %d",
                                clientPid, remoteServicePid));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.e(TAG, "RemoteService has unexpectedly disconnected");
            remoteService = null;
        }
    };
}
