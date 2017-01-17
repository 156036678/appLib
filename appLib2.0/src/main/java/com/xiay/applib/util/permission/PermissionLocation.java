package com.xiay.applib.util.permission;

import android.Manifest;
import android.support.v4.app.Fragment;

import com.xiay.applib.AppActivity;

/**
 * 获取定位权限
 * */
public  class PermissionLocation extends PermissionBase {
    public static final   int PERMISSION_CODE=1001;
    public PermissionLocation(AppActivity activity){
        super(activity);
        sendRequest();
    }
    public PermissionLocation(Fragment fragment){
        super(fragment);
        sendRequest();
    }
    public static String[] getPermissions(){
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    }
    private void sendRequest(){
        PERMISSION_NICE_NAME="定位";
        requestPermission(PERMISSION_CODE,getPermissions());
    }
    public static void   showNoPermissionDialog(AppActivity act){
        PermissionBase.showNoPermissionDialog(act,PERMISSION_NICE_NAME,getPermissions());
    }
}
