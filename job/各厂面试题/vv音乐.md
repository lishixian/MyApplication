vv音乐(有笔试)
笔试题很多
# sax解析xml的优点
不需要像dom解析那样在内存中建立一个dom对象，占用内存，sax解析是逐行解析的，每次读入内存的只是一行xml，
所以速度快，效率高点。不过sax一般是处理固定格式的xml。
优点：不用事先调入整个文档，占用资源少。尤其在嵌入式环境，如Android,极力推荐采用SAX进行解析。
缺点：不像DOM一样将文档树长期留驻在内存，数据不是长久的。事件过后，若没保存数据，那么数据就会丢失。
使用场合：机器有性能限制，尤其是在嵌入式环境。


# Contentvalue 键值类型
内部使用hashmap存储, 基本数据类型
键值类型: key 是 String, value 是:Boolean,byte,byte[],Double,Float,short,Integer,String

# androiddvm的进程与linux的进程说法正确的是?(选择题)
D
a DVM指dalvik的虚拟机.每一个Android应用程序都在它自己的进程中运行，不一定拥有一个独立 的Dalvik虚拟机实例.而每一个DVM都是在Linux中的一个进程，所以说可以认为是同一个概念.
b DVM指dalvik的虚拟机.每一个Android应用程序都在它自己的进程中运行，不一定拥有一个独立的Dalvik虚拟机实例.而每一个DVM不一定都是在Linux 中的一个进程，所以说不是一个概念.
c DVM指dalvik的虚拟机.每一个Android应用程序都在它自己的进程中运行，都拥有一个独立的Dalvik虚拟机实例.而每一个DVM不一定都是在Linux 中的一个进程，所以说不是一个概念
d DVM指dalvik的虚拟机.每一个Android应用程序都在它自己的进程中运行，都拥有一个独立的 Dalvik虚拟机实例.而每一个DVM都是在Linux 中的一个进程，所以说可以认为是同一个概念.

# Android:gravity和android:layout_gravity的区别?
（1）.android:gravity：是对view控件本身来说的，是用来设置view本身的内容应该显示在view的什么位置，默认值是左侧。也可以用来设置布局中的控件位置
（2）.android:layout_gravity：是相对于包含改元素的父元素来说的，设置该元素在父元素的什么位置；

# assets与res/raw的区别?
res/raw中的文件会被映射到R.java文件中,访问时可以使用资源Id 不可以有目录结构
assets文件夹下的文件不会被映射到R.java中，访问时需要AssetManager类，可以创建子文件夹

# 解释layout_weight的作用
一旦View设置了该属性(假设有效的情况下)，那么该 View的宽度等于原有宽度(android:layout_width)加上剩余空间的占比！

# view如何刷新?
父View负责刷新（invalidateChild）、布局（layoutChild）显示子View。
而当子View需要刷新时，则是通知父View刷新子view来完成。
两种方式刷新：
主线程可以直接调用Invalidate()方法刷新
子线程可以直接调用postInvalidate()方法刷新。

# animation.animationlistner干什么用的?
对Animation设置监听器,Animation动画效果开始执行前，执行完毕和重复执行时可以触发监听器

# android常用布局及排版效率
线性布局（LinearLayout）：按照垂直或者水平方向布局的一种组件。
相对布局（RelativeLayout）：相对某个组件的布局方式。
帧布局（FrameLayout）：组件从屏幕左上方布局组件一层一层。
表格布局（TableLayout）：按照行列方式布局组件类似于表格。
绝对布局（AbsoluteLayout）：按坐标来布局某个组件。


# collection与collections的区别
Collection 是一个集合接口。它提供了对集合对象进行基本操作的通用接口方法
Collections 是一个包装类。它包含有各种有关集合操作的静态多态方法。

# 匿名内部类是否可以extends其他类?是否可以implement interface(接口)
匿名类本身就是通过继承类或者接口来实现的。但是不能再显式的extends 或者implements了。


# 补间动画常见的效果?有哪几个常见的插入器?
- 淡入淡出： alpha
- 位移：translate
- 缩放：scale
- 旋转： rotate

BounceInterpolator
AnticipateInterpolator
AccelerateInterpolator
LinearInterpolator
CycleInterpolator
DecelerateInterpolator

# override与overload的区别?overloaded的方法是否可以改变返回值的类型?
override: 重写, 子类覆盖父类的方法.子类覆盖父类的方法时，只能比父类抛出更少的异常.访问权限只能比父类的更大，不能更小。
overload: 重载,一个类中多个方法,参数个数,类型,顺序不一样. 不可以.

# sleep与wait有什么区别?

#在android中,请简述jni的调用过程?
1.安装和下载cygwin，下载Android NDK；
2.在ndk项目中JNI接口的设计；
3.使用C/C++实现本地方法；
4.JNI生成动态链接库.so文件；
5.将动态链接库复制到java工程，在Java工程中调用，运行Java工程即可。

# 请简述android.mk的作用,并试写一个android.mk文件(包含一个.c源文件即可)
android.mk主要用来告诉编译器，需要编译哪些c++文件，以及需要编译的外部库和一些系统库。
LOCAL_PATH:=$(call my-dir)
include $(CLEAR_VARS)       //编译静态/动态库的开始，用于去除这之前的编译环境，必须要

LOCAL_SRC_FILES:= \         //需要编译为静态/动态库的源文件
    hello.cpp \
    world.cpp \

LOCAL_SHARED_LIBRARIES := \   //编译静态/动态库需要依赖的第三方库
    lib1 \
    lib2 \
    lib3 \

LOCAL_MODULE:= libhello     //编译出的静态/动态库的名字
include $(BUILD_STATIC_LIBRARY)  //编译成静态库,使用BUILD_SHARED_LIBRARY则是编译成动态库


# 冒泡排序(代码实现)

# 猴子偷桃问题代码实现
public static int stolenPeach(int n){
        if(n == 10){
            return 1;
        }else{
            return  2*stolenPeach(n+1) + 1;
        }
    }

# 给出两个链表的头指针比如p1,p2,判断这两个链表是否相交,写出主要思路即可
首先判断两个链表本身是否有环：
/**
     * 判断是否存在环
     * 步骤：设置两个指针同时指向head，其中一个一次前进一个节点（P1），另外一个一次前进两个节点(P2)。
     * p1和p2同时走，如果其中一个遇到null，则说明没有环，如果走了N步之后，二者指向地址相同，那么说明链表存在环。
     */
    public static boolean isLoop(DataNode h) {
        DataNode p1 = h;
        DataNode p2 = h;
        while(p2.getNext() != null && p2.getNext().getNext()!=null){
            p1 = p1.getNext();
            p2 = p2.getNext().getNext();
            if(p1 == p2)
                break;
        }
        return !(p1==null||p2==null);

采用最简单直接的方法，遍历两个链表，判断第一个链表的每个结点是否在第二个链表中，时间复杂度为O(len1*len2)，耗时很大；顺序查询到第一个在第二个链表种的节点即是两个链表的交点。
/**
     * 最环情况下，判断两个链表是否相交，只需要遍历链表，判断尾节点是否相等即可。
     */
    public static boolean isJoinNoLoop(DataNode h1,DataNode h2) {
        DataNode p1 = h1;
        DataNode p2 = h2;
        while(null != p1.getNext())
            p1 = p1.getNext();
        while(null != p2.getNext())
            p2 = p2.getNext();
        return p1 == p2;
}



口头问
# 简述封装,继承,多态
1.封装

意义：防止数据被无意破坏。
如何实现：把一个对象的属性私有化，同时提供一些可以被外界访问的属性的方法。
好处： 它所封装的是自己的属性和方法，所以它是不需要依赖其他对象就可以完成自己的操作。
2.继承

好处：继承是使用已存在的类的定义作为基础建立新类的技术，新类的定义可以增加新的数据或新的功能，也可以用父类的功能，但不能选择性地继承父类。通过使用继承我们能够非常方便地复用以前的代码，能够大大的提高开发的效率。
3.多态

好处： 所谓多态就是指程序中定义的引用变量所指向的具体类型和通过该引用变量发出的方法调用在编程时并不确定，而是在程序运行期间才确定，即一个引用变量倒底会指向哪个类的实例对象，该引用变量发出的方法调用到底是哪个类中实现的方法，必须在由程序运行期间才能决定。因为在程序运行时才确定具体的类，这样，不用修改源程序代码，就可以让引用变量绑定到各种不同的类实现上，从而导致该引用调用的具体方法随之改变，即不修改程序代码就可以改变程序运行时所绑定的具体代码，让程序可以选择多个运行状态，这就是多态性。

# 强软弱虚引用的应用场合

软引用
可用场景：
创建缓存的时候，创建的对象放进缓存中，当内存不足时，JVM就会回收早先创建的对象。

弱引用-WeakReference
可用场景：
Java源码中的java.util.WeakHashMap中的key就是使用弱引用，我的理解就是，一旦我不需要某个引用，JVM会自动帮我处理它，这样我就不需要做其它操作。

虚引用-PhantomReference
虚引用的回收机制跟弱引用差不多，但是它被回收之前，会被放入ReferenceQueue中。注意哦，其它引用是被JVM回收后才被传入ReferenceQueue中的。由于这个机制，所以虚引用大多被用于引用销毁前的处理工作。还有就是，虚引用创建的时候，必须带有ReferenceQueue，使用例子：

PhantomReference<String> prf = new PhantomReference<String>(new String("str"), new ReferenceQueue<>());
可用场景：
对象销毁前的一些操作，比如说资源释放等。Object.finalize()虽然也可以做这类动作，但是这个方式即不安全又低效(传


# ReferenceQueue相关(https://blog.csdn.net/gdutxiaoxu/article/details/80738581)
Reference
主要是负责内存的一个状态，当然它还和java虚拟机，垃圾回收器打交道。Reference类首先把内存分为4种状态Active，Pending，Enqueued，Inactive。

Active，一般来说内存一开始被分配的状态都是 Active，
Pending 大概是指快要被放进队列的对象，也就是马上要回收的对象，
Enqueued 就是对象的内存已经被回收了，我们已经把这个对象放入到一个队列中，方便以后我们查询某个对象是否被回收，
Inactive就是最终的状态，不能再变为其它状态。
ReferenceQueue
引用队列，在检测到适当的可到达性更改后，垃圾回收器将已注册的引用对象添加到队列中，ReferenceQueue实现了入队（enqueue）和出队（poll），还有remove操作，内部元素head就是泛型的Reference。


# java1.8新特性(https://www.cnblogs.com/owenma/p/8600685.html)

一、接口的默认方法
   Java 8允许我们给接口添加一个非抽象的方法实现，只需要使用 default关键字即可，这个特征又叫做扩展方法，
二、Lambda 表达式
```
Collections.sort(names, new Comparator<String>() {
    @Override
    public int compare(String a, String b) {
        return b.compareTo(a);
    }
});　
```
只需要给静态方法 Collections.sort 传入一个List对象以及一个比较器来按指定顺序排列。通常做法都是创建一个匿名的比较器对象然后将其传递给sort方法。
在Java 8 中你就没必要使用这种传统的匿名对象的方式了，Java 8提供了更简洁的语法，lambda表达式：
```
Collections.sort(names, (String a, String b) -> {
    return b.compareTo(a);
});
// 或者
Collections.sort(names, (String a, String b) -> b.compareTo(a));
// 或者
Collections.sort(names, (a, b) -> b.compareTo(a));
```
三、函数式接口
Lambda表达式是如何在java的类型系统中表示的呢？每一个lambda表达式都对应一个类型，通常是接口类型。而“函数式接口”是指仅仅只包含一个抽象方法的接口，每一个该类型的lambda表达式都会被匹配到这个抽象方法。因为 默认方法 不算抽象方法，所以你也可以给你的函数式接口添加默认方法。
我们可以将lambda表达式当作任意只包含一个抽象方法的接口类型，确保你的接口一定达到这个要求，你只需要给你的接口添加 @FunctionalInterface 注解，编译器如果发现你标注了这个注解的接口有多于一个抽象方法的时候会报错的。
示例如下：
```
@FunctionalInterface
interface Converter<F, T> {
    T convert(F from);
}
Converter<String, Integer> converter = (from) -> Integer.valueOf(from);
Integer converted = converter.convert("123");
System.out.println(converted);    // 123
```
需要注意如果@FunctionalInterface如果没有指定，上面的代码也是对的。
译者注 将lambda表达式映射到一个单方法的接口上，这种做法在Java 8之前就有别的语言实现，比如Rhino JavaScript解释器，如果一个函数参数接收一个单方法的接口而你传递的是一个function，Rhino 解释器会自动做一个单接口的实例到function的适配器，典型的应用场景有 org.w3c.dom.events.EventTarget 的addEventListener 第二个参数 EventListener。

四、方法与构造函数引用
前一节中的代码还可以通过静态方法引用来表示：
```
Converter<String, Integer> converter = Integer::valueOf;
Integer converted = converter.convert("123");
System.out.println(converted);   // 123　　
```
Java 8 允许你使用 :: 关键字来传递方法或者构造函数引用，上面的代码展示了如何引用一个静态方法，我们也可以引用一个对象的方法：
```
converter = something::startsWith;
String converted = converter.convert("Java");
System.out.println(converted);    // "J"
```
五、Lambda 作用域
在lambda表达式中访问外层作用域和老版本的匿名对象中的方式很相似。你可以直接访问标记了final的外层局部变量，或者实例的字段以及静态变量。
六、访问局部变量
我们可以直接在lambda表达式中访问外层的局部变量：
但是和匿名对象不同的是，这里的变量num可以不用声明为final，
不过这里的num必须不可被后面的代码修改（即隐性的具有final的语义），例如下面的就无法编译：
```
int num = 1;
Converter<Integer, String> stringConverter =
        (from) -> String.valueOf(from + num);
num = 3;
```
七、访问对象字段与静态变量
和本地变量不同的是，lambda内部对于实例的字段以及静态变量是即可读又可写。该行为和匿名对象是一致的：
八、访问接口的默认方法
九、Date API
十、Annotation 注解


# 输出一个数组,不重复?(有点忘记题目什么意思了)
# 用四个线程计算数组和(我说用join方法,或者countdownlatch,他说用线程池即可)
CountDownLatch
synchronized (MyThread.class) {}
private class MyThread extends Thread{
		@Override
		public void run() {
			super.run();
				while(j<arr.length)
				{
					synchronized (MyThread.class) {
						if(j>=arr.length){
							return;
						}
						count+=arr[j++];
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(Thread.currentThread().getName());
					}
				}
		}
	}
//方法二的线程池实现版
	public void test4(){
		ExecutorService service=Executors.newCachedThreadPool();
		Thread myThread=new MyThread();
		for(int i=0;i<5;i++){
			service.execute(myThread);
		}
        try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(count);
	}



# 什么叫安全发布对象(多线程里面)final?
但是对于普通变量的创建，之前分析过，大致分为三个步骤：
分配内存空间
将o指向分配的内存空间
调用构造函数来初始化对象
根本原因就是JVM创建对象的过程涉及到分配空间、指针设置、数据初始化等步骤，并不是同步的，涉及到主存与缓存、处理器与寄存器等，可见性没办法得到保障
什么是安全发布，简单理解就是对象的创建能够保障在被别人使用前，已经完成了数据的构造设置，或者说一个对象在使用时，已经完成了初始化。

对于final，当你创建一个对象时，使用final关键字能够使得另一个线程不会访问到处于“部分创建”的对象
因为：当构造函数退出时，final字段的值保证对访问构造对象的其他线程可见
如果某个成员是final的，JVM规范做出如下明确的保证：
一旦对象引用对其他线程可见，则其final成员也必须正确的赋值
所以说借助于final，就如同你对对象的创建访问加锁了一般，天然的就保障了对象的安全发布。


# 策略模式和命令模式是啥?
策略模式:定义一系列算法，把它们一个个封装起来，并且使它们可以相互替换。该模式使得算法可独立于它们的客户变化。
命令模式:将一个请求封装为一个对象，从而使你可用不同的请求对客户进行参数化；对请求排队或记录请求日志，以及支持可撤销的操作。

命令模式等于菜单中的复制，移动，压缩等，而策略模式是其中一个菜单的例如复制到不同算法实现。


# 拓扑排序

# 数组和链表在中间位置的插入效率

# binder的原理

# art和dvm在gc上有啥不同?有啥改进?
一、Dalvik介绍
Dalvik VM是Android平台的核心组成部分之一，它的名字来源于冰岛一个名为Dalvik的小渔村。Dalvik VM并不是一个Java虚拟机，
它没有遵循Java虚拟机规范，不能直接执行Java的Class文件，使用的是寄存器架构而不是JVM中常见的栈架构。但是它与Java又有着
千丝万缕的联系，它执行的dex（Dalvik Executable）文件可以通过Class文件转化而来，使用Java语法编写应用程序，可以直接使
用大部分的Java API等。
2014年6月25日，Android L 正式亮相于召开的谷歌I/O大会，Android L 改动幅度较大，谷歌将直接删除Dalvik，代替它的是传闻已久的ART。

二、ART介绍
在Dalvik下，应用每次运行都需要通过即时编译器（JIT）将字节码转换为机器码，即每次都要编译加运行，这一机制并不高效，
但让应用安装比较快，而且更容易在不同硬件和架构上运行。
ART完全改变了这种做法，在应用安装时就预编译字节码到机器语言，在移除解释代码这一过程后，应用程序执行将更有效率，启动更快。

三、Dalvik与JVM的区别
1、Dalvik 基于寄存器，而 JVM 基于栈。基于寄存器的虚拟机对于编译后变大的程序来说，在它们执行的时候，花费的时间更短。
2、Java虚拟机运行java字节码，Dalvik虚拟机运行的是其专有的文件格式Dex

四、Dalvik与ART的区别
1、ART与Dalvik最大的不同在于，在启用ART模式后，系统在安装应用的时候会进行一次预编译，在安装应用程序时会先将代
码转换为机器语言存储在本地，这样在运行程序时就不会每次都进行一次编译了，执行效率也大大提升。
2、ART占用空间比Dalvik大（字节码变为机器码之后，可能会增加10%-20%），这就是“时间换空间大法”。
3、预编译也可以明显改善电池续航，因为应用程序每次运行时不用重复编译了，从而减少了 CPU 的使用频率，降低了能耗。

Dalvik和ART的GC区别
Dalvik中GC的问题如下
GC时挂起所有线程
大而连续的空间紧张
内存碎片化严重
ART
在ART中标记不需要挂起所有程序的线程：在ART中GC会要求程序在分配空间的时候标记自身的堆栈，这个过程非常短，不需要挂起所有程序的线程（解决问题1）
提供 LOS ：large object space 专供Bitmap使用，从而提高了GC的管理效率和整体性能（解决问题2）
ART里有moving collector来压缩活动对象，使得内存空间更加紧凑 （解决问题3）
Google在ART里对GC做了非常大的优化，从演示的数据里看，内存分配的效率提高了10倍，GC的效率提高了2-3倍。

通过标记时机的变更使中断和阻塞的时间更短；
通过LOS解决大对象的内存分配和存储问题；
通过moving collector来压缩内存，使内存空间更加紧凑，从而达到GC整体性能的巨大提升。





# linux和windows下进程怎么通信的?(完全不了解)
# 性能优化做过什么工作?

# 一个类实现一个接口,接口引用指向这个类对象,可以不可以调用它的tostring方法?

# 浏览器,输入url匹配,假设有一亿条url缓存,用什么数据结构匹配?

# recycleview缓存机制相比listview缓存机制有啥改进?
ListView和RecyclerView缓存机制基本一致：

1). mActiveViews和mAttachedScrap功能相似，意义在于快速重用屏幕上可见的列表项ItemView，而不需要重新createView和bindView；
2). mScrapView和mCachedViews + mReyclerViewPool功能相似，意义在于缓存离开屏幕的ItemView，目的是让即将进入屏幕的ItemView重用.
3). RecyclerView的优势在于a.mCacheViews的使用，可以做到屏幕外的列表项ItemView进入屏幕内时也无须bindView快速重用；b.mRecyclerPool可以供多个RecyclerView共同使用，在特定场景下，如viewpaper+多个列表页下有优势.客观来说，RecyclerView在特定场景下对ListView的缓存机制做了补强和完善。

2. 缓存不同：
1). RecyclerView缓存RecyclerView.ViewHolder，抽象可理解为：
View + ViewHolder(避免每次createView时调用findViewById) + flag(标识状态)；
2). ListView缓存View。
1). RecyclerView中mCacheViews(屏幕外)获取缓存时，是通过匹配pos获取目标位置的缓存，这样做的好处是，当数据源数据不变的情况下，无须重新bindView：


# 一个长度为10的arraylist和linklist,在第五条插入,哪个更快?
# 子类复写父类的equals方法,但是子类增加了一个成员变量int,请问equals方法咋整?


检测卡顿--https://www.jianshu.com/p/55c08d21e63b

Android GPU呈现模式原理及卡顿掉帧浅析- https://www.jianshu.com/p/2ad8723865cc
  -- 常见分析、定位卡顿的方案,系统工具:1. TraceView ; 2. Systrace; 3. 命令行adb shell dumpsys SurfaceFlinger --latency com...包名

  -- 第三方库方案:
  1. Matrix-TraceCanary: https://github.com/Tencent/matrix
微信的卡顿检测方案，采用的ASM插桩的方式，支持fps和堆栈获取的定位，但是需要自己根据asm插桩的方法id来自己分析堆栈，定位精确度高，性能消耗小，比较可惜的是目前没有界面展示，对代码有一定的侵入性。如果线上使用可以考虑。
  2. BlockCanaryEx: https://github.com/seiginonakama/BlockCanaryEx
主要原理是利用loop()中打印的日志，loop()中打印的日志可以看鸿洋的这篇博客Android UI性能优化 检测应用中的UI卡顿，支持方法采样，知道主线程中所有方法的执行时间和执行次数，因为需要获取cpu以及一些系统的状态，性能消耗大，不支持fps展示，尤其检测到卡顿的时候，会让界面卡顿很久。之前我们项目用的就是这个工具。
  3. fpsviewer: https://github.com/SilenceDut/fpsviewer/
利用Choreographer.FrameCallback来监控卡顿和Fps的计算，异步线程进行周期采样，当前的帧耗时超过自定义的阈值时，将帧进行分析保存，不影响正常流程的进行，待需要的时候进行展示，定位。
  fpsviewer—实时显示fps，监控Android卡顿的可视化工具，能实时显示fps,一段时间的平均帧率，以及帧率范围占比，并能获取卡顿堆栈的可视化工具。侵入性低，通过在异步线程采样获取堆栈，无代码侵入，性能消耗可忽略，对性能监控项的异常数据进行采集和分析，整理输出展示相应的堆栈，从而帮助开发者开发出更高质量的应用。



链接：https://www.zhihu.com/question/34652589/answer/90344494
要完全彻底理解这个问题，需要准备以下4方面的知识：Process/Thread，Android Binder IPC，
Handler/Looper/MessageQueue消息机制，Linux pipe/epoll机制。
总结一下楼主主要有3个疑惑：
1.Android中为什么主线程不会因为Looper.loop()里的死循环卡死？
2.没看见哪里有相关代码为这个死循环准备了一个新线程去运转？
3.Activity的生命周期这些方法这些都是在主线程里执行的吧，那这些生命周期方法是怎么实现在死循环体外能够
执行起来的？-------------------------------------------------------------------------------
-------------------------------------------------------针对这些疑惑，
@hi大头鬼hi@Rocko@陈昱全 大家回答都比较精炼，接下来我再更进一步详细地一一解答楼主的疑惑：
(1) Android中为什么主线程不会因为Looper.loop()里的死循环卡死？
这里涉及线程，先说说说进程/线程，进程：每个app运行时前首先创建一个进程，该进程是由Zygote fork出来的，
用于承载App上运行的各种Activity/Service等组件。进程对于上层应用来说是完全透明的，这也是google有意为之，
让App程序都是运行在Android Runtime。大多数情况一个App就运行在一个进程中，除非在AndroidManifest.xml
中配置Android:process属性，或通过native代码fork进程。线程：线程对应用来说非常常见，
比如每次new Thread().start都会创建一个新的线程。该线程与App所在进程之间资源共享，
从Linux角度来说进程与线程除了是否共享资源外，并没有本质的区别，都是一个task_struct结构体，
在CPU看来进程或线程无非就是一段可执行的代码，CPU采用CFS调度算法，保证每个task都尽可能公平的享有CPU时间片。
有了这么准备，再说说死循环问题：对于线程既然是一段可执行的代码，当可执行代码执行完成后，
线程生命周期便该终止了，线程退出。而对于主线程，我们是绝不希望会被运行一段时间，自己就退出，
那么如何保证能一直存活呢？简单做法就是可执行代码是能一直执行下去的，死循环便能保证不会被退出，
例如，binder线程也是采用死循环的方法，通过循环方式不同与Binder驱动进行读写操作，当然并非简单地死循环，
无消息时会休眠。但这里可能又引发了另一个问题，既然是死循环又如何去处理其他事务呢？通过创建新线程的方式。
真正会卡死主线程的操作是在回调方法onCreate/onStart/onResume等操作时间过长，会导致掉帧，
甚至发生ANR，looper.loop本身不会导致应用卡死。

(2) 没看见哪里有相关代码为这个死循环准备了一个新线程去运转？
事实上，会在进入死循环之前便创建了新binder线程，在代码ActivityThread.main()中：
public static void main(String[] args) {
        ....

        //创建Looper和MessageQueue对象，用于处理主线程的消息
        Looper.prepareMainLooper();

        //创建ActivityThread对象
        ActivityThread thread = new ActivityThread();

        //建立Binder通道 (创建新线程)
        thread.attach(false);

        Looper.loop(); //消息循环运行
        throw new RuntimeException("Main thread loop unexpectedly exited");
    }thread.attach(false)；
    便会创建一个Binder线程（具体是指ApplicationThread，Binder的服务端，用于接收系统服务AMS发送来的事件），
    该Binder线程通过Handler将Message发送给主线程，具体过程可查看 startService流程分析，这里不展开说，
    简单说Binder用于进程间通信，采用C/S架构。关于binder感兴趣的朋友，可查看我回答的另一个知乎问题：
    为什么Android要采用Binder作为IPC机制？ - Gityuan的回答另外，ActivityThread实际上并非线程，
    不像HandlerThread类，ActivityThread并没有真正继承Thread类，只是往往运行在主线程，
    该人以线程的感觉，其实承载ActivityThread的主线程就是由Zygote fork而创建的进程。
    主线程的死循环一直运行是不是特别消耗CPU资源呢？ 其实不然，这里就涉及到Linux pipe/epoll机制
    ，简单说就是在主线程的MessageQueue没有消息时，便阻塞在loop的queue.next()中的nativePollOnce()方法里，
    详情见Android消息机制1-Handler(Java层)，此时主线程会释放CPU资源进入休眠状态，
    直到下个消息到达或者有事务发生，通过往pipe管道写端写入数据来唤醒主线程工作。
    这里采用的epoll机制，是一种IO多路复用机制，可以同时监控多个描述符，当某个描述符就绪(读或写就绪)，
    则立刻通知相应程序进行读或写操作，本质同步I/O，即读写是阻塞的。 所以说，主线程大多数时候都是处于休眠状态，
    并不会消耗大量CPU资源。

    (3) Activity的生命周期是怎么实现在死循环体外能够执行起来的？
    ActivityThread的内部类H继承于Handler，通过handler消息机制，简单说Handler机制用于同一个进程的线程间
    通信。Activity的生命周期都是依靠主线程的Looper.loop，当收到不同Message时则采用相应措施：
    在H.handleMessage(msg)方法中，根据接收到不同的msg，执行相应的生命周期。
    比如收到msg=H.LAUNCH_ACTIVITY，则调用ActivityThread.handleLaunchActivity()方法，
    最终会通过反射机制，创建Activity实例，然后再执行Activity.onCreate()等方法；
    再比如收到msg=H.PAUSE_ACTIVITY，则调用ActivityThread.handlePauseActivity()方法，
    最终会执行Activity.onPause()等方法。 上述过程，我只挑核心逻辑讲，真正该过程远比这复杂。
    主线程的消息又是哪来的呢？当然是App进程中的其他线程通过Handler发送给主线程，
    请看接下来的内容：---------------------------------------------------------
    -----------------------------------------------------------------------------
    最后，从进程与线程间通信的角度，通过一张图加深大家对App运行过程的理解：
    <img src="https://pic4.zhimg.com/50/7fb8728164975ac86a2b0b886de2b872_hd.jpg" data-rawwidth="890" data-rawheight="535" class="origin_image zh-lightbox-thumb" width="890" data-original="https://pic4.zhimg.com/7fb8728164975ac86a2b0b886de2b872_r.jpg"/>system_server进程是系统进程，
java framework框架的核心载体，里面运行了大量的系统服务，比如这里提供ApplicationThreadProxy（简称ATP），
ActivityManagerService（简称AMS），这个两个服务都运行在system_server进程的不同线程中，
由于ATP和AMS都是基于IBinder接口，都是binder线程，binder线程的创建与销毁都是由binder驱动来决定的。
App进程则是我们常说的应用程序，主线程主要负责Activity/Service等组件的生命周期以及UI相关操作都运行在
这个线程； 另外，每个App进程中至少会有两个binder线程 ApplicationThread(简称AT)和
ActivityManagerProxy（简称AMP），除了图中画的线程，其中还有很多线程，
比如signal catcher线程等，这里就不一一列举。Binder用于不同进程之间通信，
由一个进程的Binder客户端向另一个进程的服务端发送事务，比如图中线程2向线程4发送事务；
而handler用于同一个进程中不同线程的通信，比如图中线程4向主线程发送消息。结合图说说Activity生命周期，
比如暂停Activity，流程如下：

线程1的AMS中调用线程2的ATP；（由于同一个进程的线程间资源共享，可以相互直接调用，但
需要注意多线程并发问题）
线程2通过binder传输到App进程的线程4；
线程4通过handler消息机制，将暂停Activity的消息发送给主线程；主线程在looper.loop()中循环遍历消息，
当收到暂停Activity的消息时，便将消息分发给ActivityThread.H.handleMessage()方法，
再经过方法的调用，最后便会调用到Activity.onPause()，当onPause()处理完后，继续循环loop下去。


