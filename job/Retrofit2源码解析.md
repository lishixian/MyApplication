Retrofit2源码解析系列

Retrofit2源码解析(一)
Retrofit2源码解析——网络调用流程(下)

本文基于Retrofit2的2.4.0版本
````implementation 'com.squareup.retrofit2:retrofit:2.4.0'````

网络调用流程分析
我们在发起异步网络请求时是这样调用的：
````
MyService myService = retrofit.create(MyService.class);
Call<IpBean> call = myService.getData();
call.enqueue(new Callback<IpBean>() {
    @Override
    public void onResponse(Call<IpBean> call, Response<IpBean> response) {

    }

    @Override
    public void onFailure(Call<IpBean> call, Throwable t) {

    }
});
```
总结起来就是三步：通过create方法生成我们的接口对象、调用接口得到Call、调用Call发起网络请求。我们分别来看看这三步Retrofit2都干了些啥。
## 创建接口对象
```
//Retrofit.class
public <T> T create(final Class<T> service) {
    Utils.validateServiceInterface(service);
    if (validateEagerly) {
        eagerlyValidateMethods(service);
    }
    return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
            new InvocationHandler() {
                private final Platform platform = Platform.get();

                @Override
                public Object invoke(Object proxy, Method method, @Nullable Object[] args)
                        throws Throwable {
                    // If the method is a method from Object then defer to normal invocation.
                    if (method.getDeclaringClass() == Object.class) {
                        return method.invoke(this, args);
                    }
                    //这里默认返回是false，所以不会执行
                    if (platform.isDefaultMethod(method)) {
                        return platform.invokeDefaultMethod(method, service, proxy, args);
                    }
                    //创建serviceMethod
                    ServiceMethod<Object, Object> serviceMethod =
                            (ServiceMethod<Object, Object>) loadServiceMethod(method);
                    //创建OkHttpCall，用于进行网络请求
                    OkHttpCall<Object> okHttpCall = new OkHttpCall<>(serviceMethod, args);
                    //返回经过适配器适配后的okHttpCall
                    return serviceMethod.adapt(okHttpCall);
                }
            });
}
```

可以看到Retrofit的create方法返回的是一个动态代理对象，当我们调用通过create方法生成的接口对象myService时，就会调用代理对象的invoke方法。在invoke方法中做了几件事：
（1）根据调用的具体方法Method(比如我们调用的getData方法)，生成ServiceMethod
（2）用生成的ServiceMethod和方法中的参数生成OkHttpCall用于后面调用OkHttp3请求网络
（3）将生成的OkHttpCall通过Call适配器适配以后返回，也就是将OkHttpCall转换成需要的Call类型，比如Retrofit2的Call，RxJava的Observable等，供我们调用。
## 调用接口得到Call
我们调用接口myService的getData方法时，会调用上面提到的动态代理对象的invoke方法，invoke方法会分别创建ServiceMethod、OkHttpCall，并将OkHttpCall适配返回我们需要的Call对象。下面我们来深入源码看看Retrofit是怎么做这些事儿的。
（1）首先我们看看是怎么创建ServiceMethod的。
```
//Retrofit.class
private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap<>();
ServiceMethod<?, ?> loadServiceMethod(Method method) {
    ServiceMethod<?, ?> result = serviceMethodCache.get(method);
    if (result != null) return result;

    synchronized (serviceMethodCache) {
        result = serviceMethodCache.get(method);
        if (result == null) {
            result = new ServiceMethod.Builder<>(this, method).build();
            serviceMethodCache.put(method, result);
        }
    }
    return result;
}
```
Retrofit中利用ConcurrentHashMap对ServiceMethod进行了缓存，创建ServiceMethod时会先去缓存中找，缓存中没有的话再调用ServiceMethod的Builder创建。因为Retrofit会为我们写的接口类中的每一个方法都创建一个ServiceMethod，所以ServiceMethod的数量会很多，利用缓存可以提高效率。
```
public ServiceMethod build() {
    //找到该方法所需要的CallAdapter
    callAdapter = createCallAdapter();
    responseType = callAdapter.responseType();
    ...
    //找到该方法需要的返回类型转换器
    responseConverter = createResponseConverter();

    //解析方法中的注解
    for (Annotation annotation : methodAnnotations) {
        parseMethodAnnotation(annotation);
    }

    ...
    //这里省略解析参数中的注解步骤
    ...

    return new ServiceMethod<>(this);
}
```
ServiceMethod的build方法中除了解析方法和参数的注解，最重要的就是确定该方法(每一个方法对应一个ServiceMethod)的CallAdapter和ResponseConverter。我们在构建Retrofit时可以添加多个CallAdapter和ResponseConverter，而这些CallAdapter和ResponseConverter都存放在Retrofit的对应的列表中，所以这里肯定需要去Retrofit的列表里找，我们来看看。
```
//ServiceMethod.class
private CallAdapter<T, R> createCallAdapter() {
    Type returnType = method.getGenericReturnType();
    if (Utils.hasUnresolvableType(returnType)) {
        throw methodError(
                "Method return type must not include a type variable or wildcard: %s", returnType);
    }
    if (returnType == void.class) {
        throw methodError("Service methods cannot return void.");
    }
    Annotation[] annotations = method.getAnnotations();
    try {

        //通过retrofit的callAdapter方法来查找对应的CallAdapter
        return (CallAdapter<T, R>) retrofit.callAdapter(returnType, annotations);
    } catch (RuntimeException e) { // Wide exception range because factories are user code.
        throw methodError(e, "Unable to create call adapter for %s", returnType);
    }
}
```
可以看到这里确实是通过retrofit来查找CallAdapter的，那我们去Retrofit的callAdapter方法方法看看
```
//Retrofit.class
public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
    return nextCallAdapter(null, returnType, annotations);
}

public CallAdapter<?, ?> nextCallAdapter(@Nullable CallAdapter.Factory skipPast, Type returnType,
                                         Annotation[] annotations) {
    ...

    int start = callAdapterFactories.indexOf(skipPast) + 1;
    for (int i = start, count = callAdapterFactories.size(); i < count; i++) {
        CallAdapter<?, ?> adapter = callAdapterFactories.get(i).get(returnType, annotations, this);
        if (adapter != null) {
            return adapter;
        }
    }

    ...
}
```
callAdapter方法中会遍历callAdapterFactories列表中的CallAdapterFactory，并调用其get方法，尝试获取CallAdapter，如果CallAdapter不为null，就说明是要找的CallAdapter。这里我们来简单看下默认的CallAdapterFactory的get方法。
从本文的开头的分析我们知道，默认的CallAdapterFactory是Platform的内部类Android返回的ExecutorCallAdapterFactory
```
final class ExecutorCallAdapterFactory extends CallAdapter.Factory {
    ...
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

        if (getRawType(returnType) != Call.class) {
            return null;
        }
        ...
    }

    ...
}
```
可以看到，ExecutorCallAdapterFactory的get方法首先会判断当前的返回类型是不是Call以及Call的子类，不是的话就返回null。所以这就是Retrofit从适配器列表中找到对应适配器的方法依据。比如我们再来看看RxJava的适配器：
```
//RxJavaCallAdapterFactory.class
@Override
public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {

    ...
    if (rawType != Observable.class && !isSingle && !isCompletable) {
      return null;
    }

    ...

    return new RxJavaCallAdapter(responseType, scheduler, isAsync, isResult, isBody, isSingle,
        false);
}
```
所以当我们接口需要的是Observable时，我们就需要给Retrofit设置RxJava的适配器，这样Retrofit在创建ServiceMethod时就能找到对应的RxJava适配器了。
（2）创建OkHttpCall
创建OkHttpCall比较简单，直接调用构造方法就行
```
OkHttpCall(ServiceMethod<T, ?> serviceMethod, @Nullable Object[] args) {
    this.serviceMethod = serviceMethod;
    this.args = args;
}
```
（3）返回接口需要的Call对象
从上面的分析中我们知道，是通过serviceMethod的adapt方法来返回目标Call对象的，那我们来看看serviceMethod的adapt方法
```
T adapt(Call<R> call) {
    return callAdapter.adapt(call);
}
```
可以看到调用的serviceMethod中的callAdapter的adapt方法，也就是在上面的创建ServiceMethod的过程中确定的CallAdapter的adapt方法。这里我们看看默认的CallAdapter的adapt方法，也就是ExecutorCallAdapterFactory的adapt方法
```
final class ExecutorCallAdapterFactory extends CallAdapter.Factory {

    ...
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != Call.class) {
            return null;
        }
        final Type responseType = Utils.getCallResponseType(returnType);
        return new CallAdapter<Object, Call<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public Call<Object> adapt(Call<Object> call) {
                return new ExecutorCallbackCall<>(callbackExecutor, call);
            }
        };
    }

    static final class ExecutorCallbackCall<T> implements Call<T> {
        final Executor callbackExecutor;
        final Call<T> delegate;

        ExecutorCallbackCall(Executor callbackExecutor, Call<T> delegate) {
            this.callbackExecutor = callbackExecutor;
            this.delegate = delegate;
        }

        @Override
        public void enqueue(final Callback<T> callback) {
            checkNotNull(callback, "callback == null");

            delegate.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, final Response<T> response) {
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (delegate.isCanceled()) {
                                // Emulate OkHttp's behavior of throwing/delivering an IOException on cancellation.
                                callback.onFailure(ExecutorCallbackCall.this, new IOException("Canceled"));
                            } else {
                                callback.onResponse(ExecutorCallbackCall.this, response);
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Call<T> call, final Throwable t) {
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailure(ExecutorCallbackCall.this, t);
                        }
                    });
                }
            });
        }

        @Override
        public Response<T> execute() throws IOException {
            return delegate.execute();
        }

        ...
    }
}
```
可以看到，ExecutorCallAdapterFactory的adapt方法返回的是ExecutorCallAdapterFactory的内部类ExecutorCallbackCall，ExecutorCallbackCall内部有2部分组成，一个是回调执行器callbackExecutor，这个是用于将请求结果回调到主线程的；另一是Call对象，这里对应的就是OkHttpCall，因为我们调用adapt方法传入的就是OkHttpCall。
所以到这里其实网络调用的前半部分流程就清楚了：

> 我们在调用我们的接口方法myService的getData方法时，实际上调用的是Retrofit为我们生成的代理类的invoke方法，invoke方法会创建ServiceMethod和OkHttpCall，ServiceMethod中保存着对应CallAdapter和ResponseConverter，然后会调用ServiceMethod中的adapt方法利用CallAdapter将OkHttpCall转换成我们需要的Call类型并返回给我们调用。当我们调用Call进行网络请求时实际上调用的就是OkHttpCall对应的方法。

上次我们分析到网络请求是通过OkHttpCall类来完成的，下面我们就来分析下OkHttpCall类。
```
final class OkHttpCall<T> implements Call<T> {

    ...
    @Override
    public void enqueue(final Callback<T> callback) {
        checkNotNull(callback, "callback == null");

        okhttp3.Call call;
        Throwable failure;

        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;

            call = rawCall;
            failure = creationFailure;
            if (call == null && failure == null) {
                try {
                    //调用createRawCall创建OkHttp3的Call
                    call = rawCall = createRawCall();
                } catch (Throwable t) {
                    throwIfFatal(t);
                    failure = creationFailure = t;
                }
            }
        }

        ...

        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse) {
                Response<T> response;
                try {
                    //解析返回的结果
                    response = parseResponse(rawResponse);
                } catch (Throwable e) {
                    callFailure(e);
                    return;
                }

                try {
                    callback.onResponse(OkHttpCall.this, response);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                callFailure(e);
            }

            private void callFailure(Throwable e) {
                try {
                    callback.onFailure(OkHttpCall.this, e);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

    ...
}
```
OkHttpCall的enqueue方法主要干了2件事，一个是创建OkHttp3的Call用于执行网络请求；另一个是解析返回的结果并回调。下面我们来看看创建OkHttp3的Call的过程
```
//OkHttpCall.class
private okhttp3.Call createRawCall() throws IOException {
    okhttp3.Call call = serviceMethod.toCall(args);
    if (call == null) {
        throw new NullPointerException("Call.Factory returned null.");
    }
    return call;
}
```
可以发现是通过serviceMethod的toCall方法来创建的
```
//ServiceMethod.class
okhttp3.Call toCall(@Nullable Object... args) throws IOException {
    RequestBuilder requestBuilder = new RequestBuilder(httpMethod, baseUrl, relativeUrl, headers,
            contentType, hasBody, isFormEncoded, isMultipart);

    ...
    for (int p = 0; p < argumentCount; p++) {
        handlers[p].apply(requestBuilder, args[p]);
    }
    //最后调用OkHttpClient的newCall方法返回Call
    return callFactory.newCall(requestBuilder.build());
}
```
ServiceMethod的toCall方法也是通过OkHttpClient的newCall方法来返回Call的。
在我们通过OkHttpClient请求得到结果后，我们还需要将返回的结果Response解析成我们接口需要的实体类型，这就需要用到我们在创建Retrofit时设置的ConverterFactory了，比如GsonConverterFactory。
```
//OkHttpCall.class
Response<T> parseResponse(okhttp3.Response rawResponse) throws IOException {
    ResponseBody rawBody = rawResponse.body();

    rawResponse = rawResponse.newBuilder()
            .body(new NoContentResponseBody(rawBody.contentType(), rawBody.contentLength()))
            .build();

    ...

    ExceptionCatchingRequestBody catchingBody = new ExceptionCatchingRequestBody(rawBody);
    try {
        //通过serviceMethod的toResponse方法解析
        T body = serviceMethod.toResponse(catchingBody);
        return Response.success(body, rawResponse);
    } catch (RuntimeException e) {

        catchingBody.throwIfCaught();
        throw e;
    }
}
```
OkHttpCall的parseResponse方法调用的是serviceMethod的toResponse方法来解析返回的结果。
```
//ServiceMethod.class
R toResponse(ResponseBody body) throws IOException {
    return responseConverter.convert(body);
}
```
在ServiceMethod中最后调用responseConverter的convert方法来转换返回的结果。这个responseConverter和上面分析的CallAdapter的确定过程一样，也是在ServiceMethod的build方法中，通过调用retrofit的requestBodyConverter方法遍历我们传入的ConverterFactory，直到找到合适的。
```
//Retrofit.class
public <T> Converter<T, RequestBody> requestBodyConverter(Type type,
                                                              Annotation[] parameterAnnotations, Annotation[] methodAnnotations) {
    return nextRequestBodyConverter(null, type, parameterAnnotations, methodAnnotations);
}

public <T> Converter<T, RequestBody> nextRequestBodyConverter(
        @Nullable Converter.Factory skipPast, Type type, Annotation[] parameterAnnotations,
        Annotation[] methodAnnotations) {
    ...

    int start = converterFactories.indexOf(skipPast) + 1;
    for (int i = start, count = converterFactories.size(); i < count; i++) {
        Converter.Factory factory = converterFactories.get(i);
        Converter<?, RequestBody> converter =
                factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, this);
        if (converter != null) {
            //noinspection unchecked
            return (Converter<T, RequestBody>) converter;
        }
    }

    ...
}
```
需要注意的是在创建Retrofit时默认添加了一个BuiltInConverters，这个是Retrofit为我们提供一个默认的responseConverter，它主要处理的是返回类型是ResponseBody和Void的情况。
```
final class BuiltInConverters extends Converter.Factory {
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (type == ResponseBody.class) {
            return Utils.isAnnotationPresent(annotations, Streaming.class)
                    ? StreamingResponseBodyConverter.INSTANCE
                    : BufferingResponseBodyConverter.INSTANCE;
        }
        if (type == Void.class) {
            return VoidResponseBodyConverter.INSTANCE;
        }
        return null;
    }
    ...
}
```
因为我们一般返回值类型都是具体的实体类型，所以我们需要添加自己的responseConverter，一般也就是GsonConverterFactory了。
至此，网络调用的后半部分流程也清楚了：

我们调用Call对象的enqueue方法发起异步请求时，实际上调用的是OkHttpCall对应的enqueue方法。OkHttpCall会先调用ServiceMethod类的toCall方法利用OkHttpClient的newCall方法创建OkHttp3的call对象，然后利用这个call对象执行具体的网络请求。在网络请求返回成功以后会调用ServiceMethod类的toResponse方法利用我们设置的responseConverter将返回结果转换成我们需要的类型，然后通过我们设置的回调或是默认的回调方法，将结果回调回主线程，从而完成整个请求过程。

总结
Retrofit2的网络调用的整个流程我们已经分析完了。通过这次分析，我们可以看到Retrofit2中最主要的就是3个类：Retrofit、ServiceMethod和OkHttpCall。这三个类指责明确，相互配合共同完成整个网络调用的流程。
（1）Retrofit负责供外部初始化和定制，保存CallAdapter的列表和ResponseConverterFactory列表。
（2）ServiceMethod对应每一个接口方法的信息，包括解析注解和参数等，同时它也是连接Retrofit和OkHttpCall的桥梁。ServiceMethod中保存着当前接口对应方法所需要的CallAdapter和ResponseConverter。利用CallAdapter将OkHttpCall转换成接口需要的类型，供接口调用。利用toResponse方法让OkHttpCall调用ResponseConverter解析网络请求返回的结果。
（3）OkHttpCall则是用来执行具体网络请求。Retrofit2没有直接使用OkHttp3的Call接口，而是有自己的Call接口。在OkHttpCall内部通过组合的方法持有OkHttp3的Call接口，并通过ServiceMethod的toCall方法得到OkHttp3的call来进行网络请求，减少对OkHttp3的耦合。

作者：xxq2dream
链接：https://www.jianshu.com/p/7aef9ccc613e
来源：简书
简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。