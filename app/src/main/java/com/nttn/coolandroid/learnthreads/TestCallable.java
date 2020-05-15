package com.nttn.coolandroid.learnthreads;

import java.util.concurrent.Callable;

/**
 * Created by Apple on 2020/1/19.
 */
public class TestCallable implements Callable<Integer> {
    private Alipay alipay;
    private int form, to;
    private double amount;

    public TestCallable(Alipay alipay, int from, int to, double amount) {
        this.alipay = alipay;
        this.form = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public Integer call() throws Exception {
        return alipay.transfer(form, to, amount);
    }
}
