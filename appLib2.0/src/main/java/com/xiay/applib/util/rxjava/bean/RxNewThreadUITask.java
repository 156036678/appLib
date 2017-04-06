package com.xiay.applib.util.rxjava.bean;


/**
 * 通用的Rx执行任务 
 * Created by Xiay on 2016/12/25.
 */
public abstract class RxNewThreadUITask<T> {
    public RxNewThreadUITask(T t) {
        setValue(t);
    }

    public RxNewThreadUITask() {
    }

    private T t;

    public T getValue() {
        return t;
    }

    public void setValue(T t) {
        this.t = t;
    }


    public abstract void doInUIThread();
    public abstract void doInNewThread();


}  
