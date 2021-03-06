package com.example.myapplication;


import android.util.Log;

import java.util.List;

import base.BPresent;
import data.CardItem;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import network.ErrorHandledConsumer;
import network.RetrofitHelper;

public class MainPresenter extends BPresent<MainActivity> {

    private static final String TAG = "MainPresenter";

    public void getCardService() {

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
    }

    public void test(){
        // create()构建了一个包含ObservableOnSubscribe的ObservableCreate对象，该对象继承Observable。
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("1");
                emitter.onNext("2");
                emitter.onNext("3");
                emitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())//构建了一个ObservableSubscribeOn对象。
                //subscribeActual创建一个SubscribeOnObserver对象,
                .observeOn(AndroidSchedulers.mainThread())//构建了一个ObservableObserveOn对象。
                //subscribeActual生成ObserveOnObserver,用ObserveOnObserver订阅ObservableSubscribeOn对象
                .subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
            }
            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext : " + s);
            }
            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError : " + e.toString());
            }
            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete");
            }
        });
    }
}
