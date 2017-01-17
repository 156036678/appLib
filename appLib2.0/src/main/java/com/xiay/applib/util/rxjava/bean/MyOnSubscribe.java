package com.xiay.applib.util.rxjava.bean;

import rx.Observable;

/**
 * Created by Xiay on 2016/12/25.
 */

public abstract class MyOnSubscribe<C> implements Observable.OnSubscribe<C> {
    private C c;

    public MyOnSubscribe(C c) {
        setT(c);
    }

    public C getT() {
        return c;
    }

    public void setT(C c) {
        this.c = c;
    }


}