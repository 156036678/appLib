package com.xiay.applib.util.permission;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.Fragment;

import com.xiay.applib.AppActivity;

/**
 * 获取SD卡权限
 * */
public  class PermissionSD extends PermissionBase {
    public static final   int PERMISSION_CODE=1000;
    public PermissionSD(Activity activity){
        super(activity);
        sendRequest();
    }
    public PermissionSD(Fragment fragment){
        super(fragment);
        sendRequest();
    }
    public static String[] getPermissions(){
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
    }
    private void sendRequest(){
        PERMISSION_NICE_NAME="SD";
        requestPermission(PERMISSION_CODE,getPermissions());
    }
    public static void   showNoPermissionDialog(AppActivity act){
        PermissionBase.showNoPermissionDialog(act,PERMISSION_NICE_NAME,getPermissions());
    }

}
