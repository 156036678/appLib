package com.xiay.applib.bean;

import android.databinding.BaseObservable;

public class BaseBean extends BaseObservable {
    public int status;
    public String msg;

    public BaseBean() {
    }

    public  boolean isSuccess(){
        return  status==1;
    }
}
