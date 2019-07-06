package test;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by CHG on 2017-02-23 20:20.
 */
public class PrintABC {
    static class Produce {

        public Object object;
        public ArrayList<Integer> list;//用list存放生产之后的数据，最大容量为1

        public Produce(Object object,ArrayList<Integer> list ){
            this.object = object;
            this.list = list;
        }

        public void produce() {

            synchronized (object) {
                /*只有list为空时才会去进行生产操作*/
                try {
                    while(!list.isEmpty()){
                        System.out.println("生产者"+Thread.currentThread().getName()+" waiting");
                        object.wait();
                    }
                    int value = 9999;
                    list.add(value);
                    System.out.println("生产者"+Thread.currentThread().getName()+" Runnable");
                    object.notifyAll();//然后去唤醒因object调用wait方法处于阻塞状态的线程
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static class Consumer {

        public Object object;
        public ArrayList<Integer> list;//用list存放生产之后的数据，最大容量为1

        public Consumer(Object object,ArrayList<Integer> list ){
            this.object = object;
            this.list = list;
        }

        public void consmer() {

            synchronized (object) {
                try {
                    /*只有list不为空时才会去进行消费操作*/
                    while(list.isEmpty()){
                        System.out.println("消费者"+Thread.currentThread().getName()+" waiting");
                        object.wait();
                    }
                    list.clear();
                    System.out.println("消费者"+Thread.currentThread().getName()+" Runnable");
                    object.notifyAll();//然后去唤醒因object调用wait方法处于阻塞状态的线程

                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    static class ProduceThread extends Thread {
        private Produce p;
        public ProduceThread(Produce p){
            this.p = p;
        }
        @Override
        public void run() {
            while (true) {
                p.produce();
            }
        }
    }
    static class ConsumeThread extends Thread {
        private Consumer c;

        public ConsumeThread(Consumer c) {
            this.c = c;
        }

        @Override
        public void run() {
            while (true) {
                c.consmer();
            }
        }
    }

        public static void main(String[] args) {
        /*Object object = new Object();
        ArrayList<Integer> list = new ArrayList<Integer>();

        Produce p = new Produce(object, list);
        Consumer c = new Consumer(object, list);

        ProduceThread[] pt = new ProduceThread[2];
        ConsumeThread[] ct = new ConsumeThread[2];

        for(int i=0;i<2;i++){
            pt[i] = new ProduceThread(p);
            pt[i].setName("生产者 "+(i+1));
            ct[i] = new ConsumeThread(c);
            ct[i].setName("消费者"+(i+1));
            pt[i].start();
            ct[i].start();
        }*/

            MyService service = new MyService();

            ProduceThread2[] pt = new ProduceThread2[2];
            ConsumeThread2[] ct = new ConsumeThread2[2];

            for(int i=0;i<1;i++){
                pt[i] = new ProduceThread2(service);
                pt[i].setName("Condition 生产者 "+(i+1));
                ct[i] = new ConsumeThread2(service);
                ct[i].setName("Condition 消费者"+(i+1));
                pt[i].start();
                ct[i].start();
            }
        }

    static class ConsumeThread2 extends Thread {
        private MyService c;
        public ConsumeThread2(MyService c){
            this.c = c;
        }
        @Override
        public void run() {
            while (true) {
                c.consmer();
            }
        }
    }

    static class ProduceThread2 extends Thread {
        private MyService p;
        public ProduceThread2(MyService p){
            this.p = p;
        }
        @Override
        public void run() {
            while (true) {
                p.produce();
            }
        }
    }

    static class MyService {

        private ReentrantLock lock = new ReentrantLock();
        private Condition condition = lock.newCondition();
        private boolean hasValue = false;


        public void produce() {
            lock.lock();
            try {
                /*只有list为空时才会去进行生产操作*/
                if(hasValue == true){
                    System.out.println("生产者"+Thread.currentThread().getName()+" waiting");
                    condition.await();
                }
                hasValue = true;
                System.out.println("生产者"+Thread.currentThread().getName()+" Runnable");
                condition.signalAll();//然后去唤醒因object调用wait方法处于阻塞状态的线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{
                lock.unlock();
            }

        }

        public void consmer() {
            lock.lock();
            try {
                /*只有list为空时才会去进行生产操作*/
                if(hasValue == false){
                    System.out.println("消费者"+Thread.currentThread().getName()+" waiting");
                    condition.await();
                }
                hasValue = false;
                System.out.println("消费者"+Thread.currentThread().getName()+" Runnable");
                condition.signalAll();//然后去唤醒因object调用wait方法处于阻塞状态的线程
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally{
                lock.unlock();
            }

        }
    }
}

