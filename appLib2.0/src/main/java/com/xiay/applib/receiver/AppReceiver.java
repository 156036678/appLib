package com.xiay.applib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xiay.applib.util.AppUtil;

import cn.xiay.ui.Toast;
import cn.xiay.util.AppActivityManager;
import cn.xiay.util.AppHelper;

public class AppReceiver extends BroadcastReceiver {
    public static String INSTALL ="install";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (INSTALL.equals(intent.getAction())){
            AppHelper.getInstance().installApk(context,intent.getStringExtra("filePath"));
            if (AppUtil.isForceUpdate){
                AppActivityManager.getInstance().removeAllActivity();
                Toast.show("请安装最新版");
            }
        }
    }
}
