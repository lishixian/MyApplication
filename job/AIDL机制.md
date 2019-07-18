
Binder的工作机制总结

客户端：
    绑定服务，并获得Binder对象
    通过asInterface方法以（1）中的Binder对象为参数，获得Stub.Proxy代理类实例；
    在子线程中通过Stub.Proxy实例调用其实现的接口方法。_data中写入参数，并通过Binder调用transact方法发起RPC，线程挂起，等待RPC返回结果

服务端：
    通过底层封装后回调onTransact方法。在此方法中从data取出参数，并执行目标，执行完毕在reply中写入目标方法的返回值。最后返回onTransact方法的执行结果，true则代表客户端请求成功，RPC将会返回结果，执行过程也将回到客户端。

客户端：
    RPC过程返回后，原本挂起的客户端线程继续执行，从_reply中取出服务端方法执行的结果并返回。





## asInterface方法： 将服务端的Binder对象转换成客户端需要的AIDL接口类型对象。
如果服务端和客户端同进程，返回IBookManager接口对象本身，否则返回系统封装后的Stub.Proxy对象
之前的项目案例中，我们在客户端就是通过IBookManager.Stub.asInterface(service)来获取接口对象的


## onTransact方法：这个方法运行在服务端中的Binder线程池，当客服端发起跨进程请求时，远程请求通过系统底层封装后交由此方法处理。工作过程如下：
onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)

1. 服务端通过code确定请求的目标方法
2. 从data中取出目标方法需要的参数（如果目标方法有参数），然后执行目标方法
3. 执行完毕后，就向reply中写入返回值（如果目标方法有返回值）
4. 此方法返回false时，客户端请求失败（会抛出异常）


##  声明内部类Proxy实现IBookManager接口，为Stub的代理类（由4可以知）。主要工作重写getBookList和addBook方法。
分析Proxy#getBookList，这个方法运行在客户端

1. 创建所需要的Parcel对象：输入型_data、输出型_reply，以及返回值对象List
2. 然后将方法的参数信息写入_data中（如果有参数的话）
3. 接着通过IBinder类型的成员变量mRemote（实际就是通过绑定服务成功获得的IBinder对象）调用transact方法来发起RPC（远程过程调用）请求，同时线程挂起；
4. 然后服务端onTransact方法被调用，直到RPC过程返回，当前线程继续执行，并从_reply取出返回结果（即6中的reply）
5. 最后返回_reply结果

注意：
- 客户端的发出的同步请求，所以不能在UI线程发起远程请求
- 服务端Binder运行在Binder线程池，即onTransact方法执行在Binder方法池中，在此方法中最终执行了我们接口中定义方法的具体实现。所以在AIDLService中的IBookManager.Stub对象实现接口方法时，不管操作是否耗时都要采用同步的方式。
