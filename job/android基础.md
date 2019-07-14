android基础问题有如下内容：
1. handle机制；handler如何找到looper；延迟消息怎么处理；messagequeue的数据结构是什么；
2. context和Acticity的关系；
3. Service 的使用（start和bind），IntentService的使用，源码解析；
4. Activity的启动流程；
5. 匿名内部类有构造方法吗；
6. targetSDK、compileSDK、minSDK的区别；
7. singletop和singleTask的应用场景；
8. process带冒号和不带冒号的区别；
9. 前台进程、可见进程、服务进程、后台进程、空进程；
10. 谈谈你对 JNI 和 NDK 的理解；
11.自定义view
11.1. view事件分发机制；上下滑动冲突问题如何处理；
11.2. mvp和mvvm的区别；

 android有序广播和无序广播的区别;

 Service的bindService和startService混合使用及其关闭分析;

 sharepreference  contentProvider是线程安全的吗,是进程安全你的吗?

 activity生命周期 A启动B，B返回A, A,B的全生命周期

# 1. handler机制；handler如何找到looper；延迟消息怎么处理；messagequeue的数据结构是什么；

## 1.1 先说一下handler的使用
```
/**
  * 此处以 匿名内部类 的使用方式为例
  */
  // 步骤1：在主线程中 通过匿名内部类 创建Handler类对象
            private Handler mhandler = new  Handler(){
                // 通过复写handlerMessage()从而确定更新UI的操作
                @Override
                public void handleMessage(Message msg) {
                        ...// 需执行的UI操作
                    }
            };

  // 步骤2：创建消息对象
    Message msg = Message.obtain(); // 实例化消息对象
    msg.what = 1; // 消息标识
    msg.obj = "AA"; // 消息内容存放

  // 步骤3：在工作线程中 通过Handler发送消息到消息队列中
   mHandler.sendMessage(msg);

```

## 1.2 handler的创建与发消息
Handler的主要作用是将一个任务切换到某个指定的线程中去执行。因为Android规定访问UI 的只能是主线程，那么耗时操作需要切换到子线程去执行。
handler在创建的时候使用了所在线程的looper和looper里的queue
```
public Handler(Callback callback, boolean async) {
        mLooper = Looper.myLooper();
        if (mLooper == null) {
            throw new RuntimeException(
                "Can't create handler inside thread " + Thread.currentThread()
                        + " that has not called Looper.prepare()");
        }
        mQueue = mLooper.mQueue;
        mCallback = callback;
        mAsynchronous = async;
    }
```
然后发送消息，将消息入队（这里我们顺带分析延时发送消息的流程）
```
    public final boolean sendMessageDelayed(Message msg, long delayMillis)
    {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
    }
    public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
        MessageQueue queue = mQueue;
        if (queue == null) {
            RuntimeException e = new RuntimeException(
                    this + " sendMessageAtTime() called with no mQueue");
            Log.w("Looper", e.getMessage(), e);
            return false;
        }
        // 这里看出延时消息就是发普通消息的基础上加上延时的时间。
        return enqueueMessage(queue, msg, uptimeMillis);
    }
    private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
        // 这里msg携带了handler实例,会在从queue取出msg时用到,将返回到主线程操作
        msg.target = this;
        if (mAsynchronous) {
            msg.setAsynchronous(true);
        }
        return queue.enqueueMessage(msg, uptimeMillis);
    }
```
消息入队之后，我们需要去分析queue的运作情况

## 1.3 MessageQueue的入队和出队
MessageQueue是一个单链表的数据结构，主要负责存储消息。
```
boolean enqueueMessage(Message msg, long when) {
        synchronized (this) {
            msg.markInUse();
            msg.when = when;
            Message p = mMessages;
            boolean needWake;
             // 当消息队列中没有消息，或者按时间来说是第一个消息
             // 从这里看出是,把消息按时间顺序排序
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
                msg.next = p;
                mMessages = msg;
                needWake = mBlocked;
            } else {
                // Inserted within the middle of the queue.  Usually we don't have to wake
                // up the event queue unless there is a barrier at the head of the queue
                // and the message is the earliest asynchronous message in the queue.
                needWake = mBlocked && p.target == null && msg.isAsynchronous();
                Message prev;
                for (;;) {
                    prev = p;
                    p = p.next;
                    // 循环遍历,找到比when大的消息,插入到它的前面
                    // 从这里看出是把消息按时间顺序排序
                    if (p == null || when < p.when) {
                        break;
                    }
                    if (needWake && p.isAsynchronous()) {
                        needWake = false;
                    }
                }
                msg.next = p; // invariant: p == prev.next
                prev.next = msg;
            }

        }
        return true;
    }
```
enqueueMessage就是消息的入队操作，队列按消息的时间顺序排序.
有入队就有出队，MessageQueue的出队就是next方法
```
Message next() {
        int pendingIdleHandlerCount = -1; // -1 only during first iteration
        int nextPollTimeoutMillis = 0;
        for (;;) {
            if (nextPollTimeoutMillis != 0) {
                Binder.flushPendingCommands();
            }

            nativePollOnce(ptr, nextPollTimeoutMillis);

            synchronized (this) {
                // Try to retrieve the next message.  Return if found.
                final long now = SystemClock.uptimeMillis();
                Message prevMsg = null;
                Message msg = mMessages;
                if (msg != null && msg.target == null) {
                    // Stalled by a barrier.  Find the next asynchronous message in the queue.
                    do {
                        prevMsg = msg;
                        msg = msg.next;
                    } while (msg != null && !msg.isAsynchronous());
                }
                if (msg != null) {
                    if (now < msg.when) {
                        // Next message is not ready.  Set a timeout to wake up when it is ready.
                        nextPollTimeoutMillis = (int) Math.min(msg.when - now, Integer.MAX_VALUE);
                    } else {
                        // Got a message.
                        mBlocked = false;
                        if (prevMsg != null) {
                            prevMsg.next = msg.next;
                        } else {
                            mMessages = msg.next;
                        }
                        msg.next = null;
                        if (DEBUG) Log.v(TAG, "Returning message: " + msg);
                        msg.markInUse();
                        return msg;
                    }
                } else {
                    // No more messages.
                    nextPollTimeoutMillis = -1;
                }

            }

        }
    }
```
next是一个无线循环方法,如果队列没有消息就阻塞,如果有就返回这条消息,并从单链表中移除.
调用queue.next方法的是looper,下面看看looper的创建和调用:

## 1.4looper
```
//looper的创建
    private static void prepare(boolean quitAllowed) {
        if (sThreadLocal.get() != null) {
            throw new RuntimeException("Only one Looper may be created per thread");
        }
        // 通过ThreadLocal保存,保证各个线程间不冲突.
        sThreadLocal.set(new Looper(quitAllowed));
    }
    private Looper(boolean quitAllowed) {
        mQueue = new MessageQueue(quitAllowed);
        mThread = Thread.currentThread();
    }

    //这里说说sThreadLocal.set(new Looper(quitAllowed))方法
    // 这个方法里是找到当前的线程,并从当前线程里取出ThreadLocalMap,
    //把当前的ThreadLocal和要存的value值放入当前线程的map里.保证了多个线程各取各的值
        public void set(T value) {
            Thread t = Thread.currentThread();
            ThreadLocalMap map = getMap(t);
            if (map != null)
                map.set(this, value);
            else
                createMap(t, value);
        }

//looper.loop()调用queue.next()
public static void loop() {
        final Looper me = myLooper();
        for (;;) {
            Message msg = queue.next(); // might block
            if (msg == null) {
                // No message indicates that the message queue is quitting.
                return;
            }

            // 这里msg.target就是handler,这里又会回handler里去处理消息,
            // 而handler在主线程创建的,所以又回到了主线程
             msg.target.dispatchMessage(msg);
            msg.recycleUnchecked();
        }
    }
```
looper.loop()开启无线循环,不断的从消息队里取出消息,并做处理.通过msg.target.dispatchMessage(msg);将操作返回到主线程.
最后看下handler的dispatchMessage方法:
## 1.5 handler.dispatchMessage
```
    public void dispatchMessage(Message msg) {
        if (msg.callback != null) {
            handleCallback(msg);//这里的callback是handler.post(Runnable r)里的r
        } else {
            if (mCallback != null) {// 这里的mCallback是通过创建new Handler(new callback());
                if (mCallback.handleMessage(msg)) {
                    return;
                }
            }
            handleMessage(msg);
        }
    }

   //msg.callback是继承Runnable的一个类
   public interface Runnable {
       public abstract void run();
   }

    //mCallback是继承Callback的一个类
    public interface Callback {
            public boolean handleMessage(Message msg);
    }
```

# 2. context和Acticity的关系；
Context是一个抽象基类。在翻译为上下文，也可以理解为环境，是提供一些程序的运行环境基础信息。
Context下有两个子类，ContextWrapper是上下文功能的封装类，而ContextImpl则是上下文功能的实现类。
而ContextWrapper又有三个直接的子类， ContextThemeWrapper、Service和Application。
其中，ContextThemeWrapper是一个带主题的封装类，而它有一个直接子类就是Activity
关系如下:
```

											Context
											____|____
											|        |
									ContextWrapper   ContextImpl
										|               |
ContextThemeWrapper  Service   Application
        |
    Activity

Context数量 = Activity数量 + Service数量 + 1 （1为Application）
```

# 3. Service 的使用（start和bind），IntentService的使用，源码解析；
Service是一个在后台执行长时间运行操作而不用提供用户界面的应用组件，可由其他组件启动，即使用户切换到其他应用程序，Service 仍然在后台继续运行。

## 3.1 startService方式启动Service
当应用组件通过startService方法来启动Service 时，Service 则会处于启动状态，一旦服务启动，它就会在后台无限期的运行，生命周期独立于启动它的组件，即使启动它的组件已经销毁了也不受任何影响，由于启动的服务长期运行在后台，这会大量消耗手机的电量，因此，我们应该在任务执行完成之后调用stopSelf()来停止服务，或者通过其他应用组件调用stopService 来停止服务。
startService 启动服务后，会执行如下生命周期：onCreate() －>onStartCommand() －>  onStart()(现在已经废弃) －> onDestroy() 。具体看一下它的几个生命周期方法：

- onCreate() :首次启动服务的时候，系统会调用这个方法，在onStartCommand 和 onBind 方法之前，如果服务已经启动起来了，再次启动时，则不会调用此方法，因此可以在onCreate 方法中做一些初始化的操作，比如要执行耗时的操作，可以在这里创建线程，要播放音乐，可以在这里初始化音乐播放器。
- onStartCommand(): 当通过startService 方法来启动服务的时候，在onCreate 方法之后就会回调这个方法，此方法调用后，服务就启动起来了，将会在后台无限期的运行，直到通过stopService 或者 stopSelf 方法来停止服务。
- onDestroy():当服务不再使用且将被销毁时，系统将调用此方法。服务应该实现此方法来清理所有资源，如线程、注册的侦听器、接收器等。 这是服务接收的最后一个调用。

## 3.2 bindService 方式启动服务
除了startService 来启动服务之外，另外一种启动服务的方式就是通过bindService 方法了，也就是绑定服务，其实通过它的名字就容易理解，绑定即将启动组件和服务绑定在一起。前面讲的通过startService 方式启动的服务是与组件相独立的，即使启动服务的组件被销毁了，服务仍然在后台运行不受干扰。但是通过bindSerivce 方式绑定的服务就不一样了，它与绑定组件的生命周期是有关的。如下：
多个组件可以绑定到同一个服务上，如果只有一个组件绑定服务，当绑定的组件被销毁时，服务也就会停止了。如果是多个组件绑定到一个服务上，当绑定到该服务的所有组件都被销毁时，服务才会停止。
bindService 绑定服务 和startService 的生命周期是不一样，bindServie 的生命周期如下：onCreate －> onBind －> onUnbind  －>onDestroy。其中重要的就是onBind 和onUnbind 方法。

- onBind(): 当其他组件想通过bindService 与服务绑定时，系统将会回调这个方法，在实现中，你必须返回一个IBinder接口，供客户端与服务进行通信，必须实现此方法，这个方法是Service 的一个抽象方法，但是如果你不允许绑定的话，返回null 就可以了。
- onUnbind(): 当所有与服务绑定的组件都解除绑定时，就会调用此方法。

## 3.3 IntentService的使用
IntentService 默认为我们开启了一个工作线程，在任务执行完毕后，自动停止服务，因此在我们大多数的工作中，使用IntentService 就够了
IntentService 自动为我们开启了一个线程来执行耗时操作，并且在任务完成后自动停止服务，那么它是怎么做的呢？我们看一下源码一探究竟。
```
    // 1,有一个Looper 变量和一个ServiceHandler 变量，ServiceHander 继承Handler 处理消息
        private volatile Looper mServiceLooper;
        private volatile ServiceHandler mServiceHandler;
        private String mName;
        private boolean mRedelivery;

        private final class ServiceHandler extends Handler {
            public ServiceHandler(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message msg) {
               // 在工作线程中调用onHandleIntent，子类根据Intent传递的数据执行具体的操作
                onHandleIntent((Intent)msg.obj);
              // 任务执行完毕后，自动停止Service
                stopSelf(msg.arg1);
            }
        }
    //2, 在OnCreate 方法中，创建了一个线程HandlerThread ，并启动线程
    // 然后获取工作线程的Looper ，并用Looper 初始化Handler(我们都知道Handler 的创建需要一依赖Looper)
     public void onCreate() {
            // TODO: It would be nice to have an option to hold a partial wakelock
            // during processing, and to have a static startService(Context, Intent)
            // method that would launch the service & hand off a wakelock.

            super.onCreate();
            HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
            thread.start();

            mServiceLooper = thread.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);
        }
    //3, 在onStart()方法中发送消息给Handler,并且把Intent 传给了Handler 处理
     @Override
        public void onStart(@Nullable Intent intent, int startId) {
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            msg.obj = intent;
            mServiceHandler.sendMessage(msg);
        }
    // 4，onStartCommand 直接调用的是onStart方法
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
            onStart(intent, startId);
            return mRedelivery ? START_REDELIVER_INTENT : START_NOT_STICKY;
        }
    // 5 最后就是一个子类需要实现的抽象方法，这个方法在handleMessage中调用，也就是在工作线程中执行。
     protected abstract void onHandleIntent(@Nullable Intent intent);

```
IntentService是Service 的子类，默认给我们开启了一个工作线程执行耗时任务，并且执行完任务后自 动停止服务。扩展IntentService比较简单，提供一个构造方法和实现onHandleIntent 方法就可了，不用重写父类的其他方法。但是如果要绑定服务的话，还是要重写onBind 返回一个IBinder 的。使用Service 可以同时执行多个请求，而使用IntentService 只能同时执行一个请求。

参考链接:https://www.jianshu.com/p/476d3ed50db1

# 4. Activity的启动流程；
```
Activity#startActivity()
Activity#startActivityForResult()
    Instrumentation.ActivityResult ar =
                mInstrumentation.execStartActivity(
                    this, mMainThread.getApplicationThread(), mToken, this,
                    intent, requestCode, options);
Instrumentation#execStartActivity
    int result = ActivityManager.getService()
                    .startActivity(whoThread, who.getBasePackageName(), intent,
                            intent.resolveTypeIfNeeded(who.getContentResolver()),
                            token, target != null ? target.mEmbeddedID : null,
                            requestCode, 0, null, options);

ActivityManagerService#startActivity
ActivityManagerService#startActivityAsUser
    mActivityStartController.obtainStarter(intent, "startActivityAsUser")
                .setCaller(caller)
                .setCallingPackage(callingPackage)
                .setResolvedType(resolvedType)
                .setResultTo(resultTo)
                .setResultWho(resultWho)
                .setRequestCode(requestCode)
                .setStartFlags(startFlags)
                .setProfilerInfo(profilerInfo)
                .setActivityOptions(bOptions)
                .setMayWait(userId)
                .execute();
ActivityStarter#execute
                if (mRequest.mayWait) {
                    return startActivityMayWait(mRequest.caller, mRequest.callingUid,
                            mRequest.callingPackage, mRequest.intent, mRequest.resolvedType,
                            mRequest.voiceSession, mRequest.voiceInteractor, mRequest.resultTo,
                            mRequest.resultWho, mRequest.requestCode, mRequest.startFlags,
                            mRequest.profilerInfo, mRequest.waitResult, mRequest.globalConfig,
                            mRequest.activityOptions, mRequest.ignoreTargetSecurity, mRequest.userId,
                            mRequest.inTask, mRequest.reason,
                            mRequest.allowPendingRemoteAnimationRegistryLookup);
                } else {
                    return startActivity(mRequest.caller, mRequest.intent, mRequest.ephemeralIntent,
                            mRequest.resolvedType, mRequest.activityInfo, mRequest.resolveInfo,
                            mRequest.voiceSession, mRequest.voiceInteractor, mRequest.resultTo,
                            mRequest.resultWho, mRequest.requestCode, mRequest.callingPid,
                            mRequest.callingUid, mRequest.callingPackage, mRequest.realCallingPid,
                            mRequest.realCallingUid, mRequest.startFlags, mRequest.activityOptions,
                            mRequest.ignoreTargetSecurity, mRequest.componentSpecified,
                            mRequest.outActivity, mRequest.inTask, mRequest.reason,
                            mRequest.allowPendingRemoteAnimationRegistryLookup);
                }
ActivityStarter#startActivityMayWait
                int res = startActivity(caller, intent, ephemeralIntent, resolvedType, aInfo, rInfo,
                        voiceSession, voiceInteractor, resultTo, resultWho, requestCode, callingPid,
                        callingUid, callingPackage, realCallingPid, realCallingUid, startFlags, options,
                        ignoreTargetSecurity, componentSpecified, outRecord, inTask, reason,
                        allowPendingRemoteAnimationRegistryLookup);
ActivityStarter#startActivity
    result = startActivityUnchecked(r, sourceRecord, voiceSession, voiceInteractor,
                        startFlags, doResume, options, inTask, outActivity);
ActivityStarter#startActivityUnchecked
    mSupervisor.resumeFocusedStackTopActivityLocked();
ActivityStackSupervisor#resumeFocusedStackTopActivityLocked
ActivityStack#resumeTopActivityUncheckedLocked
    result = resumeTopActivityInnerLocked(prev, options);
ActivityStack#resumeTopActivityInnerLocked
    pausing |= startPausingLocked(userLeaving, false, next, false);//这一步会将栈顶的Activity onPause
    mStackSupervisor.startSpecificActivityLocked(next, true, false);
ActivityStackSupervisor#startSpecificActivityLocked
    realStartActivityLocked(r, app, andResume, checkConfig);
ActivityStackSupervisor#realStartActivityLocked
    clientTransaction.addCallback(LaunchActivityItem.obtain(new Intent(r.intent),
                            System.identityHashCode(r), r.info,
                            // TODO: Have this take the merged configuration instead of separate global
                            // and override configs.
                            mergedConfiguration.getGlobalConfiguration(),
                            mergedConfiguration.getOverrideConfiguration(), r.compat,
                            r.launchedFromPackage, task.voiceInteractor, app.repProcState, r.icicle,
                            r.persistentState, results, newIntents, mService.isNextTransitionForward(),
                            profilerInfo));
LaunchActivityItem#execute
    client.handleLaunchActivity(r, pendingActions, null /* customIntent */);
ActivityThread#handleLaunchActivity
    final Activity a = performLaunchActivity();
ActivityThread#performLaunchActivity
    activity = mInstrumentation.newActivity();// 创建Activity
    mInstrumentation.callActivityOnCreate(activity, r.state);
Instrumentation.callActivityOnCreate()
    activity.performCreate(icicle);
Activity#performCreate()
    onCreate(icicle);//执行onCreate方法
```



# 5. 匿名内部类有构造方法吗；
匿名内部类中的构造代码块充当了构造器的作用.  构造器创建的时候,会把外部类的引用传进去,因而持有了外部类的引用.

# 6. targetSDK、compileSDK、minSDK的区别；
- compileSdkVersion: 定义程序编译的时候,选择哪个SDK.采用该api进行代码检察和警告;
- minSdkVersion: 编译时兼容的最低版本的api.小于该版本的手机装不了该apk
- targetSdkVersion: 目标软件开发版本.会将该版本的api编译进apk中


# 7. singletop和singleTask的应用场景；
Activity四种启动模式,
- Standard: 普通模式.一般情况常用
- SingleTop: 栈顶复用. 防止按两次登陆按钮;在通知栏点击启动新活动;页面启动service后home键,service又返回到该界面
- SingleTask: 栈内复用. 主页和登陆页面;
- SingleInstance: 单例模式. 电话界面


# 8. process带冒号和不带冒号的区别；
带冒号的是该进程的使用进程,当它被需要或者这个服务需要在新进程中运行的时候，这个新进程将会被创建
不带冒号的是全局进程,允许在不同应用中的各种组件可以共享一个进程，从而减少资源的占用
因为设置了 android:process 属性将组件运行到另一个进程，相当于另一个应用程序，所以在另一个线程中也将新建一个 Application 的实例。因此，每新建一个进程 Application 的 onCreate 都将被调用一次。 如果在 Application 的 onCreate 中有许多初始化工作并且需要根据进程来区分的，那就需要特别注意了。
解决方案:
```
public static String getProcessName(Context cxt, int pid) {
    ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
    List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
    if (runningApps == null) {
        return null;
    }
    for (RunningAppProcessInfo procInfo : runningApps) {
        if (procInfo.pid == pid) {
            return procInfo.processName;
        }
    }
    return null;
}

//或者
public static String getProcessName() {
  try {
    File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
    BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
    String processName = mBufferedReader.readLine().trim();
    mBufferedReader.close();
    return processName;
  } catch (Exception e) {
    e.printStackTrace();
    return null;
  }


//然后在 Application 的 onCreate 中获取进程名称并进行相应的判断
    String processName = getProcessName(this, android.os.Process.myPid());

    if (!TextUtils.isEmpty(processName) && processName.equals(this.getPackageName())) {//判断进程名，保证只有主进程运行
	//主进程初始化逻辑
	....

```

# 9. 前台进程、可见进程、服务进程、后台进程、空进程；
### 前台进程
用户当前操作所必需的进程。通常在任意给定时间前台进程都为数不多。只有在内存不足以支持它们同时继续运行这一万不得已的情况下，系统才会终止它们。
- 拥有用户正在交互的 Activity（已调用 onResume()）
- 拥有某个 Service，后者绑定到用户正在交互的 Activity
- 拥有正在“前台”运行的 Service（服务已调用 startForeground()）
- 拥有正执行一个生命周期回调的 Service（onCreate()、onStart() 或 onDestroy()）
- 拥有正执行其 onReceive() 方法的 BroadcastReceiver

总结: 一个进程是前台进程,对于activity而言,此时的activity正在与用户处于交互的情况;要是一个服务,要不就是执行了运行在前台的方法—startForeground()或者是和activity的互动是绑定的;或者是调用的自己的生命周期;要是广播接受者此调用了onReceive()方法.

### 可见进程
没有任何前台组件、但仍会影响用户在屏幕上所见内容的进程。可见进程被视为是极其重要的进程，除非为了维持所有前台进程同时运行而必须终止，否则系统不会终止这些进程。
- 拥有不在前台、但仍对用户可见的 Activity（已调用 onPause()）
- 拥有绑定到可见（或前台）Activity 的 Service

### 服务进程
 一个包含已启动服务的进程就是服务进程，服务没有用户界面，不与用户直接交互，但能够在后台长期运行，提供用户所关心的重要功能
通过startService来开启的一个服务,比如是服务开启的音乐的播放和下载数据

### 后台进程
如果一个进程不包含任何已经启动的服务，而且没有用户可见的Activity，则这个进程就是后台进程。

### 空进程
  空进程是不包含任何活跃组件的进程。在系统资源紧张时会被首先清除。

其他详情请参考:https://segmentfault.com/a/1190000006251859

# 10. 谈谈你对 JNI 和 NDK 的理解；
JNI 是 Java Native Interface 的缩写，即 Java 的本地接口。
目的是使得 Java 与本地其他语言（如 C/C++）进行交互。
JNI 是属于 Java 的，与 Android 无直接关系。
NDK 是 Native Development Kit 的缩写，是 Android 的工具开发包。
作用是更方便和快速开发 C/C++ 的动态库，并自动将动态库与应用一起打包到 apk。
NDK是属于 Android 的，与 Java 无直接关系。
总结：
JNI 是实现的目的，NDK 是 Android 中实现 JNI 的手段

# 11. 自定义view
11.1. view事件分发机制；上下滑动冲突问题如何处理；View的绘制流程
 view事件分发机制: ontouch ontouchEvent   performclick
View，ViewGroup事件分
- 1. Touch事件分发中只有两个主角:ViewGroup和View。ViewGroup包含onInterceptTouchEvent、dispatchTouchEvent、onTouchEvent三个相关事件。View包含dispatchTouchEvent、onTouchEvent两个相关事件。其中ViewGroup又继承于View。
- 2.ViewGroup和View组成了一个树状结构，根节点为Activity内部包含的一个ViwGroup。
- 3.触摸事件由Action_Down、Action_Move、Aciton_UP组成，其中一次完整的触摸事件中，Down和Up都只有一个，Move有若干个，可以为0个。
- 4.当Acitivty接收到Touch事件时，将遍历子View进行Down事件的分发。ViewGroup的遍历可以看成是递归的。分发的目的是为了找到真正要处理本次完整触摸事件的View，这个View会在onTouchuEvent结果返回true。
- 5.当某个子View返回true时，会中止Down事件的分发，同时在ViewGroup中记录该子View。接下去的Move和Up事件将由该子View直接进行处理。由于子View是保存在ViewGroup中的，多层ViewGroup的节点结构时，上级ViewGroup保存的会是真实处理事件的View所在的ViewGroup对象:如ViewGroup0-ViewGroup1-TextView的结构中，TextView返回了true，它将被保存在ViewGroup1中，而ViewGroup1也会返回true，被保存在ViewGroup0中。当Move和UP事件来时，会先从ViewGroup0传递至ViewGroup1，再由ViewGroup1传递至TextView。
- 6.当ViewGroup中所有子View都不捕获Down事件时，将触发ViewGroup自身的onTouch事件。触发的方式是调用super.dispatchTouchEvent函数，即父类View的dispatchTouchEvent方法。在所有子View都不处理的情况下，触发Acitivity的onTouchEvent方法。
- 7.onInterceptTouchEvent有两个作用：1.拦截Down事件的分发。2.中止Up和Move事件向目标View传递，使得目标View所在的ViewGroup捕获Up和Move事件。


view的绘制流程
自定义控件：
1、组合控件。这种自定义控件不需要我们自己绘制，而是使用原生控件组合成的新控件。如标题栏。
2、继承原有的控件。这种自定义控件在原生控件提供的方法外，可以自己添加一些方法。如制作圆角，圆形图片。
3、完全自定义控件：这个View上所展现的内容全部都是我们自己绘制出来的。比如说制作水波纹进度条。

View的绘制流程：OnMeasure()——>OnLayout()——>OnDraw()

第一步：OnMeasure()：测量视图大小。从顶层父View到子View递归调用measure方法，measure方法又回调OnMeasure。

第二步：OnLayout()：确定View位置，进行页面布局。从顶层父View向子View的递归调用view.layout方法的过程，即父View根据上一步measure子View所得到的布局大小和布局参数，将子View放在合适的位置上。

第三步：OnDraw()：绘制视图。ViewRoot创建一个Canvas对象，然后调用OnDraw()。六个步骤：①、绘制视图的背景；②、保存画布的图层（Layer）；③、绘制View的内容；④、绘制View子视图，如果没有就不用；
⑤、还原图层（Layer）；⑥、绘制滚动条。


#### 11.2. MVP，MVC，MVVM
此处延伸：手写mvp例子，与mvc之间的区别，mvp的优势
MVP模式，对应着Model--业务逻辑和实体模型,view--对应着activity，负责View的绘制以及与用户交互,Presenter--负责View和Model之间的交互,MVP模式是在MVC模式的基础上，将Model与View彻底分离使得项目的耦合性更低，
在Mvc中项目中的activity对应着mvc中的C--Controllor,而项目中的逻辑处理都是在这个C中处理，同时View与Model之间的交互，也是也就是说，mvc中所有的逻辑交互和用户交互，都是放在Controllor中，也就是activity中。View和model是可以直接通信的。而MVP模式则是分离的更加彻底，分工更加明确Model--业务逻辑和实体模型，view--负责与用户交互，Presenter 负责完成View于Model间的交互，
MVP和MVC最大的区别是MVC中是允许Model和View进行交互的，而MVP中很明显，Model与View之间的交互由Presenter完成。还有一点就是Presenter与View之间的交互是通过接口的



# 12. Android中的几种动画
帧动画：指通过指定每一帧的图片和播放时间，有序的进行播放而形成动画效果，比如想听的律动条。
补间动画：指通过指定View的初始状态、变化时间、方式，通过一系列的算法去进行图形变换，从而形成动画效果，主要有Alpha、Scale、Translate、Rotate四种效果。注意：只是在视图层实现了动画效果，并没有真正改变View的属性，比如滑动列表，改变标题栏的透明度。
属性动画：在Android3.0的时候才支持，通过不断的改变View的属性，不断的重绘而形成动画效果。相比于视图动画，View的属性是真正改变了。比如view的旋转，放大，缩小。


# Binder机制原理
在Android系统的Binder机制中，是有Client,Service,ServiceManager,Binder驱动程序组成的，其中Client，service，Service Manager运行在用户空间，Binder驱动程序是运行在内核空间的。而Binder就是把这4种组件粘合在一块的粘合剂，其中核心的组件就是Binder驱动程序，Service Manager提供辅助管理的功能，而Client和Service正是在Binder驱动程序和Service Manager提供的基础设施上实现C/S 之间的通信。其中Binder驱动程序提供设备文件/dev/binder与用户控件进行交互，Client、Service，Service Manager通过open和ioctl文件操作相应的方法与Binder驱动程序进行通信。而Client和Service之间的进程间通信是通过Binder驱动程序间接实现的。而Binder Manager是一个守护进程，用来管理Service，并向Client提供查询Service接口的能力。

# 热修复的原理
我们知道Java虚拟机 —— JVM 是加载类的class文件的，而Android虚拟机——Dalvik/ART VM 是加载类的dex文件，
而他们加载类的时候都需要ClassLoader,ClassLoader有一个子类BaseDexClassLoader，而BaseDexClassLoader下有一个
数组——DexPathList，是用来存放dex文件，当BaseDexClassLoader通过调用findClass方法时，实际上就是遍历数组，
找到相应的dex文件，找到，则直接将它return。而热修复的解决方法就是将新的dex添加到该集合中，并且是在旧的dex的前面，
所以就会优先被取出来并且return返回。


# RecyclerView和ListView的区别
RecyclerView可以完成ListView,GridView的效果，还可以完成瀑布流的效果。同时还可以设置列表的滚动方向（垂直或者水平）；
RecyclerView中view的复用不需要开发者自己写代码，系统已经帮封装完成了。
RecyclerView可以进行局部刷新。
RecyclerView提供了API来实现item的动画效果。
在性能上：
如果需要频繁的刷新数据，需要添加动画，则RecyclerView有较大的优势。
如果只是作为列表展示，则两者区别并不是很大。


# Universal-ImageLoader，Picasso，Fresco，Glide对比
Fresco 是 Facebook 推出的开源图片缓存工具，主要特点包括：两个内存缓存加上 Native 缓存构成了三级缓存，
优点：
1. 图片存储在安卓系统的匿名共享内存, 而不是虚拟机的堆内存中, 图片的中间缓冲数据也存放在本地堆内存, 所以, 应用程序有更多的内存使用, 不会因为图片加载而导致oom, 同时也减少垃圾回收器频繁调用回收 Bitmap 导致的界面卡顿, 性能更高。
2. 渐进式加载 JPEG 图片, 支持图片从模糊到清晰加载。
3. 图片可以以任意的中心点显示在 ImageView, 而不仅仅是图片的中心。
4. JPEG 图片改变大小也是在 native 进行的, 不是在虚拟机的堆内存, 同样减少 OOM。
5. 很好的支持 GIF 图片的显示。

缺点:
1. 框架较大, 影响 Apk 体积
2. 使用较繁琐

Universal-ImageLoader：（估计由于HttpClient被Google放弃，作者就放弃维护这个框架）
优点：
1.支持下载进度监听
2.可以在 View 滚动中暂停图片加载，通过 PauseOnScrollListener 接口可以在 View 滚动中暂停图片加载。
3.默认实现多种内存缓存算法 这几个图片缓存都可以配置缓存算法，不过 ImageLoader 默认实现了较多缓存算法，如 Size 最大先删除、使用最少先删除、最近最少使用、先进先删除、时间最长先删除等。
4.支持本地缓存文件名规则定义

Picasso 优点
1. 自带统计监控功能。支持图片缓存使用的监控，包括缓存命中率、已使用内存大小、节省的流量等。
2.支持优先级处理。每次任务调度前会选择优先级高的任务，比如 App 页面中 Banner 的优先级高于 Icon 时就很适用。
3.支持延迟到图片尺寸计算完成加载
4.支持飞行模式、并发线程数根据网络类型而变。 手机切换到飞行模式或网络类型变换时会自动调整线程池最大并发数，比如 wifi 最大并发为 4，4g 为 3，3g 为 2。  这里 Picasso 根据网络类型来决定最大并发数，而不是 CPU 核数。
5.“无”本地缓存。无”本地缓存，不是说没有本地缓存，而是 Picasso 自己没有实现，交给了 Square 的另外一个网络库 okhttp 去实现，这样的好处是可以通过请求 Response Header 中的 Cache-Control 及 Expired 控制图片的过期时间。

 Glide 优点
1. 不仅仅可以进行图片缓存还可以缓存媒体文件。Glide 不仅是一个图片缓存，它支持 Gif、WebP、缩略图。甚至是 Video，所以更该当做一个媒体缓存。
2. 支持优先级处理。
3. 与 Activity/Fragment 生命周期一致，支持 trimMemory。Glide 对每个 context 都保持一个 RequestManager，通过 FragmentTransaction 保持与 Activity/Fragment 生命周期一致，并且有对应的 trimMemory 接口实现可供调用。
4. 支持 okhttp、Volley。Glide 默认通过 UrlConnection 获取数据，可以配合 okhttp 或是 Volley 使用。实际 ImageLoader、Picasso 也都支持 okhttp、Volley。
5. 内存友好。Glide 的内存缓存有个 active 的设计，从内存缓存中取数据时，不像一般的实现用 get，而是用 remove，再将这个缓存数据放到一个 value 为软引用的 activeResources map 中，并计数引用数，在图片加载完成后进行判断，如果引用计数为空则回收掉。内存缓存更小图片，Glide 以 url、view_width、view_height、屏幕的分辨率等做为联合 key，将处理后的图片缓存在内存缓存中，而不是原始图片以节省大小与 Activity/Fragment 生命周期一致，支持 trimMemory。图片默认使用默认 RGB_565 而不是 ARGB_888，虽然清晰度差些，但图片更小，也可配置到 ARGB_888。
6.Glide 可以通过 signature 或不使用本地缓存支持 url 过期

# Xutils, OKhttp, Volley, Retrofit对比
Xutils这个框架非常全面，可以进行网络请求，可以进行图片加载处理，可以数据储存，还可以对view进行注解，使用这个框架非常方便，但是缺点也是非常明显的，使用这个项目，会导致项目对这个框架依赖非常的严重，一旦这个框架出现问题，那么对项目来说影响非常大的。、
OKhttp：Android开发中是可以直接使用现成的api进行网络请求的。就是使用HttpClient,HttpUrlConnection进行操作。okhttp针对Java和Android程序，封装的一个高性能的http请求库，支持同步，异步，而且okhttp又封装了线程池，封装了数据转换，封装了参数的使用，错误处理等。API使用起来更加的方便。但是我们在项目中使用的时候仍然需要自己在做一层封装，这样才能使用的更加的顺手。
Volley：Volley是Google官方出的一套小而巧的异步请求库，该框架封装的扩展性很强，支持HttpClient、HttpUrlConnection， 甚至支持OkHttp，而且Volley里面也封装了ImageLoader，所以如果你愿意你甚至不需要使用图片加载框架，不过这块功能没有一些专门的图片加载框架强大，对于简单的需求可以使用，稍复杂点的需求还是需要用到专门的图片加载框架。Volley也有缺陷，比如不支持post大数据，所以不适合上传文件。不过Volley设计的初衷本身也就是为频繁的、数据量小的网络请求而生。
Retrofit：Retrofit是Square公司出品的默认基于OkHttp封装的一套RESTful网络请求框架，RESTful是目前流行的一套api设计的风格， 并不是标准。Retrofit的封装可以说是很强大，里面涉及到一堆的设计模式,可以通过注解直接配置请求，可以使用不同的http客户端，虽然默认是用http ，可以使用不同Json Converter 来序列化数据，同时提供对RxJava的支持，使用Retrofit + OkHttp + RxJava + Dagger2 可以说是目前比较潮的一套框架，但是需要有比较高的门槛。
Volley VS OkHttp
Volley的优势在于封装的更好，而使用OkHttp你需要有足够的能力再进行一次封装。而OkHttp的优势在于性能更高，因为 OkHttp基于NIO和Okio ，所以性能上要比 Volley更快。IO 和 NIO这两个都是Java中的概念，如果我从硬盘读取数据，第一种方式就是程序一直等，数据读完后才能继续操作这种是最简单的也叫阻塞式IO,还有一种是你读你的,程序接着往下执行，等数据处理完你再来通知我，然后再处理回调。而第二种就是 NIO 的方式，非阻塞式， 所以NIO当然要比IO的性能要好了,而 Okio是 Square 公司基于IO和NIO基础上做的一个更简单、高效处理数据流的一个库。理论上如果Volley和OkHttp对比的话，更倾向于使用 Volley，因为Volley内部同样支持使用OkHttp,这点OkHttp的性能优势就没了，  而且 Volley 本身封装的也更易用，扩展性更好些。
OkHttp VS Retrofit
毫无疑问，Retrofit 默认是基于 OkHttp 而做的封装，这点来说没有可比性，肯定首选 Retrofit。
Volley VS Retrofit
这两个库都做了不错的封装，但Retrofit解耦的更彻底,尤其Retrofit2.0出来，Jake对之前1.0设计不合理的地方做了大量重构， 职责更细分，而且Retrofit默认使用OkHttp,性能上也要比Volley占优势，再有如果你的项目如果采用了RxJava ，那更该使用  Retrofit 。所以这两个库相比，Retrofit更有优势，在能掌握两个框架的前提下该优先使用 Retrofit。但是Retrofit门槛要比Volley稍高些，要理解他的原理，各种用法，想彻底搞明白还是需要花些功夫的，如果你对它一知半解，那还是建议在商业项目使用Volley吧。


# TCP和UDP的区别
tcp是面向连接的，由于tcp连接需要三次握手，所以能够最低限度的降低风险，保证连接的可靠性。
udp 不是面向连接的，udp建立连接前不需要与对象建立连接，无论是发送还是接收，都没有发送确认信号。所以说udp是不可靠的。
由于udp不需要进行确认连接，使得UDP的开销更小，传输速率更高，所以实时行更好。

# Socket建立网络连接的步骤
建立Socket连接至少需要一对套接字，其中一个运行与客户端--ClientSocket，一个运行于服务端--ServiceSocket
1、服务器监听：服务器端套接字并不定位具体的客户端套接字，而是处于等待连接的状态，实时监控网络状态，等待客户端的连接请求。
2、客户端请求：指客户端的套接字提出连接请求，要连接的目标是服务器端的套接字。注意：客户端的套接字必须描述他要连接的服务器的套接字，
指出服务器套接字的地址和端口号，然后就像服务器端套接字提出连接请求。
3、连接确认：当服务器端套接字监听到客户端套接字的连接请求时，就响应客户端套接字的请求，建立一个新的线程，把服务器端套接字的描述
发给客户端，一旦客户端确认了此描述，双方就正式建立连接。而服务端套接字则继续处于监听状态，继续接收其他客户端套接字的连接请求。


# android有序广播和无序广播的区别
BroadcastReceiver所对应的广播分两类：普通广播和有序广播。
普通广播：通过Context.sendBroadcast()方法来发送，它是完全异步的。
所有的receivers（接收器）的执行顺序不确定，因此所有的receivers（接收器）接收broadcast的顺序不确定。
这种方式效率更高，但是BroadcastReceiver无法使用setResult系列、getResult系列及abort（中止）系列API

有序广播：是通过Context.sendOrderedBroadcast来发送，所有的receiver依次执行。
BroadcastReceiver可以使用setResult系列函数来结果传给下一个BroadcastReceiver，通过getResult系列函数来取得上个BroadcastReceiver返回的结果，并可以abort系列函数来让系统丢弃该广播，使用该广播不再传送到别的BroadcastReceiver。
可以通过在intent-filter中设置android:priority属性来设置receiver的优先级，优先级相同的receiver其执行顺序不确定。
如果BroadcastReceiver是代码中注册的话，且其intent-filter拥有相同android:priority属性的话，先注册的将先收到广播。
有序广播，即从优先级别最高的广播接收器开始接收，接收完了如果没有丢弃，就下传给下一个次高优先级别的广播接收器进行处理，依次类推，直到最后。如果多个应用程序设置的优先级别相同，则谁先注册的广播，谁就可以优先接收到广播。


# Service的bindService和startService混合使用及其关闭分析
1.startService启动方式过程及生命周期：
onCreate()–> onStartCommand()/onStart() —> onDestory();
1）创建服务onCreate()在整个生命周期仅执行一次，每次调用服务都会执行onStart()或onStartCommand();
2）停止服务onDestory()在整个生命周期仅执行一次；
3）服务一旦启动，生命周期将不受限于UI线程。应用（Activity）终止，服务仍然在后台运行；
4）直接启动的服务，其它应用不能调用其中的功能。

2.bindService绑定方式过程及生命周期：
onCreate() —> onBind() —> onUnbind() –> onDestory();
1）创建服务onCreate()在整个生命周期仅执行一次；
2）每次调用服务必须首先bindService/onBind，执行unbindService/onUnbind后不能调用；
3）服务的生命周期受限于UI线程。一旦应用（Activity）终止，服务将onDestory销毁；
4）可以在绑定后调用服务里的功能。

#### 混合使用
- 1.如果先bindService,再startService:
在bind的Activity退出的时候,Service会执行unBind方法而不执行onDestory方法,因为有startService方法调用过,所以Activity与Service解除绑定后会有一个与调用者没有关连的Service存在

- 2.如果先bindService,再startService,再调用Context.stopService
Service的onDestory方法不会立刻执行,因为有一个与Service绑定的Activity,但是在Activity退出的时候,会执行onDestory,如果要立刻执行stopService,就得先解除绑定

- 3 先startService，再bindService。
首先在主界面创建时，startService(intent)启动方式开启服务，保证服务长期后台运行；
然后调用服务时，bindService(intent, connection, BIND_AUTO_CREATE)绑定方式绑定服务，这样可以调用服务的方法；

调用服务功能结束后，unbindService(connection)解除绑定服务，置空中介对象；
最后不再需要服务时，stopService(intent)终止服务。

#  sharepreference  contentProvider是线程安全的吗,是进程安全你的吗?

sharepreference是线程安全的, 进程不安全;
sharepreference因为获取和赋值的时候加了锁,所以是线程安全的.
```
ContextImpl#getSharedPreferences():
public SharedPreferences getSharedPreferences(String name, int mode) {
        // At least one application in the world actually passes in a null
        // name.  This happened to work because when we generated the file name
        // we would stringify it to "null.xml".  Nice.
        if (mPackageInfo.getApplicationInfo().targetSdkVersion <
                Build.VERSION_CODES.KITKAT) {
            if (name == null) {
                name = "null";
            }
        }

        File file;
        synchronized (ContextImpl.class) {
            if (mSharedPrefsPaths == null) {
                mSharedPrefsPaths = new ArrayMap<>();
            }
            file = mSharedPrefsPaths.get(name);
            if (file == null) {
                file = getSharedPreferencesPath(name);
                mSharedPrefsPaths.put(name, file);
            }
        }
        return getSharedPreferences(file, mode);
    }

// 在赋值的时候也加了锁
android.app.SharedPreferencesImpl.EditorImpl#putString
        @Override
        public Editor putString(String key, @Nullable String value) {
            synchronized (mEditorLock) {
                mModified.put(key, value);
                return this;
            }
        }
```
当多个进程同时而又高频的调用commit方法时，就会导致文件被反复覆盖写入，而并没有被及时读取，所以造成进程间数据的不同步

ps:sharepreference的apply 和 commit:
apply没有返回值而commit返回boolean表明修改是否提交成功
apply是将修改数据原子提交到内存，而后异步真正提交到硬件磁盘；而commit是同步的提交到硬件磁盘，因此，在多个并发的提交commit的时候，他们会等待正在处理的commit保存到磁盘后在操作，从而降低了效率。而apply只是原子的提交到内存，后面有调用apply的函数的将会直接覆盖前面的内存数据，这样从一定程度上提高了很多效率。
apply方法不会提示任何失败的提示



Provider线程不安全, 进程安全;
Provider是单例对象，但可能会在多个线程中执行数据操作的方法。
那么，如果Provider中使用的是同一个SQLiteOpenHelper实例，是可以保证数据库创建/升级/降级线程安全的。
```
android.database.sqlite.SQLiteOpenHelper.getWritableDatabase
    public SQLiteDatabase getWritableDatabase() {
        synchronized (this) {
            return getDatabaseLocked(true);
        }
    }

```

面试题：多个进程同时调用一个ContentProvider的query获取数据，ContentPrvoider是如何反应的呢？
标准答案：一个content provider可以接受来自另外一个进程的数据请求。尽管ContentResolver与ContentProvider类隐藏了实现细节，但是ContentProvider所提供的query()，insert()，delete()，update()都是在ContentProvider进程的线程池中被调用执行的，而不是进程的主线程中。这个线程池是有Binder创建和维护的，其实使用的就是每个应用进程中的Binder线程池。


更多信息参看:https://www.cnblogs.com/zdz8207/p/android-learning-2018.html


# activity生命周期 A启动B，B返回A, A,B的全生命周期
1.启动A
      Activity的初始化了，A第一步创建onCreate(20569): -------->成功！
     Activity被激活A，onStart   Activity显示在屏幕上(20569): -------->成功！
     Activity被恢复A，onResume(20569): -------->成功！
2.在A中启动B
   Activity被暂停A，Activity进入暂停状态onPause(21407): -------->成功！
   B------------------>(21407): 创建！
   Activity被激活B，onStart   Activity显示在屏幕上(21407): -------->成功！
   Activity被恢复B，onResume(21407): -------->成功！
   Activity被停止A，Activity进入停止状态onStop(21407): -------->成功！

3.从B中返回A（按物理硬件返回键）
     Activity被暂停B，Activity进入暂停状态onPause(21407): -------->成功！
     Activity被重启A，Activity从停止状态进入活动状态onRestart(21407): -------->成功！
     Activity被激活A，onStart   Activity显示在屏幕上(21407): -------->成功！
     Activity被恢复A，onResume(21407): -------->成功！
     Activity被停止B，Activity进入停止状态onStop(21407): -------->成功！
     Activity的消亡了，B最后的生命！销毁onDestroy(21407): -------->成功！
4.继续返回
     Activity被暂停A，Activity进入暂停状态onPause(21407): -------->成功！
     Activity被停止A，Activity进入停止状态onStop(21407): -------->成功！
     Activity的消亡了，A最后的生命！销毁onDestroy(21407): -------->成功！








