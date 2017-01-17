package com.xiay.applib.listener;

import com.nohttp.extra.HttpListener;
import com.nohttp.rest.Response;


public abstract class HttpCallBack<T> implements HttpListener<T> {
    @Override
    public abstract void onSucceed(int what, T responseData);

    @Override
    public void onFailed(int what, Response<T> responseData) {
        onFailed(what,responseData.get());
    }
    public void onFailed(int what, T responseData) {
    }
}
