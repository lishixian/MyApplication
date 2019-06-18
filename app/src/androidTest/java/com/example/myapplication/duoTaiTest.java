package com.example.myapplication;

import android.content.Intent;

/**
 * 测试多态
 */
public class duoTaiTest {

    interface f1{
        void getf();
    }

    interface f2{
        void getf();
    }

    class ff implements f1,f2{

        @Override
        public void getf() {

        }
    }

    static class  Parent{

        static{
            System.out.println("p  -  static");
        }

        {
            System.out.println("p  -  no static");
        }

        Parent(){
            System.out.println("creat p");
        }


        String value = "p";
        protected String getValue(){
            return value;
        }
    }

    static class Child extends Parent{

        static{
            System.out.println("c  -  static");
        }

        {
            System.out.println("c  -  no static");
        }

        Child(){
            System.out.println("creat c");
        }

        String value = "c";
        protected String getValue(){
            return value;
        }
    }

    static class Child2 extends Parent{

        static{
            System.out.println("c2  -  static");
        }

        {
            System.out.println("c2  -  no static");
        }

        Child2(){
            System.out.println("creat c2");
        }

        String value = "c2";
        protected String getValue(){
            return value;
        }
    }


    public static void main(String[] args) {
        test1();
    }


    public static void test2(){

    }

    public static void test1(){
        Parent p = new Child();
        Parent p2 = new Child2();
        System.out.println("value=" + ((Child) p).value+ ",get=" + p.getValue());
        System.out.println(",get="+p2.getValue());
    }

/*    p  -  static      // 父类静态代码块
    c  -  static            // 子类静态代码块
    p  -  no static         //父类非静态代码块
    creat p             //父类构造器
    c  -  no static     //子类非静态代码块
    creat c             //子类构造器


    c2  -  static           // 子类静态代码块
    p  -  no static     // 父类非静态代码块二次初始化，静态的已经初始化过了
    creat p             // 父类构造器
    c2  -  no static        // 子类非静态代码块
    creat c2            // 子类构造器


    value=c,get=c//
     ,get=c2*///
}
