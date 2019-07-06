package com.example.myapplication;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class MyTest {

    static class Person {

        @Override
        public String toString() {
            return "Person{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }

        String name;
        int age;

        Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    public static void main(String[] args) {

        ///test();
        //test2();
        //test3();
        //testInteger();
    }

    public static void testInteger(){
        Integer int1 = new Integer(12);
        Integer int2 = new Integer(12);
        System.out.println("int1 == int2:" + (int1 == int2));//false
        System.out.println("int1.equals(int2):" + (int1.equals(int2)));//true

        Integer int3 = 12;
        Integer int4 = 12;
        System.out.println("int3 == int4:" + (int3 == int4));//在-128~127之间为true//大于128为false
        System.out.println("int3.equals(int4):" + (int3.equals(int4)));//true

        Integer int5 = new Integer(1212);
        Integer int6 = new Integer(1212);
        System.out.println("int5 == int6:" + (int5 == int6));
        System.out.println("int5.equals(int6):" + (int5.equals(int6)));

        Integer in7 = new Integer(56);

        System.out.println("in7.hashCode()" + in7.hashCode());
    }



    public static void test3(){
        byte a = 127;
        byte b = 127;

//        a+b 操作会将 a、b 提升为 int 类型，所以将 int 类型赋值给 byte 就会编译出错
//        b = a + b; // error : cannot convert from int to byte

        b += a; // ok.//+= 隐式的将加操作的结果类型强制转换为持有结果的类型
        System.out.println(3*0.1 == 0.3);//false,因为有些浮点数不能完全精确的表示出来。
    }

    public static void test2() {

        StringBuffer a, b, c;
        a = new StringBuffer("test");
        b = a;
        c = b;
        String processA = processA(a);
        String processB = processB(b);
        String processC = processC(c);
        System.out.println(processA);
        System.out.println(processB);
        System.out.println(processC);
    }

    static String processA(StringBuffer str){
        return str.append("A").toString();
    }

    static String processB(StringBuffer str){
        return str.append("B").toString();
    }

    static String processC(StringBuffer str){
        return str.append("C").toString();
    }


    public static void test() {
        Set<Person> set = new HashSet<Person>();
        Person p1 = new Person("唐僧", 25);
        Person p2 = new Person("孙悟空", 26);
        Person p3 = new Person("猪八戒", 27);
        set.add(p1);
        set.add(p2);
        set.add(p3);
        System.out.println("总共有:" + set.size() + " 个元素!"); //结果：总共有:3 个元素!
        p3.setAge(2); //修改p3的年龄,此时p3元素对应的hashcode值发生改变

        set.remove(p3); //此时remove不掉，造成内存泄漏//可以remove掉
        System.out.println("总共有:" + set.size() + " 个元素!"); //结果：总共有:4 个元素!
        set.add(p3); //重新添加，居然添加成功
        System.out.println("总共有:" + set.size() + " 个元素!"); //结果：总共有:4 个元素!
        for (Person person : set) {
            System.out.println(person);
        }
    }


}
