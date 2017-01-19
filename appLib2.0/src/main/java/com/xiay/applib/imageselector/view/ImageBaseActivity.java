package com.xiay.applib.imageselector.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.xiay.applib.util.permission.PermissionBase;
import com.xiay.applib.util.permission.PermissionSDAndCamera;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;


public abstract class ImageBaseActivity extends AppCompatActivity implements PermissionListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults,this);
    }
    @Override
    public void onSucceed(int requestCode) {
        onPermissionSucceed(requestCode);
    }
    @Override
    public void onFailed(int requestCode) {
        PermissionBase.showNoPermissionDialog(this, PermissionSDAndCamera.PERMISSION_NICE_NAME, PermissionSDAndCamera.getPermissions());
    }
    public abstract void onPermissionSucceed(int requestCode);
}
