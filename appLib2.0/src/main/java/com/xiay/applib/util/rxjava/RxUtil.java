package com.xiay.applib.util.rxjava;

import com.xiay.applib.util.rxjava.bean.RxIOTask;
import com.xiay.applib.util.rxjava.bean.MyOnSubscribe;
import com.xiay.applib.util.rxjava.bean.RxTask;
import com.xiay.applib.util.rxjava.bean.RxUITask;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 *  Created by Xiay on 2016/12/25.
 *
 * 使用方法

 */

public class RxUtil {
    /**
     * 在ui线程中工作 
     *
     * @param uiTask
     */
    public static <T> void doInUIThread(RxUITask<T> uiTask) {
        Observable.just(uiTask)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxUITask<T>>() {
                    @Override
                    public void call(RxUITask<T> uitask) {
                        uitask.doInUIThread();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /*** 在IO线程中执行任务 * * @param <T> */
    public static <T> void doInIOThread(RxIOTask<T> rxIoTask) {
        Observable.just(rxIoTask)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<RxIOTask<T>>() {
                    @Override
                    public void call(RxIOTask<T> rxIoTask) {
                        rxIoTask.doInIOThread();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    /**
     * 执行Rx通用任务 (IO线程中执行耗时操作 执行完成调用UI线程中的方法)
     * @param t
     */
    public static <T> void executeRxTask(RxTask<T> t) {
       MyOnSubscribe<RxTask<T>> onsubscribe = new  MyOnSubscribe<RxTask<T>>(t) {
            @Override
            public void call(Subscriber<? super RxTask<T>> subscriber) {
                getT().doInIOThread();
                subscriber.onNext(getT());
                subscriber.onCompleted();
            }
        };
        Observable.create(onsubscribe)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RxTask<T>>() {
                    @Override
                    public void call(RxTask<T> t) {
                        t.doInUIThread();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

    }
}
