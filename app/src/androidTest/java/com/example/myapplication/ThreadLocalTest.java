package com.example.myapplication;

/**
 *
 * 验证ThreadLocal可以区分不同的线程
 *
 * 因为ThreadLocal的set方法下，将当前的ThreadLocal作为key，存的值作为value，存到了
 * 当前线程的ThreadLocalMap里。 每个线程都持有该ThreadLocal。
 *
 *
 * @ThreadLocal.set
 *     public void set(T value) {
 *         Thread t = Thread.currentThread();
 *         ThreadLocalMap map = getMap(t);
 *         if (map != null)
 *             map.set(this, value);
 *         else
 *             createMap(t, value);
 *     }
 *
 *
 * @ThreadLocalMap
 *
 * static class ThreadLocalMap {
 *      static class Entry extends WeakReference<ThreadLocal<?>> {
 *              Object value;
 *
 *             Entry(ThreadLocal<?> k, Object v) {
 *                 super(k);
 *                 value = v;
 *             }
 *         }
 *      ...
 *      ...
 * }
 *
 *
 */
public class ThreadLocalTest {

    // 创建mThreadLocal & 初始化// 它才能表现出多线程存值不冲突
    private static ThreadLocal<String> mThreadLocal = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "初始化值";
        }
    };

    // 创建mThreadLocal & 初始化// 它才能表现出多线程存值不冲突
    private static ThreadLocal<String> mThreadLocal2 = new ThreadLocal<String>() {
        @Override
        protected String initialValue() {
            return "初始化值";
        }
    };

    // 测试代码
    public static void main(String[] args) {
        MyRunnable runnable = new MyRunnable();
        new Thread(runnable, "线程1").start();
        new Thread(runnable, "线程2").start();
        new Thread(runnable, "线程3").start();
    }

    // 线程类
    public static class MyRunnable implements Runnable {
        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            mThreadLocal.set(name + "的threadLocal"); // 设置值 = 线程名
            mThreadLocal.set(name + "的threadLocal——9999999999");
            System.out.println(name + "：" + mThreadLocal.get());

            mThreadLocal2.set(name + "的threadLocal——222");
            System.out.println(name + "：" + mThreadLocal2.get());
        }
    }

}
