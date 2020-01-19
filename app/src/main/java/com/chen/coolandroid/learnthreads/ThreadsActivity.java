package com.chen.coolandroid.learnthreads;

import android.content.Intent;
import android.view.View;

import com.chen.coolandroid.R;
import com.chen.coolandroid.activity.BaseHeadActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *  编号11.多线程与通信学习
 */
public class ThreadsActivity extends BaseHeadActivity implements View.OnClickListener {

    @Override
    public int getTitleResId() {
        return R.string.learn_threads;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_threads;
    }

    @Override
    public void initView() {
        findViewById(R.id.btn_ipc_test).setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ipc_test) {
            Intent intent = new Intent(mContext, AIDLTestActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 线程的状态：<br>
     * Java线程在运行周期中可能处于6中不同的状态，即<br>
     * 1.New 新创建状态，尚未调用start();<br>
     * 2.Runnable 可运行状态，一旦调用start() 方法，线程就处于该状态，但是线程的运行取决于系统调度；<br>
     * 3.Blocked 阻塞状态；<br>
     * 4.Waiting 等待状态，直到线程调度器重新激活线程；<br>
     * 5.Timed waiting 超时等待状态，它可以在指定时间后自行返回；<br>
     * 6.Terminated 终止状态，表示线程已经执行完毕，一种是run()方法执行完成，另一种是未捕获异常终止；<br>
     * <br>
     * 多线程编程 -- 同步
     *
     * @param view 按钮
     */
    public void syncThreads(View view) {

    }

    public static void main(String[] args)  {
        //创建线程：
        //1、继承Thread类，重写run方法；
        //2、实现Runnable接口，并实现该接口的run方法；
        //3、实现Callable接口，重写call方法；
        //其实Callable接口实际是属于Executor框架中的功能类，其与Runnable接口功能类似，但是更强大，表现如下：
        // 1）Callable可以在任务结束后提供一个返回值；
        // 2）call()方法可以抛出一个异常；
        // 3）运行Callable可以拿到一个Future对象，它提供了检查任务是否完成的方法。
        // 但是调用Future的get()方法以获取结果时，当前线程会被阻塞，直到call()方法返回结果

        //----1、----
        TestThread testThread = new TestThread();
        testThread.run();
        //----2、----
        TestRunnable testRunnable = new TestRunnable();
        Thread thread = new Thread(testRunnable, "Thread-runnable");
        thread.run();
        //----3、----
        TestCallable testCallable = new TestCallable();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> future = executorService.submit(testCallable);
        try {
            System.out.println("test callable: " + future.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //总结：在这三种方式中，一般推荐实现Runnable接口的方式，其原因是，一个类应该在其需要加强时
        // 或者修改时才会被继承。因此如果没有必要重写Thread类的其他方法时，最好在这种情况下使用实现
        //Runnable接口的方式。
    }

}
