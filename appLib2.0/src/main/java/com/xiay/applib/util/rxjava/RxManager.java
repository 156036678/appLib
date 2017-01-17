package com.xiay.applib.util.rxjava;

import android.support.v4.util.ArrayMap;

import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
/**
 * 使用方法
 * //当通过rxManager.post("tag","内容");或者 RxBus.get().post("tag","内容");发送数据时，会执行此方法，类似广播。
 * rxManager.add(Action.ON_USER_BANK_CARD_CHANGE,new Action1<String>() {
 * @Override public void call(String s) {
 * Log.i("xxxx call="+s.toString());
 * }
 * });
 * //添加一个被观察者及观察者，每隔1秒执行一次call()
 * rxManager.add(Observable.interval(1, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
 * @Override public void call(Long aLong) {
 * Log.i("xxxx call"+Thread.currentThread().getName());
 * }
 * }));
 * Created by Xiay on 2016/12/25.
 */

public class RxManager {
    public RxBus mRxBus;
    //管理rxbus订阅
    private ArrayMap<String, Observable<?>> mObservables;
    /*管理Observables 和 Subscribers订阅*/
    private CompositeSubscription mCompositeSubscription;
    /**
     * RxBus订阅
     *
     * @param eventName
     * @param action1
     */
    public <T> void add(String eventName, Action1<T> action1) {
        if (mObservables == null||mRxBus==null) {
            mObservables = new ArrayMap<>();
            mRxBus= RxBus.get();// //拿到rxBus
            mCompositeSubscription = new CompositeSubscription();
        }
        Observable<T> mObservable = mRxBus.register(eventName);
        mObservables.put(eventName, mObservable);
        /*订阅管理*/
        mCompositeSubscription.add(mObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(action1, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        //处理错误信息
                        throwable.printStackTrace();
                    }
                }));
    }

    /**
     * 单纯的Observables 和 Subscribers管理
     *
     * @param m
     */
    public void add(Subscription m) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        /*订阅管理*/
        mCompositeSubscription.add(m);
    }

    /**
     * 单个presenter生命周期结束，取消订阅和所有rxbus观察
     */
    public void clear() {
        if (mCompositeSubscription != null)
            mCompositeSubscription.unsubscribe();// 取消所有订阅
        if (mObservables!=null){
            for (Map.Entry<String, Observable<?>> entry : mObservables.entrySet()) {
                mRxBus.unregister(entry.getKey(), entry.getValue());// 移除rxbus观察
            }
            mObservables.clear();
        }
        mObservables = null;
        mRxBus = null;
        mCompositeSubscription = null;
    }
    //发送rxbus
    public void post(Object tag, Object content) {
        if(mRxBus==null)
            mRxBus= RxBus.get();// //拿到rxBus
        mRxBus.post(tag, content);
    }
    //发送rxbus
    public void post(Object tag) {
        if(mRxBus==null)
            mRxBus= RxBus.get();// //拿到rxBus
        mRxBus.post(tag);
    }
}
