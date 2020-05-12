package com.chen.coolandroid.tool.network;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;

import com.chen.coolandroid.tool.LogUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Apple.
 *
 * Desc: 网络状态判断
 * 参考1：<a href="https://www.jianshu.com/p/86d347b2a12b"></a>
 * 参考2：<a href="https://www.jianshu.com/p/3a95523fb21b"></a>
 */
public class NetworkStateUtil implements NetworkCallbackImp.NetworkCallback,
        NetworkStateReceiver.IConnectivityChanged{
    private static final String TAG = "NetworkStateUtil >>>";
    private static final String ACTION_CONN_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private static NetworkStateUtil instance = null;
    private Application application;
    private NetworkStateReceiver receiver;
    private ConnectivityManager connManager;
    private NetworkCallbackImp networkCallback;

    private Map<Object, List<MethodManager>> netStateChangeMethodMap = new HashMap<>();

    private NetworkStateUtil() {

    }

    public static NetworkStateUtil getInstance() {
        if (instance == null) {
            synchronized (NetworkStateUtil.class) {
                if (instance == null) {
                    instance = new NetworkStateUtil();
                }
            }
        }
        return instance;
    }

    public void init(@NonNull Application application){
        this.application = application;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connManager = (ConnectivityManager) application.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            if (connManager == null) return;
            NetworkRequest request = new NetworkRequest.Builder().build();
            networkCallback = new NetworkCallbackImp(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                connManager.registerNetworkCallback(request, networkCallback);
            }
        } else {
            receiver = new NetworkStateReceiver(this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_CONN_CHANGE);
            application.registerReceiver(receiver, filter);
        }
    }

    /**
     * 注册监听
     * @param observer 执行的方法所属的类
     */
    public void register(Object observer) {
        if (observer == null) return;
        List<MethodManager> methodManagers = netStateChangeMethodMap.get(observer);
        if (methodManagers == null) {
            methodManagers = getAnnotationMethod(observer);
            netStateChangeMethodMap.put(observer, methodManagers);
        }
    }

    /**
     * 解注册
     * @param observer 执行的方法所属的类
     */
    public void unregister(Object observer) {
        if (netStateChangeMethodMap.isEmpty() || observer == null) return;
        netStateChangeMethodMap.remove(observer);
    }

    public void unregisterAll(){
        if (netStateChangeMethodMap == null) return;
        if (!netStateChangeMethodMap.isEmpty()) {
            netStateChangeMethodMap.clear();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connManager.unregisterNetworkCallback(networkCallback);
        } else {
            //解注册广播
            this.application.unregisterReceiver(receiver);
        }
        netStateChangeMethodMap = null;
    }

    private List<MethodManager> getAnnotationMethod(Object observer) {
        List<MethodManager> methodManagers = new ArrayList<>();
        Method[] methods = observer.getClass().getMethods();
        for (Method method : methods) {
            NetStateMonitor netStateMonitor = method.getAnnotation(NetStateMonitor.class);
            if (netStateMonitor == null) {
                continue;
            }
            //校验返回值和参数
            Type genericReturnType = method.getGenericReturnType();
            if (!"void".equals(genericReturnType.toString())) {
                throw new RuntimeException("the return type of method#" + method.getName() + "should be void");
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new RuntimeException("the arguments of method#" + method.getName() + "should be one");
            }
            MethodManager methodManager = new MethodManager(parameterTypes[0], netStateMonitor.netState(), method);
            methodManagers.add(methodManager);
        }
        return methodManagers;
    }

    @Override
    public void onConnChanged(Context context) {
        NetworkState networkState = getNetworkState(context);
        postNetState(networkState);
    }

    @Override
    public void onAvailable() {
        LogUtil.i(TAG, "网络连接了");
        postNetState(getNetworkState(application));
    }

    @Override
    public void onLost() {
        LogUtil.i(TAG, "网络断开了");
        postNetState(NetworkState.NONE);
    }

    @Override
    public void onWifiAvailable() {
        LogUtil.i(TAG, "WIFI连接了");
        postNetState(NetworkState.WIFI);
    }

    @Override
    public void onMobileAvailable() {
        LogUtil.i(TAG, "移动网络连接了");
        postNetState(NetworkState.MOBILE);
    }

    /**
     * 通知所有注册的方法，网络发生了改变
     * @param state The {@link NetworkState}
     */
    private void postNetState(NetworkState state) {
        Set<Object> objects = netStateChangeMethodMap.keySet();
        for (Object object : objects) {
            List<MethodManager> methodManagers = netStateChangeMethodMap.get(object);
            if (methodManagers == null) continue;
            for (MethodManager methodManager : methodManagers) {
                invoke(state, object, methodManager);
            }
        }
    }

    /**
     * 具体执行方法
     * @param state {@link NetworkState} 网络状态
     * @param object 网络改变执行的方法所属的类
     * @param methodManager 注解方法的管理类
     */
    private void invoke(NetworkState state, Object object, MethodManager methodManager) {
        try {
            if (methodManager.getClazz().isAssignableFrom(state.getClass())) {
                Method method = methodManager.getMethod();
                if (state == methodManager.getState() || state == NetworkState.NONE
                        || state == NetworkState.AUTO) {
                    method.invoke(object, state);
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static NetworkState getNetworkState(Context context) {
        //获取当前的网络服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //当前无网络
        if (connManager == null) return NetworkState.NONE;
        //获取当前网络类型：无网络、WiFi、数据连接
        NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
        if (activeNetInfo == null || activeNetInfo.isAvailable()) {
            return NetworkState.NONE;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NetworkState.WIFI;
                }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state)
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    switch (activeNetInfo.getSubtype()) {
                        //如果是2g类型
                        case TelephonyManager.NETWORK_TYPE_GPRS: // 联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: // 电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: // 移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return NetworkState.SECOND_GENERATION;
                        //如果是3g类型
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: // 电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return NetworkState.THIRD_GENERATION;
                        //如果是4g类型
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return NetworkState.FOUR_GENERATION;
                        default:
                            //中国移动 联通 电信 三种3G制式
                            if (strSubTypeName.equalsIgnoreCase("TD-SCDMA")
                                    || strSubTypeName.equalsIgnoreCase("WCDMA")
                                    || strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                                return NetworkState.THIRD_GENERATION;
                            } else {
                                return NetworkState.MOBILE;
                            }
                    }
                }
        }
        return NetworkState.NONE;
    }
}
