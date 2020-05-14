package com.chen.coolandroid.tool.networkstate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Apple.
 * Desc: 监听网络状态
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String ACTION_CONN_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private IConnectivityChanged connectivityChanged;

    public interface IConnectivityChanged {
        void onConnChanged(Context context);
    }

    public NetworkStateReceiver(IConnectivityChanged connectivityChanged) {
        this.connectivityChanged = connectivityChanged;
    }

    public NetworkStateReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }
        if (intent.getAction().equalsIgnoreCase(ACTION_CONN_CHANGE)
                && connectivityChanged != null) {
            connectivityChanged.onConnChanged(context);
        }
    }
}
