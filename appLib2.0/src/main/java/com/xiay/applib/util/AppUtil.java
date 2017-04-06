package com.xiay.applib.util;

import android.content.Context;
import android.content.Intent;

import com.xiay.applib.AppActivity;
import com.xiay.applib.bean.AppUpdateInfo;
import com.xiay.applib.listener.AppUpdateListener;
import com.xiay.applib.listener.HttpCallBack;
import com.xiay.applib.service.AppUpdateService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import cn.xiay.ui.Toast;
import cn.xiay.util.AppActivityManager;
import cn.xiay.util.AppHelper;
import cn.xiay.util.SPUtil;

/**
 */
public class AppUtil {

    public  static boolean isForceUpdate;
    private static class MyObjectHandler {
        private  static AppUtil singleOne = new AppUtil();
    }

    private AppUtil() {
    }

    public  static  AppUtil getInstance() {
        return AppUtil.MyObjectHandler.singleOne;
    }
    /**
     * 检查更新
     * @param appActivity
     * @param loadingMsg 请求的网络显示的提示消息
     * @param updateListener  返回 null没更新，否者有更新
     */
    public  void checkUpdate(AppActivity appActivity, String loadingMsg, final AppUpdateListener updateListener){
        isForceUpdate=false;
        checkUpdate(appActivity,loadingMsg,appActivity.initParams("activeCount"),updateListener);
    }
    public  void checkUpdate(AppActivity appActivity, String loadingMsg, Map<String, String> params,final AppUpdateListener updateListener){
        isForceUpdate=false;
        appActivity.sendJsonObjectPost(params,loadingMsg,new HttpCallBack<JSONObject>(){
            @Override
            public void onSucceed(int what, JSONObject jObj) {
                try {
                    if (jObj.getInt("status")==2){
                        updateListener.updateListener(jObj.getString("data"),false);
                    }else if (jObj.getInt("status")==4){//强制更新
                        isForceUpdate=true;
                        updateListener.updateListener(jObj.getString("data"),true);
                    }else {
                        updateListener.updateListener(null,false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 开始App更新
     * @param ctx
     * @param appInfo 更新时候的信息
     */
    public  void  startAppUpdate(Context ctx,AppUpdateInfo appInfo){
        Intent intent = new Intent(ctx,AppUpdateService.class);
        intent.putExtra("appInfo",appInfo);
        ctx.startService(intent);
    }


    /**
     * 安装apk
     *
     */
    public   void installApk(Context ctx,String filePath ) {
        if (AppHelper.getInstance().installApk(ctx,filePath)){
            if (isForceUpdate){
                AppActivityManager.getInstance().removeAllActivity();
                Toast.show("请安装最新版");
            }
        }

    }
    /**
     * 安装统计
     * @param ctx
     */
    public  void installCount(Context ctx){
        AppActivity appActivity=(AppActivity)ctx;
        installCount(ctx,appActivity.initParams("installCount"));
    }
    /**
     * 安装统计
     * @param ctx
     */
    public  void installCount(Context ctx,Map<String, String> params){
        boolean isInstall= SPUtil.getInstance().getBoolean("isAppIntall");
        if (!isInstall){
            AppActivity appActivity=(AppActivity)ctx;
            appActivity.sendStringPost(params,new HttpCallBack<String>(){
                @Override
                public void onSucceed(int what, String response) {
                    try {
                        JSONObject jObj=new JSONObject(response);
                        if (jObj.getInt("status")==1)
                            SPUtil.getInstance().saveBoolean("isAppIntall",true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


}
