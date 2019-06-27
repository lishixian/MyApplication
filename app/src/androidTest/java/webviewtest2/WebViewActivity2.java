package webviewtest2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import webviewTest.WebViewActivity;

public class WebViewActivity2 extends AppCompatActivity {

    private WebView webView;
    private WebSettings seting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initWeb();
        initSetting();
        webView.addJavascriptInterface(new JSObject(), "jsAndroid");
    }
    private void initWeb() {
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //添加js代码
                view.loadUrl("javascript:function img(){" +
                        "var href=document.getElementsByTagName(\"img\");" +
                        "\t\t for(var i=0;i<href.length;i++){\n" +
                        "\t\t \t   var a=document.getElementsByTagName(\"img\")[i];\n" +
                        "\t\t \t   a.onclick=function(){\n" +
                        "\t\t \t        window.jsAndroid.onShowImage(this.src)" +
                        "\t\t \t   };\n" +
                        "\t\t }" +
                        "}");
                //执行js函数
                view.loadUrl("javascript:img()");
            }
        });
    }
    private void initSetting() {
        seting = webView.getSettings();
        seting.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        seting.setJavaScriptCanOpenWindowsAutomatically(true);
        //防止中文乱码
        seting.setDefaultTextEncodingName("UTF-8");
        //设置webview的缓存
        seting.setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    class JSObject {
        @JavascriptInterface
        public void onShowImage(String src) {
            Intent intent = new Intent(WebViewActivity2.this, WebViewActivity.class);
            intent.putExtra("src", src);
            startActivity(intent);
        }
    }


}
