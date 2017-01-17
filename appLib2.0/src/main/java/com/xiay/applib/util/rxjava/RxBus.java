package com.xiay.applib.util.rxjava;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.Subject;

/**
 * Created by Xiay on 2016/12/25.
 * 功能描述：类似android中的广播。
 * 使用方法(推荐使用RxManager来管理 具体用法参考{@link com.xiay.applib.util.rxjava.RxManager}  )
 * Observable<String> ob ;
 * //创建被观察者
 * ob = RxBus.get().register( tag , String.class ) ;
 * //订阅观察事件
 * ob.subscribe(new Action1<String>() {
 * @Override public void call(String s) {
 * System.out.println( "fff-- " + s  );
 * }
 * }) ;
 *
 * //发送内容
 * RxBus.get().post(  tag , "我是内容" );
 *
 * //在Activity销毁的时候取消订阅,否者会造成内存溢出且下次call方法会执行两次
 * @Override protected void onDestroy() {
 * super.onDestroy();
 * //取消订阅
 * RxBus.get().unregister( tag , ob );
 * }
 */

public class RxBus {
    private static RxBus instance;

    public static RxBus get() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }

    private RxBus() {
    }

    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public <T> Observable<T> register(@NonNull String tag, @NonNull Class<T> clazz) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        //     if (DEBUG) Log.d(TAG, "[register]subjectMapper: " + subjectMapper);
        return subject;
    }
    /**
     * rxBus的注册方法
     * @param tag
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> Observable<T> register(@NonNull Object tag) {
        //可以为同一个tag 注册多个
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<>();
            subjectMapper.put(tag, subjectList);
        }

        Subject<T, T> subject;
        //使用replaysubject 可以先发送事件,在订阅
        subjectList.add(subject = ReplaySubject.create());
        return subject;
    }

    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            if (observable != null && subjects.contains(observable)) {
                subjects.remove(observable);
            }
            if (isEmpty(subjects)) {
                subjectMapper.remove(tag);
            }
        }

        //    if (DEBUG) Log.d(TAG, "[unregister]subjectMapper: " + subjectMapper);
    }

    public void post(@NonNull Object tag) {
        post(tag, null);
    }

    @SuppressWarnings("unchecked")
    public void post(@NonNull Object tag, @NonNull Object content) {
        List<Subject> subjectList = subjectMapper.get(tag);

        if (!isEmpty(subjectList)) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
            }
        }
        //      if (DEBUG) Log.d(TAG, "[send]subjectMapper: " + subjectMapper);
    }

    private boolean isEmpty(Collection collection) {
        return null == collection || collection.isEmpty();
    }
}
