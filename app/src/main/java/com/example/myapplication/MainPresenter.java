package com.example.myapplication;


import java.util.List;

import base.BPresent;
import data.CardItem;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import network.ErrorHandledConsumer;
import network.RetrofitHelper;

public class MainPresenter extends BPresent<MainActivity> {

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
}
