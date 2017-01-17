package com.xiay.applib.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.xiay.applib.AppActivity;
import com.xiay.applib.bean.AppUpdateInfo;
import com.xiay.applib.listener.AppNetListener;
import com.xiay.applib.listener.AppUpdateListener;
import com.xiay.applib.ui.dialog.AppUpdateDialog;
import com.xiay.applib.util.AppUtil;

import java.util.ArrayList;
import java.util.List;

import cn.xiay.dialog.ClickListener;
import cn.xiay.ui.Toast;

/**
 * Created by Xiay on 2016/4/27.
 */
public class AppNetBroadcastReceiver extends BroadcastReceiver {
    public static List<AppNetListener> appNetListener=new ArrayList<>();
    @Override
    public void onReceive(final Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo==null){
            Toast.show("网络已断开");
          //  appNetListener.onNetChangeListener(false);
        }else {
            Toast.show("网络已连接");
        }
    }
}
