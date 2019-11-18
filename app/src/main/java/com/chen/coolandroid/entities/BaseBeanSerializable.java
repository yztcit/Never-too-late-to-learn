package com.chen.coolandroid.entities;

import java.io.Serializable;

/**
 * Created by Apple on 2019/11/18.
 */
public class BaseBeanSerializable<T> implements Serializable {
    private static final long serialVersionUID = 5548106613878755474L;
    private String retCode;
    private String retMsg;
    private T data;

    public String getRetCode() {
        return retCode;
    }

    public void setRetCode(String retCode) {
        this.retCode = retCode;
    }

    public String getRetMsg() {
        return retMsg;
    }

    public void setRetMsg(String retMsg) {
        this.retMsg = retMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
