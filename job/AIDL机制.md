
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

Serializable和Parcelable
Parcelable 和 Serializable 都可以实现序列化并且都可用于 Intent 间的数据传递，那我们还是得总结下它们的使用场景：

Serializable 是 Java 的序列化接口，使用简单但开销很大，序列化和反序列化都需要大量的 I/O 操作；而 Parcelable 是 Android 中的序列化方式，因此更适合于 Android 平台上，它的缺点是使用起来稍微麻烦点，但它的效率很高，这是 Android 推荐的序列化方式，因此我们要首选 Parcelable。但 Serializable 也不是在 Android 上无用武之地，下面两种情况就发日常适合 Serializable：
1. 需要将对象序列化到设备；
2. 对象序列化后需要网络传输。

Serializable 使用 I/O 读写存储在硬盘上，而 Parcelable 是直接 在内存中读写;
Serializable 会使用反射，序列化和反序列化过程需要大量 I/O 操作; Parcelable 自已实现封送和解封（marshalled &unmarshalled）操作不需要用反射，
数据也存放在 Native 内存中，效率要快很多。

Intent 中的 Bundle 是使用 Binder 机制进行数据传送的。能使用的 Binder 的缓冲区是有大小限制的（有些手机是 2 M），而一个进程默认有 16 个 Binder 线程，
所以一个线程能占用的缓冲区就更小了（ 有人以前做过测试，大约一个线程可以占用 128 KB）

作者：nanchen2251
链接：https://www.jianshu.com/p/1b362e374354
来源：简书
著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
public class TestSerializable implements Serializable {
String msg;

    List<ItemBean> datas;
    
    public static class ItemBean implements Serializable{
        String name;
    }
}

public class TestParcelable implements Parcelable {
String msg;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
    }

    TestParcelable(String msg) {
        this.msg = msg;
    }

    private TestParcelable(Parcel in) {
        this.msg = in.readString();
    }

    public static final Creator<TestParcelable> CREATOR = new Creator<TestParcelable>() {
        @Override
        public TestParcelable createFromParcel(Parcel source) {
            return new TestParcelable(source);
        }

        @Override
        public TestParcelable[] newArray(int size) {
            return new TestParcelable[size];
        }
    };
}


