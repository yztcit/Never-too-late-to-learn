package com.chen.coolandroid.learnthreads;

/**
 * 通过synchronized模拟竞争条件
 * 同步方法或同步代码块
 */
public class Alipay2 {
    private double[] accounts;

    public Alipay2(int n, double money) {
        accounts = new double[n];
        for (int i = 0; i < n; i++) {
            accounts[i] = money;
        }
    }

    //同步方法
    public synchronized int transfer(int form, int to, double amount) throws InterruptedException{
        while (accounts[form] < amount) {
            wait();
        }
        accounts[form] -= amount;
        accounts[to] += amount;
        notifyAll();

        return 200;
    }

    //同步代码块
    public int transfer2(int form, int to, double amount) throws InterruptedException{
        //为了获取对象锁，实现同步代码块
        synchronized (this) {
            while (accounts[form] < amount) {
                wait();
            }
            accounts[form] -= amount;
            accounts[to] += amount;
            notifyAll();
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
