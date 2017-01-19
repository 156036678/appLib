package com.xiay.applib.util.permission;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.Fragment;

import com.xiay.applib.AppActivity;

/**
 * 获取SD卡和相机权限
 * */
public  class PermissionSDAndCamera extends PermissionBase {
    public static final   int PERMISSION_CODE=1002;
    public PermissionSDAndCamera(Activity activity){
        super(activity);
        sendRequest();
    }
    public PermissionSDAndCamera(Fragment fragment){
        super(fragment);
        sendRequest();
    }
    public static String[] getPermissions(){
        return new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA};
    }
    private void sendRequest(){
        PERMISSION_NICE_NAME="SD和相机";
        requestPermission(PERMISSION_CODE,getPermissions());
    }
    public static void   showNoPermissionDialog(AppActivity act){
        PermissionBase.showNoPermissionDialog(act,PERMISSION_NICE_NAME,getPermissions());
    }
}
