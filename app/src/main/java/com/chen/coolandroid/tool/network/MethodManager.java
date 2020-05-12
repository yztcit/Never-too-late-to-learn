package com.chen.coolandroid.tool.network;

import java.lang.reflect.Method;

/**
 * Created by Apple.
 * Desc: 注解方法的管理类
 */
public class MethodManager {
    /**
     * 网络改变执行的方法所属的类
     */
    private Class<?> clazz;
    /**
     * 监听的网络改变类型
     */
    private NetworkState state;
    /**
     * 网络改变执行的方法
     */
    private Method method;

    public MethodManager(Class<?> clazz, NetworkState state, Method method) {
        this.clazz = clazz;
        this.state = state;
        this.method = method;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public NetworkState getState() {
        return state;
    }

    public void setState(NetworkState state) {
        this.state = state;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
