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
