package com.xiay.applib;

import com.xiay.applib.service.AppInitializeService;

import cn.xiay.App;

public class AppApplication extends App {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    public void initData() {
        AppInitializeService.start(this);
    }

    /**数据初始化完成之后必须调用此方法
     public void initFinish(){
     RxBus.get().post(AppAction.ON_APPLICATION_INIT_FINISH);
     }*/
}