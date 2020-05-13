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
    private NetworkState[] states;
    /**
     * 网络改变执行的方法
     */
    private Method method;

    public MethodManager(Class<?> clazz, NetworkState[] states, Method method) {
        this.clazz = clazz;
        this.states = states;
        this.method = method;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public NetworkState[] getStates() {
        return states;
    }

    public void setStates(NetworkState[] states) {
        this.states = states;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
