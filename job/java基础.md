
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

重载和重写

抽象类(abstract)和接口(interface)的异同点


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

# 重载和重写
重载是指不同的函数使用相同的函数名，但是函数的参数个数或类型不同。调用的时候根据函数的参数来区别不同的函数。
覆盖（也叫重写）是指在派生类中重新对基类中的虚函数（注意是虚函数）重新实现。即函数名和参数都一样，只是函数的实现体不一样。

overload（重载）
　　1、参数类型、个数、顺序至少有一个不相同。
　　2、不能重载只有返回值不同的方法名。
　　3、存在于父类和子类、同类中。

override（重写）
　　 1、方法名、参数、返回值相同。
　　 2、子类方法不能缩小父类方法的访问权限。
　　 3、子类方法不能抛出比父类方法更多的异常(但子类方法可以不抛出异常)。
　　 4、存在于父类和子类之间。
　　 5、方法被定义为final不能被重写。

重写(Overriding)是父类与子类之间多态性的一种表现，而重载(Overloading)是一个类中多态性的一种表现。


# 抽象类(abstract)和接口(interface)的异同点
抽象类是 "is a", 接口是"like a"
接口是抽象类的延伸，是抽象类的特殊化
相同点：
　　都可以写抽象方法，规定了子类必须要重写的方法（所以不能有抽象构造方法和抽象静态方法）；
　　　　　　为什么不能有抽象构造方法：构造方法是类实例化时的构造过程，而抽象类不能被实例化，两者矛盾，所以不存在抽象构造方法。
　　　　　　为什么不能有抽象静态方法：抽象方法是专用于继承来实现的，而静态方法可以被类及其对象调用，不能被继承，两者矛盾，所以不存在抽象静态方法。
　　都不能被实例化，所以不能创建实例对象（由于没有对应的具体概念）；【可以用new 接口(){}的方法来当做匿名类，把方法作为参数来进行传递，注：这不是实例化】

不同点（语法）：
　　抽象类是对类抽象，而接口是对行为抽象；
　　抽象类只能继承一个，而接口可以实现多个；
　　抽象类有构造方法（为子类准备），而接口没有构造方法；
　　抽象类中可以有非抽象方法，而接口中只能有抽象方法，并且只能是public类型的，默认为 public abstract 类型（JDK1.8开始，接口中可以通过default关键字来定义非抽象方法，解决扩展问题）；
　　抽象类中可以有成员变量和属性，而接口中只能有由static final修饰的常量；
　　抽象类和接口中都可以包含静态成员变量，抽象类中的静态成员变量的访问类型可以是任意类型，但接口中定义的变量只能是 public static final 类型，并且默认为 public static final 类型。

不同点（应用）：

　　抽象类在代码实现方面发挥作用，可以实现代码的重用；而接口更多的是在系统架构方面发挥作用，主要用于定义模块之间的通信契约。

这道题的思路是先从整体解释抽象类与接口的概念，然后答比较两者的相同点，接下来答语法方面的区别，最后答应用方面的区别。
比较两者语法区别的条理是：先从本质区别开始，然后是继承性，构造方法，抽象方法，成员变量和属性以及常量，最后是静态成员变量。


##final、finally、finalize的区别
三者的区别
1.性质不同

（1）final为关键字;

（2）finalize()为方法;

（3）finally为为区块标志,用于try语句中;

2. 作用

（1）final为用于标识常量的关键字,final标识的关键字存储在常量池中(在这里final常量的具体用法将在下面进行介绍);

（2）finalize()方法在Object中进行了定义,用于在对象“消失”时,由JVM进行调用用于对对象 进行垃圾回收，类似于C++中的析构函数;用户自定义时,用于释放对象占用的资源(比如进行 I/0操作);

（3）finally{}用于标识代码块,与try{ }进行配合,不论try中的代码执行完或没有执行完(这里指有异常),该代码块之中的程序必定会进行 .

一.final的用法
被final修饰的类不可以被继承
被final修饰的方法不可以被重写
被final修饰的变量不可以被改变,如果修饰引用,那么表示引用不可变,引用指向的内容可变.
被final修饰的方法,JVM会尝试将其内联,以提高运行效率
被final修饰的常量,在编译阶段会存入常量池中
除此之外,编译器对final域要遵守的两个重排序规则更好:

        在构造函数内对一个final域的写入,与随后把这个被构造对象的引用赋值给一个引用变量,这两个操作之间不能重排序,初次读一个包含final域的对象的引用,与随后初次读这个final域,这两个操作之间不能重排序

二.finally的用法
finally是在异常处理中的使用的

不管 try 语句块正常结束还是异常结束,finally 语句块是保证要执行的.

如果 try 语句块正常结束,那么在 try 语句块中的语句都执行完之后,再执行 finally 语句块.

不管有没有出现异常,finally块中的代码都会执行;
当try和catch中有return时,finally仍然会执行;
finally是在return后面的表达式运算后执行的(此时并没有返回运算后的值,而是先把要返回的值保存起来,无论finally中的代码怎么样,返回的值都不会改变,仍然是之前保存的值),所以函数返回值是在finally执行前确定好的;
finally中最好不要包含return,否则程序会提前退出,返回值不是try或catch中保存的返回值.
三.finalize的用法
finalize() 是Java中Object的一个protected方法.返回值为空,当该对象被垃圾回收器回收时,会调用该方法.

关于finalize()函数

finalize不等价于c++中的析构函数;
对象可能不被垃圾机回收器回收;
垃圾回收不等于析构;
垃圾回收只与内存有关;
垃圾回收和finalize()都是靠不住的,只要JVM还没有快到耗尽内存的地步,它是不会浪费时间进行垃圾回收的;
程序强制终结后,那些失去引用的对象将会被垃圾回收.(System.gc())
finalize()的用途:比如当一个对象代表了打开了一个文件,在对象被回收前,程序应该要关闭该文件,可以通过finalize函数来发现未关闭文件的对象,并对其进行处理.

public class FileOperator {

    private boolean closed = false;
 
    void close(){
        this.closed = true;
    }
 
    @Override
    protected void finalize(){//当垃圾回收器企图回收本对象时，会调用该方法,该方法是重写父类的方法的
        if(!closed){//如果该书没有被签入，
            System.out.println("Error: A File was not closed . Name:" + this);
            this.closed = true;
        }
    }
 
    public static void main(String[] args) {
        FileOperator fileOperator = new FileOperator();//有引用的对象，不会被虚拟机回收
        new FileOperator();//匿名对象，会被虚拟机回收
        System.gc();//强制进行终结动作
    }


