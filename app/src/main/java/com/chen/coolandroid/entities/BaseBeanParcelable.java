package com.chen.coolandroid.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Apple on 2019/11/18.
 */
public class BaseBeanParcelable<T extends Parcelable> implements Parcelable {
    private String retCode;
    private String retMsg;
    private T data;

    public BaseBeanParcelable() {
    }

    public BaseBeanParcelable(Parcel in) {
        readFromParcelable(in);
    }

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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(retCode);
        dest.writeString(retMsg);
        //泛型序列化时拿不到classLoader
        //---- 当前思路：先保存泛型的标准类名，然后通过反射得到ClassLoader ----↓
        if (data == null) return;
        dest.writeString(data.getClass().getName());
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseBeanParcelable> CREATOR = new Creator<BaseBeanParcelable>() {
        @Override
        public BaseBeanParcelable createFromParcel(Parcel in) {
            return new BaseBeanParcelable(in);
        }

        @Override
        public BaseBeanParcelable[] newArray(int size) {
            return new BaseBeanParcelable[size];
        }
    };

    private void readFromParcelable(Parcel in){
        BaseBeanParcelable baseBean = new BaseBeanParcelable();
        baseBean.retCode = in.readString();
        baseBean.retMsg = in.readString();
        //---- 拿到序列化的标准类名，通过反射得到ClassLoader ----↑
        String dataName = in.readString();
        try {
            if (dataName == null) return;
            baseBean.data = in.readParcelable(Class.forName(dataName).getClassLoader());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
