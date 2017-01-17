package com.xiay.applib.util.rxjava.bean;

/**
 * 在IO线程中执行的任务
 * Created by Xiay on 2016/12/25.
 */

public abstract class RxIOTask<T> {
    private T t;

    public RxIOTask() {

    }

    public T getValue() {
        return t;
    }

    public void setValue(T t) {
        this.t = t;
    }


    public RxIOTask(T t) {
        setValue(t);
    }


    public abstract void doInIOThread();
}
