package com.fanfan.novel;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2018/3/22/022.
 */

public class ThreadTest {

    private static Lock lock = new ReentrantLock();

    private static Condition subThreadCondition = lock.newCondition();
    private static boolean bBhouldSubThread = false;

    private static boolean bShouldMain = false;

    @Test
    public void main() {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        threadPool.execute(new Runnable() {
            public void run() {
                for (int i = 0; i < 50; i++) {
                    lock.lock();
                    try {
                        if (!bBhouldSubThread)
                            subThreadCondition.await();
                        for (int j = 0; j < 10; j++) {
                            System.out.println(Thread.currentThread().getName() + ",j=" + j);
                        }
                        bBhouldSubThread = false;
                        subThreadCondition.signal();
                    } catch (Exception e) {
                    } finally {
                        lock.unlock();
                    }
                }
            }

        });
        threadPool.shutdown();

        for (int i = 0; i < 50; i++) {
            lock.lock();
            try {
                if (bBhouldSubThread)
                    subThreadCondition.await();
                for (int j = 0; j < 10; j++) {
                    System.out.println(Thread.currentThread().getName() + ",j=" + j);
                }
                bBhouldSubThread = true;
                subThreadCondition.signal();
            } catch (Exception e) {
            } finally {
                lock.unlock();
            }
        }

        ////////////

//        new Thread(
//                new Runnable() {
//                    public void run() {
//                        for (int i = 0; i < 50; i++) {
//                            synchronized (ThreadTest.class) {
//                                if (bShouldMain) {
//                                    try {
//                                        ThreadTest.class.wait();
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                for (int j = 0; j < 10; j++) {
//                                    System.out.println(
//                                            Thread.currentThread().getName() +
//                                                    "i=" + i + ",j=" + j);
//                                }
//                                bShouldMain = true;
//                                ThreadTest.class.notify();
//                            }
//                        }
//                    }
//                }
//        ).start();
//
//        for (int i = 0; i < 50; i++) {
//            synchronized (ThreadTest.class) {
//                if (!bShouldMain) {
//                    try {
//                        ThreadTest.class.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                for (int j = 0; j < 5; j++) {
//                    System.out.println(Thread.currentThread().getName() + "i=" + i + ",j=" + j);
//                }
//                bShouldMain = false;
//                ThreadTest.class.notify();
//            }
//        }
    }
}
