package com.example.myapplication;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/***
 * @Time 20190511
 * @author lisx
 *
 * 这个类主要测试了反射的使用
 */
public class Ltest {

    public static abstract class Product {
        public abstract void show();
    }

    public static class ProductA extends Product {

        @Override
        public void show() {
            System.out.println("生产出了产品A");
        }
    }

    class ProductB extends Product {

        @Override
        public void show() {
            System.out.println("生产出了产品B");
        }
    }

    public static void main(String[] args) {

        //test();
        //test1();
        test2();
    }


    /**
     * 测试反射_内部类
     */
    private static void test2() {
        // 静态内部类可直接 class.newInstance
        try {
            // 1. 根据 传入的产品类名 获取 产品类类型的Class对象
            Class product_Class = Class.forName(
                    "com.example.myapplication.Ltest$ProductA");
            // 2. 通过Class对象动态创建该产品类的实例
            Product concreteProduct = (Product) product_Class.newInstance();
            concreteProduct .show();

        } catch (Exception e) {
            e.printStackTrace();
        }


        // 成员内部类需要先构造外部类
        try {
            Class outClass = Class.forName(
                    "com.example.myapplication.Ltest");

            Class innerClass = Class.forName(
                    "com.example.myapplication.Ltest$ProductB");
            // 获取非静态内部类的实例，需要先获取外部类实例
            Product b = (Product) innerClass.getDeclaredConstructors()[0].newInstance(
                    outClass.newInstance());
            b.show();
            // 另一种方式_创建实例
            Product b2 = (Product) innerClass.getDeclaredConstructor(outClass).newInstance(
                    outClass.newInstance());
            b2.show();
            // 另一种方式_调用方法
            Method method = innerClass.getDeclaredMethod("show");
            method.invoke(b);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 测试反射_基本用法
     */
    private static void test1() {

        class Student {

            private String name;

            // 无参构造函数
            public Student() {
                System.out.println("调用了无参构造函数");
            }

            // 有参构造函数
            public Student(String str) {
                System.out.println("调用了有参构造函数");
            }

            // 无参数方法
            public void setName1() {
                System.out.println("调用了无参方法：setName1（）");
            }

            // 有参数方法
            public void setName2(String str) {
                System.out.println("调用了有参方法setName2 str:" + str);
            }
        }

        try {

            // 1. 获取Student类的Class对象
            Class studentClass = Student.class;

            // 3. 通过Class对象获取Student类的name属性
            Field f = studentClass.getDeclaredField("name");
            // 4. 设置私有访问权限
            f.setAccessible(true);
            // 2. 通过Class对象创建Student类的对象
            Object mStudent = studentClass.newInstance();
            // 5. 对新创建的Student对象设置name值
            f.set(mStudent, "Carson_Ho");
            // 6. 获取新创建Student对象的的name属性 & 输出
            System.out.println(f.get(mStudent));

            //利用反射调用构造函数

            // 2.1 通过Class对象获取Constructor类对象，从而调用无参构造方法
            // 注：构造函数的调用实际上是在newInstance()，而不是在getConstructor()中调用
            Object mObj1 = studentClass.getConstructor().newInstance();
            // 2.2 通过Class对象获取Constructor类对象（传入参数类型），从而调用有参构造方法
            Object mObj2 = studentClass.getConstructor(String.class).newInstance("Carson");


            // 3.1 通过Class对象获取方法setName1（）的Method对象:需传入方法名
            // 因为该方法 = 无参，所以不需要传入参数
            Method msetName1 = studentClass.getMethod("setName1");
            // 通过Method对象调用setName1（）：需传入创建的实例
            msetName1.invoke(mStudent);
            // 3.2 通过Class对象获取方法setName2（）的Method对象:需传入方法名 & 参数类型
            Method msetName2 = studentClass.getMethod("setName2", String.class);
            // 通过Method对象调用setName2（）：需传入创建的实例 & 参数值
            msetName2.invoke(mStudent, "Carson_Ho");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 测试class_name
     */
    private static void test() {
        // 对于2个String类型对象，它们的Class对象相同
        Class<?> c1, c2, c3, c4;
        c1 = "Carson".getClass();
        c2 = null;
        try {
            c2 = Class.forName("java.lang.String");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        c3 = String.class;
        c4 = Boolean.TYPE;
        System.out.println("1=" + c1);
        System.out.println("2=" + c2);
        System.out.println("3=" + c3);
        System.out.println("4=" + c4);
    }
}
