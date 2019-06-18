package com.example.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class MIntentService extends IntentService {

    public MIntentService(){
        super("MIntentService");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MIntentService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        Log.e("MIntentService--", "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("MIntentService--", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("MIntentService--", Thread.currentThread().getName() + "--" + intent.getStringExtra("info") );
        for(int i = 0; i < 100; i++){ //耗时操作
            Log.i("onHandleIntent--",  i + "--" + Thread.currentThread().getName());
        }
    }

    @Override
    public void onDestroy() {
        Log.e("MIntentService--", "onDestroy");
        super.onDestroy();
    }
}
