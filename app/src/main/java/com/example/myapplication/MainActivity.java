package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;
import android.widget.TextView;
//import android.support.design.widget.NavigationView;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import base.VActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import data.CardItem;

public class MainActivity extends VActivity<MainPresenter> {

    private Unbinder unBinder;
    private WebView webView;
    private CopyOnWriteArrayList list;

    @BindView(R.id.bt_text_view)
    TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unBinder = ButterKnife.bind(this);

        EventBus.getDefault().register(this);

        startActivity(new Intent(""));
        Handler h;
    }

    @Override
    protected void onResume() {
        super.onResume();
        intentClick();
        NdkJniUtils jni = new NdkJniUtils();
        jni.getCLanguageString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (unBinder != null && unBinder != Unbinder.EMPTY) {
            unBinder.unbind();
            unBinder = null;
        }
    }

    public void intentClick(){
        Intent intent = new Intent(this, MIntentService.class);
        intent.putExtra("info", "good good study");
        startService(intent);
    }

    private TextView statusTextView = null;
    Handler handler = new Handler();

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @Override
    public MainPresenter newP() {
        return new MainPresenter();
    }

    public void showServiceCard(List<CardItem> cardList) {

    }

    class DownloadThread extends Thread {
        @Override
        public void run() {
            try {
                System.out.println("DownloadThread id " + Thread.currentThread().getId());
                System.out.println("开始下载文件");
                //此处让线程DownloadThread休眠5秒中，模拟文件的耗时过程
                Thread.sleep(5000);
                System.out.println("文件下载完成");
                //文件下载完成后更新UI
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Runnable thread id " + Thread.currentThread().getId());
                        MainActivity.this.statusTextView.setText("文件下载完成");
                    }
                };
                handler.post(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //uiHandler在主线程中创建，所以自动绑定主线程
    private Handler uiHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    System.out.println("handleMessage thread id " + Thread.currentThread().getId());
                    System.out.println("msg.arg1:" + msg.arg1);
                    System.out.println("msg.arg2:" + msg.arg2);
                    MainActivity.this.statusTextView.setText("文件下载完成");
                    break;
            }
        }
    };

    class DownloadThread2 extends Thread{
        @Override
        public void run() {
            try{
                System.out.println("DownloadThread id " + Thread.currentThread().getId());
                System.out.println("开始下载文件");
                //此处让线程DownloadThread休眠5秒中，模拟文件的耗时过程
                Thread.sleep(5000);
                System.out.println("文件下载完成");
                //文件下载完成后更新UI
                Message msg = new Message();
                //虽然Message的构造函数式public的，我们也可以通过以下两种方式通过循环对象获取Message
                //msg = Message.obtain(uiHandler);
                //msg = uiHandler.obtainMessage();

                //what是我们自定义的一个Message的识别码，以便于在Handler的handleMessage方法中根据what识别
                //出不同的Message，以便我们做出不同的处理操作
                msg.what = 1;

                //我们可以通过arg1和arg2给Message传入简单的数据
                msg.arg1 = 123;
                msg.arg2 = 321;
                //我们也可以通过给obj赋值Object类型传递向Message传入任意数据
                //msg.obj = null;
                //我们还可以通过setData方法和getData方法向Message中写入和读取Bundle类型的数据
                //msg.setData(null);
                //Bundle data = msg.getData();

                //将该Message发送给对应的Handler
                uiHandler.sendMessage(msg);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    private HashMap<String,String> hashMap = new HashMap<>();
    private void test(){
        //NavigationView  v;
        hashMap.put("11","22");
    }

}


