


> client通过获得一个server的代理接口，对server进行调用。
代理接口中定义的方法与server中定义的方法时一一对应的。
client调用某个代理接口中的方法时，代理接口的方法会将client传递的参数打包成Parcel对象。
代理接口将Parcel发送给内核中的binder driver。
server会读取binder driver中的请求数据，如果是发送给自己的，解包Parcel对象，处理并将结果返回。
整个的调用过程是一个同步过程，在server处理的时候，client会block住。因此client调用过程不应在主线程。


Binder通信的四个角色
Client进程：使用服务的进程。
Server进程：提供服务的进程。
ServiceManager进程：ServiceManager的作用是将字符形式的Binder名字转化成Client中对该Binder的引用，使得Client能够通过Binder名字获得对Server中Binder实体的引用。
Binder驱动：驱动负责进程之间Binder通信的建立，Binder在进程之间的传递，Binder引用计数管理，数据包在进程之间的传递和交互等一系列底层支持。

Binder运行的实例解释
首先我们看看我们的程序跨进程调用系统服务的简单示例，实现浮动窗口部分代码：
```
//获取WindowManager服务引用
WindowManager wm = (WindowManager)getSystemService(getApplication().WINDOW_SERVICE);
//布局参数layoutParams相关设置略...
View view=LayoutInflater.from(getApplication()).inflate(R.layout.float_layout, null);
//添加view
wm.addView(view, layoutParams);
```
注册服务(addService)：在Android开机启动过程中，Android会初始化系统的各种Service，并将这些Service向ServiceManager注册（即让ServiceManager管理）。这一步是系统自动完成的。
获取服务(getService)：客户端想要得到具体的Service直接向ServiceManager要即可。客户端首先向ServiceManager查询得到具体的Service引用，通常是Service引用的代理对象，对数据进行一些处理操作。即第2行代码中，得到的wm是WindowManager对象的引用。
使用服务：通过这个引用向具体的服务端发送请求，服务端执行完成后就返回。即第6行调用WindowManager的addView函数，将触发远程调用，调用的是运行在systemServer进程中的WindowManager的addView函数。

使用服务的具体执行过程
client通过获得一个server的代理接口，对server进行调用。
代理接口中定义的方法与server中定义的方法时一一对应的。
client调用某个代理接口中的方法时，代理接口的方法会将client传递的参数打包成Parcel对象。
代理接口将Parcel发送给内核中的binder driver。
server会读取binder driver中的请求数据，如果是发送给自己的，解包Parcel对象，处理并将结果返回。
整个的调用过程是一个同步过程，在server处理的时候，client会block住。因此client调用过程不应在主线程。


链接：https://www.jianshu.com/p/4920c7781afe

