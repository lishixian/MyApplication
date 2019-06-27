/**
 * 1.是哪个对象在什么时候创建了子线程，是一种怎样的方式创建的？
 *在NewThreadScheduler的createWorker()方法中，通过其构建好的线程工厂，在Worker实现类的构造函数中创建了一个ScheduledExecutorService的实例，是通过SchedulerPoolFactory创建的。
 *
 *
 * 2.子线程又是如何启动的？
 * 3.上游事件是怎么跑到子线程里执行的？
 * 直接把订阅方法放在了一个Runnable中去执行，这样就一旦这个Runnable在某个子线程执行，那么上游所有事件只能在这个子线程中执行了。
 *
 * 4.多次用 subscribeOn 指定上游线程为什么只有第一次有效 ?
 *
 *
 *
 *
 *
 */
public class RxJava2 线程切换原理{

    //例子：
    Disposable disposable = RetrofitHelper.calendarApi()
            .getCardServices("")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new ErrorHandledConsumer<List<CardItem>>() {
                @Override
                public void onSuccess(List<CardItem> cardItems) {
                    getV().showServiceCard(cardItems);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    throwable.printStackTrace();
                }
            });
    addDisposable(disposable);


//subscribeOn
    //`subscribeOn` 操作符将之前产生的 `Observable` 和 传入的 `Scheduler`封装成 `ObservableSubscribeOn` 。
public final Observable<T> subscribeOn(Scheduler scheduler) {
    ObjectHelper.requireNonNull(scheduler, "scheduler is null");
    return RxJavaPlugins.onAssembly(new ObservableSubscribeOn<T>(this, scheduler));
}
//ObservableSubscribeOn.subscribeActual
    public void subscribeActual(final Observer<? super T> s) {
        final SubscribeOnObserver<T> parent = new SubscribeOnObserver<T>(s);

        s.onSubscribe(parent);

        parent.setDisposable(scheduler.scheduleDirect(new SubscribeTask(parent)));
    }
    final class SubscribeTask implements Runnable {
        private final SubscribeOnObserver<T> parent;

        SubscribeTask(SubscribeOnObserver<T> parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            source.subscribe(parent);
        }
    }


//observeOn
    //将之前产生的 Observable 和 传入的 Scheduler封装成 ObservableObserveOn
public final Observable<T> observeOn(Scheduler scheduler) {
    return observeOn(scheduler, false, bufferSize());
}


//subscribe
public final Disposable subscribe(Consumer<? super T> onNext, Consumer<? super Throwable> onError,
                                  Action onComplete, Consumer<? super Disposable> onSubscribe) {
    LambdaObserver<T> ls = new LambdaObserver<T>(onNext, onError, onComplete, onSubscribe);
    subscribe(ls);
    return ls;
}
    public final void subscribe(Observer<? super T> observer) {
        ObjectHelper.requireNonNull(observer, "observer is null");
            observer = RxJavaPlugins.onSubscribe(this, observer);
            ObjectHelper.requireNonNull(observer, "Plugin returned null Observer");
            subscribeActual(observer);
    }


    ObservableObserveOn.subscribeActual()
    protected void subscribeActual(Observer<? super T> observer) {
        if (scheduler instanceof TrampolineScheduler) {
            source.subscribe(observer);
        } else {
            Scheduler.Worker w = scheduler.createWorker();

            source.subscribe(new ObserveOnObserver<T>(observer, w, delayError, bufferSize));
        }
    }

}



