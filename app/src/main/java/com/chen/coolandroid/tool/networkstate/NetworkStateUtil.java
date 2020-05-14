package com.chen.coolandroid.tool.networkstate;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
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
 * Desc: NetworkState listener
 * Ref 1：<a href="https://www.jianshu.com/p/86d347b2a12b"></a>
 * Ref 2：<a href="https://www.jianshu.com/p/3a95523fb21b"></a>
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
    /**
     * cache methods of net state listener
     */
    private Map<Object, List<MethodManager>> netStateChangeMethodMap = new HashMap<>();
    /**
     * to avoid post frequently
     * record last {@link #postNetState(NetworkState)} time and state
     */
    private long lastPostTimeMillis = 0;
    private NetworkState lastState;
    /**
     * the time space of method {@link #postNetState(NetworkState)}
     */
    private static final long timeSpace = 300L;

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

            networkCallback = new NetworkCallbackImp(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connManager.registerDefaultNetworkCallback(networkCallback);
            } else {
                NetworkRequest request = new NetworkRequest.Builder().build();
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
            //方法没有添加 NetStateMonitor 注解不处理
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
            //参数的类型需要是 NetworkState 类型
            if (!parameterTypes[0].getName().equals(NetworkState.class.getName())) {
                throw new RuntimeException("the parameter of method#" + method.getName() + "should be NetworkState");
            }
            MethodManager methodManager = new MethodManager(
                    parameterTypes[0], netStateMonitor.netStates(), method);
            methodManagers.add(methodManager);
        }
        return methodManagers;
    }

    @Override
    public void onConnChanged(Context context) {
        if (postNetState(getNetworkState(context))) {
            LogUtil.w(TAG, "网络状态改变了");
        }
    }

    @Override
    public void onAvailable() {
        if (postNetState(getNetworkState(application))) {
            LogUtil.i(TAG, "网络连接了");
        }
    }

    @Override
    public void onLost() {
        if (postNetState(NetworkState.NONE)) {
            LogUtil.w(TAG, "网络断开了");
        }
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        LogUtil.i(TAG, "网络功能更改了");
    }

    @Override
    public void onWifiAvailable() {
        if (postNetState(NetworkState.WIFI)) {
            LogUtil.w(TAG, "WIFI连接了");
        }
    }

    @Override
    public void onMobileAvailable() {
        if (postNetState(getNetworkState(application))) {
            LogUtil.w(TAG, "移动网络连接了");
        }
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        LogUtil.i(TAG, "网络连接属性改变了");
    }

    /**
     * 通知所有注册的方法，网络发生了改变
     * @param state The {@link NetworkState}
     * @return boolean 通知成功
     */
    private boolean postNetState(NetworkState state) {
        //同一个状态在一定的时间间隔里避免重复通知
        long currentTimeMillis = System.currentTimeMillis();
        if (state == lastState && currentTimeMillis - lastPostTimeMillis <= timeSpace) {
            lastState = state;
            lastPostTimeMillis = currentTimeMillis;
            return false;
        }
        //记录上次通知的状态和时间
        lastState = state;
        lastPostTimeMillis = currentTimeMillis;
        //发送状态变化通知
        Set<Object> objects = netStateChangeMethodMap.keySet();
        for (Object object : objects) {
            List<MethodManager> methodManagers = netStateChangeMethodMap.get(object);
            if (methodManagers == null) continue;
            for (MethodManager methodManager : methodManagers) {
                invoke(state, object, methodManager);
            }
        }
        return true;
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

                NetworkState[] states = methodManager.getStates();

                for (NetworkState networkState : states) {
                    //注册了 NetworkState.AUTO ，全部监听
                    if (networkState == NetworkState.AUTO) {
                        method.invoke(object, state);
                        return;
                    }
                    //轮询通知 netStates 中的状态
                    if (state == networkState) {
                        method.invoke(object, state);
                        return;
                    } else if (networkState == NetworkState.MOBILE
                            && (state == NetworkState.SECOND_GENERATION
                                    || state == NetworkState.THIRD_GENERATION
                                    || state == NetworkState.FOUR_GENERATION
                                    || state == NetworkState.FIVE_GENERATION)) {
                        method.invoke(object, state);
                        return;
                    }
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
        if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
            return NetworkState.NONE;
        }

        // 判断是不是连接的是不是wifi
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo) {
            NetworkInfo.State state = wifiInfo.getState();
            if (null != state) {
                if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                    return NetworkState.WIFI;
                }
            }
        }

        // 如果不是wifi，则判断当前连接的是运营商的哪种网络2g、3g、4g等
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (null != networkInfo) {
            NetworkInfo.State state = networkInfo.getState();
            String strSubTypeName = networkInfo.getSubtypeName();
            if (null != state) {
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
                        //如果是5G类型
                        // TelephonyManager.NETWORK_TYPE_NR(20) which is added in SDK 29 Android 10.0
                        case 20:
                            return NetworkState.FIVE_GENERATION;
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
        }
        return NetworkState.NONE;
    }
}
