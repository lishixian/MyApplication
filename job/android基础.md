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
        // 这里msg携带了handler实例,会在从queue取出msg时用到,将操作返回到主线程操作
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
             // 当消息队列中没有消息，或者是按时间来说是该第一个第一个触发的
             // 从这里看出是把消息按时间顺序排序
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

            // 这里mag.target就是handler,这里又会回handler里去处理消息,
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
android.app.Instrumentation.callActivityOnCreate()
    activity.performCreate(icicle);
android.app.Activity#performCreate()
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
- SingleInstance: 单利模式. 电话界面


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
 前台进程是Android系统中最重要的进程，是与用户正在交互的进程
当是一个Activity的时候,此时的他正在用用户进行交互,并且他的OnResume()方法别调用
当是一个服务是前台进程的时候: 表示的就是这个Service绑定了该用户的监护和活动
当是一个服务的话,表示的就是调用了服务的 startForeground()方法
当是一个服务是一个前台进程的时候,此时的Service执行了生命周期的回调;
当是一个广播的时候,此时的广播接受者执行了onReceive()方法;
总结: 一个进程是前台进程,对于activity而言,此时的activity正在与用户处于交互的情况;要是一个服务,要不就是执行了运行在前台的方法—startForeground()或者是和activity的互动是绑定的;或者是调用的自己的生命周期;要是广播接受者此调用了onReceive()方法.

### 可见进程
可见进程指部分程序界面能够被用户看见，却不在前台与用户交互。
1. 当是一个activity处于可见的进程,此时就是用户可见,但是调用了activity的onPause()方法
2. 当是服务是一个可见进程,此时就是服务和绑定的activity是一个可见的activity

### 服务进程
 一个包含已启动服务的进程就是服务进程，服务没有用户界面，不与用户直接交互，但能够在后台长期运行，提供用户所关心的重要功能
通过startService来开启的一个服务,比如是服务开启的音乐的播放和下载数据

### 后台进程
如果一个进程不包含任何已经启动的服务，而且没有用户可见的Activity，则这个进程就是后台进程。

### 空进程
  空进程是不包含任何活跃组件的进程。在系统资源紧张时会被首先清除。



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
11.1. view事件分发机制；上下滑动冲突问题如何处理；
11.2. mvp和mvvm的区别；








