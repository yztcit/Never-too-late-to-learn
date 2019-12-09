package com.chen.coolandroid.learnthreads;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;

import com.chen.coolandroid.IRemoteService;

/**
 * Created by Apple on 2019/12/9.
 * ------AIDL接口练习------
 * 1.创建AIDL接口 {@link IRemoteService}；
 * 2.通过服务实现相应的接口 {@link RemoteService}；
 * 3.调用 {@link AIDLTestActivity}
 */
public class RemoteService extends Service {

    public RemoteService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    //实现AIDL接口
    private IRemoteService.Stub binder = new IRemoteService.Stub() {
        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString) throws RemoteException {

        }

        @Override
        public int getPid()throws RemoteException{
            return Process.myPid();
        }
    };
}
