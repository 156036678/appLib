package com.xiay.applib.util.rxjava.bean;

/**
 * * 在主线程中执行的任务
 * Created by Xiay on 2016/12/25.
 */
public abstract class RxUITask<T> {

    public abstract void doInUIThread();

    public RxUITask(T t) {
        setValue(t);
    }

    public RxUITask() {

    }

    private T t;

    public T getValue() {
        return t;
    }

    public void setValue(T t) {
        this.t = t;
    }
}