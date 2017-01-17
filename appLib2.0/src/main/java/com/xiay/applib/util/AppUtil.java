package com.xiay.applib.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.xiay.applib.AppActivity;
import com.xiay.applib.bean.AppUpdateInfo;
import com.xiay.applib.listener.AppUpdateListener;
import com.xiay.applib.listener.HttpCallBack;
import com.xiay.applib.service.AppUpdateService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.xiay.ui.Toast;
import cn.xiay.util.AppActivityManager;
import cn.xiay.util.SPUtil;

/**
 */
public class AppUtil {
    private static final String ANDROID_RESOURCE = "android.resource://";
    private static final String FOREWARD_SLASH = "/";
    public  static boolean isForceUpdate;
    public  static int versionCode;
    /**
     * 检查更新
     * @param appActivity
     * @param loadingMsg 请求的网络显示的提示消息
     * @param updateListener  返回 null没更新，否者有更新
     */
    public static void checkUpdate(AppActivity appActivity, String loadingMsg, final AppUpdateListener updateListener){
        isForceUpdate=false;
        checkUpdate(appActivity,loadingMsg,appActivity.initParams("activeCount"),updateListener);
    }
    public static void checkUpdate(AppActivity appActivity, String loadingMsg, Map<String, String> params,final AppUpdateListener updateListener){
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
    public static void  startAppUpdate(Context ctx,AppUpdateInfo appInfo){
        Intent intent = new Intent(ctx,AppUpdateService.class);
        intent.putExtra("appInfo",appInfo);
        ctx.startService(intent);
    }
    /**
     * 获取版本号
     * @param ctx
     * @return
     */
    public static int getVersionCode(Context ctx){
        if (versionCode==0){
            // 获取packagemanager的实例
            PackageManager packageManager = ctx.getPackageManager();
            try {
                // getPackageName()是你当前类的包名，0代表是获取版本信息
                PackageInfo packInfo = null;
                packInfo = packageManager.getPackageInfo(ctx.getPackageName(),0);
                versionCode=packInfo.versionCode;
                return versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            return versionCode;
        }
        return 0;
    }
    /**
     * 获取版本号
     * @param ctx
     * @return
     */
    public static String getVersionName(Context ctx){
        // 获取packagemanager的实例
        PackageManager packageManager = ctx.getPackageManager();
        try {
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = null;
            packInfo = packageManager.getPackageInfo(ctx.getPackageName(),0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }
    /**
     * 安装apk
     *
     */
    public static  void installApk(Context ctx,String filePath ) {
        if (filePath!=null){
            File apkfile = new File(filePath);
            if (!apkfile.exists()) {
                return;
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
            ctx.startActivity(i);
            if (AppUtil.isForceUpdate){
                AppActivityManager.getInstance().removeAllActivity();
                Toast.show("请安装最新版");
            }
        }

    }
    /**
     * 安装统计
     * @param ctx
     */
    public static void installCount(Context ctx){
        AppActivity appActivity=(AppActivity)ctx;
        installCount(ctx,appActivity.initParams("installCount"));
    }
    /**
     * 安装统计
     * @param ctx
     */
    public static void installCount(Context ctx,Map<String, String> params){
        boolean isInstall= SPUtil.getBoolean("isAppIntall");
        if (!isInstall){
            AppActivity appActivity=(AppActivity)ctx;
            appActivity.sendStringPost(params,new HttpCallBack<String>(){
                @Override
                public void onSucceed(int what, String response) {
                    try {
                        JSONObject jObj=new JSONObject(response);
                        if (jObj.getInt("status")==1)
                            SPUtil.saveBoolean("isAppIntall",true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    /** 需要权限:android.permission.GET_TASKS
     *应用是否在后台运行
     * @param context
     * @return
     */
    public static boolean isAppBroughtToBackground(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
    /**
     * 判断应用是否已经启动
     * @param context 一个context
     * @param packageName 要判断应用的包名
     * @return boolean
     */
    public static boolean isAppAlive(Context context, String packageName){
        ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfos.size(); i++){
            if(processInfos.get(i).processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }

    /**从资源 id 转换成 Uri*/
    public static Uri resIdToUri(Context context, int resourceId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FOREWARD_SLASH + resourceId);
    }

    /**
     * 获取进程号对应的进程名
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

        /**
         * data dir, such as /data/data/包名/appdata
         * @param context
         * @return /data/data/包名/appdata
         */
    public static File getDataDirectory(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        if (applicationInfo == null) {
            // Looks like running on a test Context, so just return without patching.
            return null;
        }

        File file=  new File(applicationInfo.dataDir,"appdata");
        if (!file.exists())
            file.mkdirs();
        return file;
    }
}
