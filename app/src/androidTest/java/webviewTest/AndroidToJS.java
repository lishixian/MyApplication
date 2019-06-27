package webviewTest;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class AndroidToJS {
    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void callAndroid(String msg){
        Log.e("zw","JS调用了Android的callAndroid()，msg : " + msg);
    }
}
