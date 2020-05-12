package com.chen.coolandroid.tool.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Apple.
 * Desc: 该注解是运行时，标注在方法上的
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NetStateMonitor {
    NetworkState netState() default NetworkState.AUTO;
}
