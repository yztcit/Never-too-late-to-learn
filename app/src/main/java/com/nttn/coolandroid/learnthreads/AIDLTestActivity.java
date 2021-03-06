package com.nttn.coolandroid.learnthreads;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.widget.TextView;

import com.nttn.coolandroid.IRemoteService;
import com.nttn.coolandroid.R;
import com.nttn.coolandroid.activity.BaseHeadActivity;
import com.nttn.coolandroid.tool.LogUtil;

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

    @Override
    protected void onDestroy() {
        if (connection != null) unbindService(connection);
        super.onDestroy();
    }

    private IRemoteService remoteService;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取AIDL接口实例引用
            remoteService = IRemoteService.Stub.asInterface(service);
            try {
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                int clientPid = Process.myPid();
                int remoteServicePid = remoteService.getPid();
                String msg = "Client pid = " + clientPid + "\nRemoteService pid = " + remoteServicePid;
                tvTest.setText(msg);
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

    //死亡代理
    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            if (remoteService == null) return;

            remoteService.asBinder().unlinkToDeath(mDeathRecipient, 0);
            remoteService = null;
        }
    };
}
