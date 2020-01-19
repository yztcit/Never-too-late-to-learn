package com.chen.coolandroid.learnthreads;

/**
 * Created by Apple on 2020/1/19.
 */
public class TestThread extends Thread {
    @Override
    public void run() {
        super.run();
        System.out.println("test thread");
    }
}
