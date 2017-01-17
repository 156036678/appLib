package com.xiay.applib.bean;

public class BaseBean {
    public int status;
    public String msg;

    public BaseBean() {
    }

    public  boolean isSuccess(){
        return  status==1;
    }
}
