package com.xiay.applib.util.permission;

import android.Manifest;
import android.support.v4.app.Fragment;

import com.xiay.applib.AppActivity;

/**
 * 获取拨打电话权限
 * */
public  class PermissionCallPhone extends PermissionBase {
    public static final   int PERMISSION_CODE=1003;
    public PermissionCallPhone(AppActivity activity){
        super(activity);
        sendRequest();
    }
    public PermissionCallPhone(Fragment fragment){
        super(fragment);
        sendRequest();
    }
    public static String[] getPermissions(){
        return new String[]{Manifest.permission.CALL_PHONE};
    }
    private void sendRequest(){
        PERMISSION_NICE_NAME="拨打电话";
        requestPermission(PERMISSION_CODE,getPermissions());
    }
    public static void   showNoPermissionDialog(AppActivity act){
        PermissionBase.showNoPermissionDialog(act,PERMISSION_NICE_NAME,getPermissions());
    }
}
