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

    /**
     * AIDL中的接口函数有时会使用in、out或者inout的参数修饰符，它们各表示什么意思？在什么情况下要使
       用呢？
     * in、out和inout表示数据的流向。大家可以把AIDL的客户端和服务端理解成两个进程（其实大多数情况
       也是这样才会使用AIDL）
     * 从客户端流向服务端用in表示，表示这个对象是从客户端中传递到服务端，在服务端修改这个对象不会
       对客户端输入的对象产生影响。
     * 而out则表示，数据只能从服务端影响客户端，即客户端输入这个参数时，服务端并不能获取到客户端的
       具体实例中的数据，而是生成一个默认数据，但是服务端对这个默认数据的修改会影响到客户端的这个
       类对象实例发生相应的改变。
     * 理解了in、out之后，inout自然不需要再解释了。AIDL默认支持的数据类型使用in修饰符，对于我们自定
       义的Parcelable对象，一般情况下我们也是使用in，如果没有必要，应该尽量避免inout。
     */
}
