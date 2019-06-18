package network;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MulRetryInterceptor implements Interceptor {

    private static final String TAG = "MulRetryInterceptor";

    public int maxRetry;//最大重试次数
    private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

    public MulRetryInterceptor(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();


        Response response = chain.proceed(request);

        retryNum = 0;
        while (!response.isSuccessful() && retryNum < maxRetry) {
            retryNum++;

            response = chain.proceed(request);
            Log.d(TAG, "response=" + response.isSuccessful()+",and code=" + response.code()+"," +retryNum);
        }
        return response;
    }
}
