package com.example.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class MessengerActivity extends Activity {
    private static final String TAG = "MessengerActivity";
    private Messenger mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = new Messenger(service);

            Message msg = Message.obtain(null,1);
            Bundle data = new Bundle();
            data.putString("msg","hello~");
            msg.setData(data);

            msg.replyTo = reMessenger;

            try{
                mService.send(msg);
            }catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private final Messenger reMessenger = new Messenger(new MessengerHandler());
    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 2:
                    Log.d(TAG, "handleMessage: msg:"
                            + msg.getData().getString("reply"));

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    protected void onCreate(Bundle save){
        super.onCreate(save);
        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE);
        Handler h = new Handler();
    }

    private static void test(){
        //创建一个引用队列
        ReferenceQueue queue = new ReferenceQueue();

// 创建弱引用，此时状态为Active，并且Reference.pending为空，当前Reference.queue = 上面创建的queue，并且next=null
        WeakReference reference = new WeakReference(new Object(), queue);
        System.out.println(reference);
// 当GC执行后，由于是弱引用，所以回收该object对象，并且置于pending上，此时reference的状态为PENDING
        System.gc();

        /* ReferenceHandler从pending中取下该元素，并且将该元素放入到queue中，此时Reference状态为ENQUEUED，Reference.queue = ReferenceENQUEUED */

        /* 当从queue里面取出该元素，则变为INACTIVE，Reference.queue = Reference.NULL */
        Reference reference1 = null;
        try {
            reference1 = queue.remove();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(reference1);

    }

    private static class MyThread extends Thread {
        public void run(){
            Looper.prepare();
            Handler h = new Handler();
            Looper.loop();
        }
    }

    protected void onDestroy(){
        unbindService(mConnection);
        super.onDestroy();
        //mHandler.removeCallbacksAndMessages(null);
    }

    static class MyHandler extends Handler {
        WeakReference<Activity > mActivityReference;

        MyHandler(Activity activity) {
            mActivityReference= new WeakReference<Activity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final Activity activity = mActivityReference.get();
            if (activity != null) {
                //mImageView.setImageBitmap(mBitmap);
            }
        }
    }

}
