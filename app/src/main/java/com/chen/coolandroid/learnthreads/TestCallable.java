package com.chen.coolandroid.learnthreads;

import java.util.concurrent.Callable;

/**
 * Created by Apple on 2020/1/19.
 */
public class TestCallable implements Callable<Integer> {

    @Override
    public Integer call() throws Exception {
        return 200;
    }
}
