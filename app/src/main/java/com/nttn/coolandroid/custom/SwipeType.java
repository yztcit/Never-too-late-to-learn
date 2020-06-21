package com.nttn.coolandroid.custom;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({SwipeType.DEFAULT, SwipeType.REFRESH, SwipeType.LOAD_MORE})
@Retention(RetentionPolicy.CLASS)
public @interface SwipeType {
    int DEFAULT = 1;
    int REFRESH = 2;
    int LOAD_MORE = 3;
}
