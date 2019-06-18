package network;

import com.example.myapplication.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static OkHttpClient mCommonSignClient;

    private static OkHttpClient mCalendarSignClient;

    static {
        //initCommonSignClient();
        initCalendarSignClient();
    }

    public static CalendarService calendarApi() {
        return createApi(CalendarService.class, mCalendarSignClient, "http://beta.teauicalendar.scloud.lfengmobile.com");
    }


    /**
     * 根据传入的baseUrl，和api创建retrofit
     */
    private static <T> T createApi(Class<T> clazz, OkHttpClient client, String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(clazz);
    }

    private static OkHttpClient.Builder createOkHttpBuider() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS);
    }

    /**
     * 初始化OKHttpClient,设置缓存,设置超时时间,设置打印日志,设置UA拦截器
     */
    /*private static void initCommonSignClient() {
        if (mCommonSignClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mCommonSignClient == null) {
                    mCommonSignClient = createOkHttpBuider()
                            //.addInterceptor(new MulRetryInterceptor(6))
                            .addInterceptor(new AuthorizationInterceptor())
                            .addInterceptor(new MulRetryInterceptor(2))
                            .build();
                }
            }
        }
    }*/

    private static void initCalendarSignClient() {
        if (mCalendarSignClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mCalendarSignClient == null) {
                    //设置Http缓存
                    mCalendarSignClient = createOkHttpBuider()
                            //.addInterceptor(new MulRetryInterceptor(6))
                            .addInterceptor(new URLSignInterceptor())
                            .addInterceptor(new MulRetryInterceptor(2))
                            .build();
                }
            }
        }
    }

    /**
     * 添加UA拦截器
     * 暂时不用
     */
    /*private static class UserAgentInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request requestWithUserAgent = originalRequest.newBuilder()
                    .removeHeader("User-Agent")
                    .addHeader("User-Agent", HttpConstants.UA)
                    .build();
            return chain.proceed(requestWithUserAgent);
        }
    }*/

    /**
     * 为okhttp添加缓存，这里是考虑到服务器不支持缓存时，从而让okhttp支持缓存
     */
    /*private static class CacheInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            // 有网络时 设置缓存超时时间1个小时
            int maxAge = 60 * 60;
            // 无网络时，设置超时为1天
            int maxStale = 60 * 60 * 24;
            Request request = chain.request();
            if (NetworkUtil.isNetworkAvailable()) {
                //有网络时只从网络获取
                request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
            } else {
                //无网络时只从缓存中读取
                request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
            }
            Response response = chain.proceed(request);
            if (NetworkUtil.isNetworkAvailable()) {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                response = response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
        }
    }*/
}
