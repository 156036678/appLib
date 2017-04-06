package com.xiay.applib.util.rxjava.bean;

/**
 * 在IO线程中执行的任务
 * Created by Xiay on 2016/12/25.
 */

public abstract class RxNewThreadTask<T> {
    private T t;

    public RxNewThreadTask() {

    }

    public T getValue() {
        return t;
    }

    public void setValue(T t) {
        this.t = t;
    }


    public RxNewThreadTask(T t) {
        setValue(t);
    }


    public abstract void doInNewThread();
}
