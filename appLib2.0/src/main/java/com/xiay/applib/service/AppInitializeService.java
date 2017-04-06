package com.xiay.applib.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.nohttp.Config;
import com.nohttp.NoHttp;
import com.nohttp.cache.DBCacheStore;
import com.nohttp.cookie.DBCookieStore;
import com.nohttp4okhttp.OkHttpNetworkExecutor;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiay.applib.util.rxjava.RxUtil;
import com.xiay.applib.util.rxjava.bean.RxUITask;
import cn.xiay.ui.Toast;
import cn.xiay.util.autolayout.config.AutoLayoutConifg;

import static cn.xiay.App.isDebug;

/**
 * Created by Xiay on 2017/4/2.
 */

public class AppInitializeService extends IntentService {
    private static final String ACTION_INIT_WHEN_APP_CREATE = "ACTION_INIT_WHEN_APP_CREATE";

    public AppInitializeService() {
        super("AppInitializeService");
    }

    public static void start(Context context,Intent intent) {
        startWithIntent(context,intent);
    }

    private static void startWithIntent(Context context, Intent intent) {
        intent.setAction(ACTION_INIT_WHEN_APP_CREATE);
        context.startService(intent);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AppInitializeService.class);
        startWithIntent(context, intent);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_INIT_WHEN_APP_CREATE.equals(action)) {
                performInit();
            }
        }
    }
    private void performInit() {
        AutoLayoutConifg.getInstance().init(getApplicationContext());
        initHttp();
        initCrashReport();
        initDataInNewThread();
        RxUtil.doInUIThread(new RxUITask() {
            @Override
            public void doInUIThread() {
                Toast.init(getApplicationContext());
                initDataInUIThread();
            }
        });
    }
    protected void initCrashReport() {
        if (!isDebug)
            CrashReport.initCrashReport(getApplicationContext(), "c5b62410e5", isDebug);
    }
    protected void initHttp() {
        //NoHttp.initialize(AppTinkerApplication.this);
        // 如果你需要自定义配置：
        NoHttp.init( new Config()
                // 设置全局连接超时时间，单位毫秒，默认10s。
                .setConnectTimeout(15 * 1000)
                // 设置全局服务器响应超时时间，单位毫秒，默认10s。
                .setReadTimeout(15 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .setCacheStore(new DBCacheStore(getApplicationContext()).setEnable(true) // 如果不使用缓存，设置false禁用。
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现。
                .setCookieStore(new DBCookieStore(getApplicationContext()).setEnable(false) // 如果不维护cookie，设置false禁用。
                )
                // 配置网络层，默认使用URLConnection，如果想用OkHttp：OkHttpNetworkExecutor。
                .setNetworkExecutor(new OkHttpNetworkExecutor())
        );
    }
    public void initDataInNewThread(){}
    public void initDataInUIThread(){}
}
