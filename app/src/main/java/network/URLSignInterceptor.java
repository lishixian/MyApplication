package network;

import android.text.TextUtils;

import java.io.IOException;
import java.util.LinkedHashMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class URLSignInterceptor implements Interceptor {

    private static final String TAG = "URLSignInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        HttpUrl url = request.url();
        String query = url.query();
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(query)) {
            String[] paramArray = query.split("&");
            for (String param : paramArray) {
                String[] values = param.split("=");
                if (values.length >= 2) {
                    paramMap.put(values[0], values[1]);
                }
            }
        }

        long timemillis = System.currentTimeMillis();
        //String authorizationHeader =
         //       LeSignature.getSignature(HttpConstants.Key.CALENDAR_ACCESS_KEY,
         //               HttpConstants.Key.CALENDAR_SECRET_KEY, paramMap, timemillis);

        HttpUrl.Builder builder = request.url().newBuilder();
        //builder.addQueryParameter(HttpConstants.Param.AK, HttpConstants.Key.CALENDAR_ACCESS_KEY);
        //builder.addQueryParameter(HttpConstants.Param._TIME, String.valueOf(timemillis));
        //builder.addQueryParameter(HttpConstants.Param._SIGN, authorizationHeader);

        Request.Builder requestBuilder = request.newBuilder();
        requestBuilder.url(builder.build());
        request = requestBuilder.build();

        return chain.proceed(request);
    }
}
