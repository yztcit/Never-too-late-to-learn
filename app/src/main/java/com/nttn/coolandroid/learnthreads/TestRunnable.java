package com.nttn.coolandroid.learnthreads;

import java.util.Arrays;

/**
 * Created by Apple on 2020/1/19.
 */
public class TestRunnable implements Runnable {
    private Alipay2 alipay;
    private int from, to;
    private double amount;

    public TestRunnable(Alipay2 alipay, int from, int to, double amount) {
        this.alipay = alipay;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public void run() {
        try {
            //System.out.println(Thread.currentThread().getName() + ": " + alipay.transfer(from, to, amount)
                    //+ ", accounts = " + Arrays.toString(alipay.getAccounts()));
            System.out.println(Thread.currentThread().getName() + ": " + alipay.transfer2(from, to, amount)
                    + ", accounts = " + Arrays.toString(alipay.getAccounts()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


    }
}
