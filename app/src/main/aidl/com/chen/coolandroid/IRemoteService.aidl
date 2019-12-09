// IRemoteService.aidl
package com.chen.coolandroid;

// Declare any non-default types here with import statements

interface IRemoteService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    //AIDL除了支持以上基本数据类型，还支持Charsequence，List&Map（List和Map中的元素必须是AIDL支持的
    //数据类型、其他AIDL生成的接口或声明的实现Parcelable类型的数据）

    /**
     * Request the process ID of this service.
     */
    int getPid();
}
