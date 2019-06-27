package webviewTest;

import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.R;

import java.util.HashMap;
import java.util.Set;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private Button btnAndroidCallJS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        webView = findViewById(R.id.web_view);
        //防止外部浏览器调用此链接
        webView.setWebViewClient(new WebViewClient());
        WebSettings settings = webView.getSettings();
        //允许WebView使用JS
        settings.setJavaScriptEnabled(true);
        //支持通过JS打开新窗口(允许JS弹窗)
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        //android调用JS代码的Button
        btnAndroidCallJS = findViewById(R.id.btn_call_js);

        webView.loadUrl("file:test.html");


        //androidCallJS();
        //jsCallAndroid();
        //jsCallAndroid2();
        //jsCallAndroid3();


    }

    //Android调用JS代码
    private void androidCallJS() {

        btnAndroidCallJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < 18) {
                    // 方式1----通过WebView的loadUrl()
                    webView.loadUrl("javascript:callJS()");
                } else {
                    //方式2--通过WebView的evaluateJavascript()
                    webView.evaluateJavascript("javascript:callJS()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            //s是JS方法的返回值
                            Log.e("zw", s); //这里s是“Android调用了JS的callJS()”
                        }
                    });
                }

            }
        });

        // 由于设置了弹窗检验调用结果,所以需要支持js对话框
        // webview只是载体，内容的渲染需要使用webviewChromClient类去实现
        // 通过设置WebChromeClient对象处理JavaScript的对话框
        //设置响应js 的Alert()函数
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(WebViewActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

        });
    }

    private void jsCallAndroid() {
        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        webView.addJavascriptInterface(new AndroidToJS(), "androidToJS");
    }

    private void jsCallAndroid2() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.e("ll", url);
                if (url.startsWith("js")) {
                    if (url.contains("webview")) {
                        Toast.makeText(getApplicationContext(), "heihei", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();

                Log.e("ll", uri.getScheme() + " , " + uri.getAuthority() + " , ");
                if ("js".equals(uri.getScheme())) {

                    if ("webview".equals(uri.getAuthority())) {
                        Toast.makeText(getApplicationContext(), "heihei", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                return super.shouldOverrideUrlLoading(view, request);
            }
        });
    }

    private void jsCallAndroid3() {
        webView.setWebChromeClient(new WebChromeClient() {
          // 拦截输入框(原理同方式2)
          // 参数message:代表promt（）的内容（不是url）
          // 参数result:代表输入框的返回值
          @Override
          public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
              // 根据协议的参数，判断是否是所需要的url(原理同方式2)
              // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
              //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

              Uri uri = Uri.parse(message);
              // 如果url的协议 = 预先约定的 js 协议
              // 就解析往下解析参数
              if (uri.getScheme().equals("js")) {

                  // 如果 authority  = 预先约定协议里的 webview，即代表都符合约定的协议
                  // 所以拦截url,下面JS开始调用Android需要的方法
                  if (uri.getAuthority().equals("webview")) {

                      //
                      // 执行JS所需要调用的逻辑
                      System.out.println("js调用了Android的方法");
                      // 可以在协议上带有参数并传递到Android上
                      HashMap<String, String> params = new HashMap<>();
                      Set<String> collection = uri.getQueryParameterNames();

                      //参数result:代表消息框的返回值(输入值)
                      result.confirm("js调用了Android的方法成功啦");
                  }
                  return true;
              }
              return super.onJsPrompt(view, url, message, defaultValue, result);
          }
           // 通过alert()和confirm()拦截的原理相同，此处不作过多讲述
          // 拦截JS的警告框
          @Override
          public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
              return super.onJsAlert(view, url, message, result);
          }

          // 拦截JS的确认框
          @Override
          public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
              return super.onJsConfirm(view, url, message, result);
          }
        });
    }

}
