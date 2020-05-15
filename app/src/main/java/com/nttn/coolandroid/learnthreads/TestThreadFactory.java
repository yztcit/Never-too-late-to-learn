package com.nttn.coolandroid.learnthreads;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Apple on 2020/1/20.
 */
public class TestThreadFactory implements ThreadFactory {
    private final AtomicInteger mCount = new AtomicInteger(1);

    @Override
    public Thread newThread(@NonNull Runnable r) {
        return new Thread(r, "custom-thread #" + mCount.getAndIncrement());
    }
}
