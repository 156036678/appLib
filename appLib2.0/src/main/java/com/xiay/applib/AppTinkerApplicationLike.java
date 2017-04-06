package com.xiay.applib;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.support.v4.util.ArrayMap;

import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.tinker.loader.app.DefaultApplicationLike;

import cn.xiay.util.log.Log;

/**
 * Created by Xiay on 2017/3/30.
 */

public class AppTinkerApplicationLike extends DefaultApplicationLike {
    public static Context context;
    public static int versionCode;
    public static ArrayMap<String, Object> data=new ArrayMap();
    public AppTinkerApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplication().getApplicationContext();
        super.onCreate();
    }

    protected void initCrashReport(){
        CrashReport.initCrashReport(context);
    }
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 安装tinker
        // TinkerManager.installTinker(this); 替换成下面Bugly提供的方法
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }
    public  void initData(){}
    /**计算屏幕宽高,通过Device类获取*/
    public void setIsDebug(boolean isDebug){
        AppApplication.isDebug=isDebug;
        if (isDebug){
            Log.isPrint=true;
        }
    }
}
