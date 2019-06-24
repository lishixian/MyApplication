package com.example.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

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
    }

    protected void onDestroy(){
        unbindService(mConnection);
        super.onDestroy();
    }
}
