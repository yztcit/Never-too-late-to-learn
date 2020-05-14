package com.chen.coolandroid.tool.networkstate;

import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Created by Apple.
 *
 * Desc: network state callback
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetworkCallbackImp extends ConnectivityManager.NetworkCallback {
    private NetworkCallback callback;

    public NetworkCallbackImp(NetworkCallback callback) {
        this.callback = callback;
    }

    public interface NetworkCallback{
        void onAvailable();
        void onLost();
        void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities);
        void onWifiAvailable();
        void onMobileAvailable();
        void onLinkPropertiesChanged(Network network, LinkProperties linkProperties);
    }

    /**
     * 网络可用的回调连接成功
     */
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        if (callback != null) {
            callback.onAvailable();
        }
    }

    /**
     * 即将断开时
     * 在网络连接正常的情况下，丢失数据会有回调
     */
    @Override
    public void onLosing(Network network, int maxMsToLive) {
        super.onLosing(network, maxMsToLive);
    }

    /**
     * 网络不可用时调用和onAvailable成对出现
     */
    @Override
    public void onLost(Network network) {
        super.onLost(network);
        if (callback != null) {
            callback.onLost();
        }
    }

    /**
     * 网络不可用
     */
    @Override
    public void onUnavailable() {
        super.onUnavailable();
    }


    /**
     * 网络功能更改，仍满足需求时调用, 可能多次调用
     * @param network The {@link Network} whose capabilities have changed.
     * @param networkCapabilities The new {@link android.net.NetworkCapabilities} for this network
     */
    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
            if (callback != null) {
                callback.onCapabilitiesChanged(network, networkCapabilities);
            }
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                if (callback != null) {
                    callback.onWifiAvailable();
                }
            } else {
                if (callback != null) {
                    callback.onMobileAvailable();
                }
            }
        }
    }

    /**
     * 网络连接属性修改时调用
     * @param network The {@link Network} whose link properties have changed.
     * @param linkProperties The new {@link LinkProperties} for this network.
     */
    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);
        if (callback != null) {
            callback.onLinkPropertiesChanged(network, linkProperties);
        }
    }
}
