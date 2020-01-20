package com.chen.coolandroid.learnthreads;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 通过重入锁和条件变量模拟竞争条件
 */
public class Alipay {
    private double[] accounts;
    private Lock alipaylock;
    private Condition condition;

    public Alipay(int n, double money) {
        accounts = new double[n];
        //创建重入锁，条件变量
        alipaylock = new ReentrantLock();
        condition = alipaylock.newCondition();

        for (int i = 0; i < n; i++) {
            accounts[i] = money;
        }
    }

    public int transfer(int form, int to, double amount) throws InterruptedException{
        alipaylock.lock();
        try {
            while (accounts[form] < amount) {
                condition.await();
            }
            accounts[form] -= amount;
            accounts[to] += amount;
            condition.signalAll();
        } finally {
            alipaylock.unlock();
        }
        return 200;
    }

    public double query(int n) {
        return accounts[n];
    }

    public double[] getAccounts() {
        return accounts;
    }
}
