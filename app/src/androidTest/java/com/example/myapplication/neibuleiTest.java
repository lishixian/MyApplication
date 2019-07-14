package com.example.myapplication;

/**
 * 该类测试匿名内部类 是否有 构造器问题
 */
public class neibuleiTest {

    public static class Student {
        private String name;

        public void run() {
            System.out.println("好好学习");
        }

        public Student() {
            super();
            System.out.println("大傻逼！");
        }

        public Student(String name) {
            super();
            this.name = name;
            System.out.println("大傻逼！==" + name);
        }
    }


    public static void main(String[] args) {
        //test();
    }

    /**
     * 该方法输出如下：
     * 大傻逼！==lisx    // 首先输出了构造器
     * 我是大傻逼        // 然后输出了局部代码块
     * 我是学生，我要好好学习！// 最后执行了run方法
     *
     * 从测试可以看出来，匿名内部类的构造代码块充当了构造器的作用。  匿名类是有构造器的。
     *
     */
    public static void test() {
        Student s = new Student("lisx") {
            public void run() {
                System.out.println("我是学生，我要好好学习！");
            }

            //调用无参构造器
            {
                System.out.println("我是大傻逼");
            }
        };
        s.run();
    }


}
