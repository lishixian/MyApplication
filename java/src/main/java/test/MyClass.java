package test;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.logging.Handler;

public class MyClass {

    public static void main(String[] args) {
        //testFutureTask();
        //Integer i = 9;
        //System.out.println(9.hashCode());// int 的hashcode是其本身值
        //testFive();
        //main2();
    }

    /**
     * 找出两个没有重复的数
     *
     * 由于 A 和 B 是不一样的值，所以 A^B 的结果不为 0，也就是说，这个异或值的二进制中某一位为1。
     * 显然，A 和 B 中有且仅有一个数的相同位上也为 1。
     */
    public static void main2() {
        int[] arr = {15,1,15,16,1,7,16,3};
        ArrayList<Integer> arr1 = new ArrayList();
        ArrayList<Integer> arr2 = new ArrayList();
        int result = 0;
        for (int i = 0; i < arr.length; i++) {
            result = result ^ arr[i];
        }
        System.out.println(result);

        int tem = 1;
        while(result %2 != 1){
            tem++;
            result = result/2;
        }
        System.out.println(tem);
        //分两个数组
        for (int i = 0; i < arr.length; i++) {
            if (isIntNumberNBitONEInBinary(arr[i],tem)){
                arr1.add(arr[i]);
            }else{
                arr2.add(arr[i]);
            }
        }
        for (int i = 0; i < arr1.size(); i++) {
            System.out.print(arr1.get(i) + " ");
        }
        System.out.println( " -----------  ");
        for (int i = 0; i < arr2.size(); i++) {
            System.out.print(arr2.get(i) + " ");
        }
        System.out.println( " -----------  ");
        result = 0;
        for (int i = 0; i < arr1.size(); i++) {
            result = result ^ arr1.get(i);
        }
        System.out.println(result);
        result = 0;
        for (int i = 0; i < arr2.size(); i++) {
            result = result ^ arr2.get(i);
        }
        System.out.println(result);
//        SpringApplication.run(PrintApplication.class, args);
    }

    private static boolean isIntNumberNBitONEInBinary(int number, int nbit) {
        boolean result = false;
/*        if((number%(Math.pow(2, nbit)))/(Math.pow(2, nbit-1)) >= 1.0){
            result = true;
        }*/
        if ((number / Math.pow(2, nbit - 1) % 2) >= 1) {
            result = true;
        }
        return result;
    }


    // Thread1、Thread2、Thread3、Thread4四条线程分别统计C、D、E、F四个盘的大小，所有线程都统计完毕交给Thread5线程去做汇总
    private static void testFive(){
        FiveThreadJoinMethodUtils ft=new FiveThreadJoinMethodUtils();
        Thread t1=new Thread(ft,"我是线程A");
        Thread t2=new Thread(ft,"我是线程B");
        Thread t3=new Thread(ft,"我是线程C");
        Thread t4=new Thread(ft,"我是线程D");
        Thread t5=new Thread(ft,"我是主线程");

/*        // 使用join
        t1.start();t2.start();t3.start();t4.start();
        try {
            t1.join();
            t2.join();
            t3.join();
            t4.join();

            t5.start();
            t5.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        final CountDownLatch countDownLatch=new CountDownLatch(4);

        Runnable c=new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("我是C");
                    Thread.sleep(100);
                    countDownLatch.countDown();  //减少一个
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };

        Runnable d=new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("我是D");
                    Thread.sleep(100);
                    countDownLatch.countDown();  //减少一个
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };

        Runnable e=new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("我是E");
                    Thread.sleep(100);
                    countDownLatch.countDown();  //减少一个
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };

        Runnable f=new Runnable() {
            @Override
            public void run() {
                try{
                    System.out.println("我是F");
                    Thread.sleep(100);
                    countDownLatch.countDown();  //减少一个
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        };
        //创建一个定长线程池，可控制线程最大并发数，超出的线程会在队列中等待。
        ExecutorService service=Executors.newFixedThreadPool(4);
        service.submit(c);
        service.submit(d);
        service.submit(e);
        service.submit(f);

        try {
            countDownLatch.await();   //只要检测到当前线程为0就可以继续往下执行
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("我是主线程");
        service.shutdown();
/*        thread.join()是当前线程执行完毕之后，其他线程才可以执行
        countDownLatch()是当线程数量为0的时候，不管其他线程是否执行了，这个线程都要执行的*/


    }



    private static class FiveThreadJoinMethodUtils implements Runnable{
        @Override
        public void run() {
            System.out.println("My name :" + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("My name :" + Thread.currentThread().getName() + " end");
        }
    }

    /**
     *  如何使用Future和callAble创建线程
     */
    public static void testFutureTask(){
        FutureTask<Double> task = new FutureTask(new MyCallable());
        //创建一个线程，异步计算结果
        Thread thread = new Thread(task);
        thread.start();

        try {
            //主线程继续工作
            Thread.sleep(1000);
            System.out.println("主线程等待计算结果...");
            //当需要用到异步计算的结果时，阻塞获取这个结果
            Double d = task.get();
            System.out.println("计算结果是："+d);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class MyCallable implements Callable<Double> {

        @Override
        public Double call() {
            double d = 0;
            try {
                System.out.println("异步计算开始.......");
                d = Math.random()*10;
                d += 1000;
                Thread.sleep(2000);
                System.out.println("异步计算结束.......");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return d;//有返回值
        }
    }
}
