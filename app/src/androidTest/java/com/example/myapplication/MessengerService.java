package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {

    private static final String TAG = "MessengerService";

    private static class MessengerHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:
                    Log.d(TAG, "handleMessage: msg:"
                            + msg.getData().getString("msg"));

                    Messenger client = msg.replyTo;
                    Message remsg = Message.obtain(null,2);
                    Bundle data = new Bundle();
                    data.putString("reply","hello too~");
                    remsg.setData(data);

                    try{
                        client.send(msg);
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    private final Messenger mMessenger = new Messenger(new MessengerHandler());

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
