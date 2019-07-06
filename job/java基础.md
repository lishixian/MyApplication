
## java基础问题汇总
1. 访问权限有哪些，区别；
2. 进程间通信方式（IPC）；
3. jvm内存管理，垃圾回收机制；回收算法；
4. java的四个引用方式与区别；
5. java动态代理机制；
6. AIDL内部有哪些方法；
7. ArrayList 和 LinkedList 区别
8. LRUCache 是如何实现的（源码角度）？为什么要用 LinkedHashmap？
   LRUCache内部采用LinkedHashmap结构，当get的时候，LinkedHashmap会把get的元素放在队尾；
   LRUCache达到最大缓存数量时，会删除头结点，即最少使用的元素；

### 1. 访问权限有哪些，区别；

java的访问权限有四种，区别如下：

| |类内部|包内部|子类|外部包|
|:--|:-:|:-:|:-:|:-:|
|public|Y|Y|Y|Y|
|protected|Y|Y|Y|N|
|默认|Y|Y|N|N|
|private|Y|N|N|N|

### 2. 进程间通信方式（IPC）及对比；
|名称 |优点|缺点|适用场景|
|:--|:-|:-|:-|
| Bundle|简单易用 |只能传输Bundle支持的类型 |四大组件之间的进程通信 |
|文件共享|简单易用|不适合高并发场景，并且无法做到进程间即时通信| 无并发访问情形，交换简单的数据，实时性不高的场景|
|AIDL|功能强大，支持一对多并发操作、实时通信|使用复杂，需要处理好线程同步|一对多通信且有RPC需求|
|Messenger|功能一般，支持一对多串行通信、实时通信|不能很好地额处理高并发情景，不支持rpc，只能传输bundle支持的类型|低并发的一对多即时通信|
|ContentProvider|数据访问功能强大，支持一对多并发数据共享，可通过call方式扩展其他操作|可以理解为受约束的aidl，主要提供数据源CRUD操作|一对多进程间的数据共享|
|Socket|功能强大，可以通过网络传输字节流，支持一对多并发实时通信|实现细节稍微繁琐，不支持直接RPC|网络数据交换|


### 3. jvm内存管理，垃圾回收机制；回收算法；java虚拟机和Dalvik虚拟机的区别

#### 3.1 jvm模型：
||||
|:-|:-:|:-
程序计数器|线程私有 |是一小块内存空间，记录当前线程所执行的字节码行号指示器。
java虚拟机|线程私有 |java方法执行的内存模型，每个方法被执行的时候都会同时创建一个栈帧。
本地方法栈|线程私有 |Native方法
java堆|线程共享 |可以处于物理上不连续的内存空间
方法区|线程共享 |用于存储已被虚拟机加载的类信息、常量、静态变量。

#### 3.2 如何判断那些内存需要回收？
3.2.1 引用计数法
引用计数是垃圾收集器中的早期策略。在这种方法中，堆中每个对象实例都有一个引用计数。当一个对象被创建时，
且将该对象实例分配给一个变量，该变量计数设置为1。当任何其它变量被赋值为这个对象的引用时，
计数加1（a = b,则b引用的对象实例的计数器+1），但当一个对象实例的某个引用超过了生命周期或者被设置为一个新值时，
对象实例的引用计数器减1。任何引用计数器为0的对象实例可以被当作垃圾收集

优点：
引用计数收集器可以很快的执行，交织在程序运行中。对程序需要不被长时间打断的实时环境比较有利。

缺点：
无法检测出循环引用。如父对象有一个对子对象的引用，子对象反过来引用父对象。这样，他们的引用计数永远不可能为0.

#### 3.2.2 根搜索算法（可达性分析法）
根搜索算法是从离散数学中的图论引入的，程序把所有的引用关系看作一张图，从一个节点GC ROOT开始，寻找对应的引用节
点，找到这个节点以后，继续寻找这个节点的引用节点，当所有的引用节点寻找完毕之后，剩余的节点则被认为是没有被引用
到的节点，即无用的节点。

java中可作为GC Root的对象有（关于引用见问题4）
- 1.虚拟机栈中引用的对象（本地变量表）
- 2.方法区中静态属性引用的对象
- 3.方法区中常量引用的对象
- 4.本地方法栈中引用的对象（Native对象）

#### 3.3 常用垃圾收集器

- 1) 标记-清除收集器 Mark-Sweep
标记-清除算法分为两个阶段：标记阶段和清除阶段。标记阶段的任务是标记出所有需要被回收的对象，清除阶段就是回收被标
记的对象所占用的空间。

- 2) 复制收集器        Copying　　
它将可用内存按容量划分为大小相等的两块，每次只使用其中的一块。当这一块的内存用完了，就将还存活着的对象复制到另
外一块上面，然后再把已使用的内存空间一次清理掉，这样一来就不容易出现内存碎片的问题。

- 3) 标记-压缩收集器 Mark-Compact
该算法标记阶段和Mark-Sweep一样，但是在完成标记之后，它不是直接清理可回收对象，而是将存活对象都向一端移动，然后
清理掉端边界以外的内存。

- 4) 分代收集器　　　Generational
分代收集算法是目前大部分JVM的垃圾收集器采用的算法。它的核心思想是根据对象存活的生命周期将内存划分为若干个不同的
区域。一般情况下将堆区划分为老年代（Tenured Generation）和新生代（Young Generation），老年代的特点是每次垃圾
收集时只有少量对象需要被回收，而新生代的特点是每次垃圾回收时都有大量的对象需要被回收，那么就可以根据不同代的特点
采取最适合的收集算法。
目前大部分垃圾收集器对于新生代都采取Copying算法，因为新生代中每次垃圾回收都要回收大部分对象，也就是说需要复制的
操作次数较少，但是实际中并不是按照1：1的比例来划分新生代的空间的，一般来说是将新生代划分为一块较大的Eden空间和
两块较小的Survivor空间，每次使用Eden空间和其中的一块Survivor空间，当进行回收时，将Eden和Survivor中还存活的对
象复制到另一块Survivor空间中，然后清理掉Eden和刚才使用过的Survivor空间。
而由于老年代的特点是每次回收都只回收少量对象，一般使用的是Mark-Compact算法。

#### 3.4 java虚拟机和Dalvik虚拟机的区别
Java虚拟机：
1、java虚拟机基于栈。 基于栈的机器必须使用指令来载入和操作栈上数据，所需指令更多更多。
2、java虚拟机运行的是java字节码。（java类会被编译成一个或多个字节码.class文件）
Dalvik虚拟机：
1、dalvik虚拟机是基于寄存器的
2、Dalvik运行的是自定义的.dex字节码格式。（java类被编译成.class文件后，会通过一个dx工具将所有的.class文件转换成一个.dex文件，然后dalvik虚拟机会从其中读取指令和数据
3、常量池已被修改为只使用32位的索引，以 简化解释器。
4、一个应用，一个虚拟机实例，一个进程（所有android应用的线程都是对应一个linux线程，都运行在自己的沙盒中，不同的应用在不同的进程中运行。每个android dalvik应用程序都被赋予了一个独立的linux PID(app_*)）

### 4. java的四个引用方式与区别；

- 强引用：只要存在，垃圾收集器就不会回收对象。
```
Object obj = new Object();之类
```
- 软引用：用来描述一些还有用但是不必须的对象，系统将要发生内存溢出异常之前，将会把这些对象列入回收范围之中进行
第二次回收，如果还是不够那就只能抛出内存溢出的异常了。
```
SoftReference<String>s = new SoftReference<>(“我还有用但不是必须的!”);
```
- 弱引用：用来描述非必须对象，但是强度比弱引用更弱，被弱引用关联的对象只能生存到下一次垃圾收集发生之前，垃圾收集
工作的时候，无论是否必要都会回收掉只被弱引用关联的对象。
```
WeakReference<String>s = new WeakReference<String>(“我只能活到下一次垃圾收集之前”);
```
- 虚引用（幽灵引用或幻影引用）：一个对象是否有虚引用，与其生命周期毫无关系，也无法通过虚引用取得一个对象实例，只
被虚引用的对象，随时都会被回收掉
```
PhantomReference<String>ref = new PhantomReference<String>(“我只能接受死亡通知”) , targetReferenceQueue<String>);
```

### 5. java代理，静态代理和动态代理；
   我们大家都知道微商代理，简单地说就是代替厂家卖商品，厂家“委托”代理为其销售商品。关于微商代理，首先我们从他们那里
买东西时通常不知道背后的厂家究竟是谁，也就是说，“委托者”对我们来说是不可见的;其次，微商代理主要以朋友圈的人为目
标客户，这就相当于为厂家做了一次对客户群体的“过滤”。
   我们把微商代理和厂家进一步抽象，前者可抽象为代理类，后者可抽象为委托类(被代理类)。通过使用代理，通常有两个优点，
并且能够分别与我们提到的微商代理的两个特点对应起来：
优点一：可以隐藏委托类的实现;
优点二：可以实现客户与委托类间的解耦，在不修改委托类代码的情况下能够做一些额外的处理。


#### 5.1静态代理
若代理类在程序运行前就已经存在，那么这种代理方式被成为 静态代理 ，这种情况下的代理类通常都是我们在Java代码中定义的。
通常情况下， 静态代理中的代理类和委托类会实现同一接口或是派生自相同的父类。 下面我们用Vendor类代表生产厂家，
BusinessAgent类代表微商代理，来介绍下静态代理的简单实现，委托类和代理类都实现了Sell接口，
```
/**
 * 委托类和代理类都实现了Sell接口
 */
public interface Sell {
    void sell();
    void ad();
}
/**
 * 生产厂家
 */
public class Vendor implements Sell {
    public void sell() {
        System.out.println("In sell method");
    }

    public void ad() {
        System,out.println("ad method");
    }
}

/**
 * 代理类
 */
public class BusinessAgent implements Sell {
    private Sell vendor;

    public BusinessAgent(Sell vendor){
        this.vendor = vendor;
    }

    public void sell() {
        vendor.sell();
    }

    public void ad() {
        vendor.ad();
    }
}

```
静态代理可以通过聚合来实现，让代理类持有一个委托类的引用即可。

#### 5.2 动态代理

   代理类在程序运行时创建的代理方式被成为 动态代理。 也就是说，这种情况下，代理类并不是在Java代码中定义的，
而是在运行时根据我们在Java代码中的“指示”动态生成的。
  相比于静态代理，动态代理的优势在于可以很方便的对代理类的函数进行统一的处理，而不用修改每个代理类的函数。这么说比较抽象，
下面我们结合一个实例来介绍一下动态代理的这个优势是怎么体现的。现在，假设我们要实现这样一个需求：
在执行委托类中的方法之前输出“before”，在执行完毕后输出“after”。我们还是以上面例子中的Vendor类作为委托类，BusinessAgent类作为代理类来进行介绍。首先我们来使用静态代理来实现这一需求，相关代码如下：
```
public class BusinessAgent implements Sell {
    private Vendor mVendor;

    public BusinessAgent(Vendor vendor) {
        this.mVendor = vendor;
    }

    public void sell() {
        System.out.println("before");
        mVendor.sell();
        System.out.println("after");
    }

    public void ad() {
        System.out.println("before");
        mVendor.ad();
        System.out.println("after");
    }
}

```
 从以上代码中我们可以了解到，通过静态代理实现我们的需求需要我们在每个方法中都添加相应的逻辑，这里只存在两个方法所以工作量还不算大，
假如Sell接口中包含上百个方法呢?这时候使用静态代理就会编写许多冗余代码。通过使用动态代理，我们可以做一个“统一指示”，
从而对所有代理类的方法进行统一处理，而不用逐一修改每个方法。

 在java的动态代理机制中，有两个重要的类或接口，一个是 InvocationHandler(Interface)、另一个则是 Proxy(Class)，
这一个类和接口是实现我们动态代理所必须用到的。
 每一个动态代理类都必须要实现InvocationHandler这个接口，并且每个代理类的实例都关联到了一个handler，当我们通过
代理对象调用一个方法的时候，这个方法的调用就会被转发为由InvocationHandler这个接口的 invoke 方法来进行调用。
    我们来看看InvocationHandler这个接口的唯一一个方法 invoke 方法：
```
public interface InvocationHandler {
    Object invoke(Object proxy, Method method, Object[] args);
}
proxy:　　指代我们所代理的那个真实对象
method:　　指代的是我们所要调用真实对象的某个方法的Method对象
args:　　指代的是调用真实对象某个方法时接受的参数
```

Proxy.newProxyInstance :
```
public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) throws IllegalArgumentException

loader:　　一个ClassLoader对象，定义了由哪个ClassLoader对象来对生成的代理对象进行加载

interfaces:　　一个Interface对象的数组，表示的是我将要给我需要代理的对象提供一组什么接口，如果我提供了一组接
口给它，那么这个代理对象就宣称实现了该接口(多态)，这样我就能调用这组接口中的方法了

h:　　一个InvocationHandler对象，表示的是当我这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上
```

```
/**
 * Sell接口
 */
public interface Sell {
    void sell();
    void ad();
}

/**
 * 生产厂家
 */
public class Vendor implements Sell {
    public void sell() {
        System.out.println("In sell method");
    }

    public void ad() {
        System,out.println("ad method");
    }
}

//中介类必须实现InvocationHandler接口
public class DynamicProxy implements InvocationHandler {
    //obj为委托类对象;
    private Object obj;

    public DynamicProxy(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before");
        Object result = method.invoke(obj, args);
        System.out.println("after");
        return result;
    }
}


//动态生成代理类的相关代码如下：
public class Main {
    public static void main(String[] args) {
        //创建中介类实例
        DynamicProxy inter = new DynamicProxy(new Vendor());
        //加上这句将会产生一个$Proxy0.class文件，这个文件即为动态生成的代理类文件
        System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles","true");

        //获取代理类实例sell(代理类是在此处动态生成的)
        Sell sell = (Sell)(Proxy.newProxyInstance(Sell.class.getClassLoader(), new Class[] {Sell.class}, inter));

        //通过代理类对象调用代理类方法，实际上会转到invoke方法调用
        sell.sell();
        sell.ad();
    }
}

```
中介类与委托类构成了静态代理关系，在这个关系中，中介类是代理类，委托类就是委托类;
代理类与中介类也构成一个静态代理关系，在这个关系中，中介类是委托类，代理类是代理类。
动态代理关系由两组静态代理关系组成，这就是动态代理的原理。

上面我们已经简单提到过动态代理的原理，这里再简单的总结下：
首先通过newProxyInstance方法获取代理类实例，而后我们便可以通过这个代理类实例调用代理类的方法，对代理类的方法的调用
实际上都会调用中介类(调用处理器)的invoke方法，在invoke方法中我们调用委托类的相应方法，并且可以添加自己的处理逻辑。


### 6. AIDL内部有哪些方法；

### 7. ArrayList 和 LinkedList 区别
1.ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。
2.对于随机访问get和set，ArrayList觉得优于LinkedList，因为LinkedList要移动指针。
3.对于新增和删除操作add和remove，LinedList比较占优势，因为ArrayList要移动数据。
```
ArrayList的同步：
同步方法：List list = Collections.synchronizedList(new ArrayList(...));或者使用Vector或CopyOnWriteArrayList
ArrayList的扩容：
int newCapacity = oldCapacity + (oldCapacity >> 1);
elementData = Arrays.copyOf(elementData, newCapacity);
```

### 8. LRUCache 是如何实现的（源码角度）？为什么要用 LinkedHashmap？
   LRUCache内部采用LinkedHashmap结构，当get的时候，LinkedHashmap会把get的元素放在队尾；
   LRUCache达到最大缓存数量时，会删除头结点，即最少使用的元素；


