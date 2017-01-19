package com.xiay.applib.util.permission;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.xiay.applib.AppActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.ArrayList;


public abstract class PermissionBase {

    public Activity act;
    public Fragment fragment;
    RationaleListener listener;
    public static String PERMISSION_NAME;
    public static String PERMISSION_NICE_NAME;

    public PermissionBase(Activity act) {
        this.act = act;
    }

    public PermissionBase(Fragment fragment) {
        this.fragment = fragment;
    }


    public void setListener(RationaleListener listener) {
        this.listener = listener;
    }

    /**
     * 申请SD卡权限，单个的。
     */
    public void requestPermission(int requestCode, String... permissions) {
        Permission permission;
        if (act != null) {
            permission = AndPermission.with(act);
        } else {
            permission = AndPermission.with(fragment);
        }
        permission.requestCode(requestCode)
                .permission(permissions)
                .rationale(rationaleListener)
                .send();
    }

    public static void showNoPermissionDialog(final Activity act, String permissionName, String... permissions) {
        for (int i = 0; i < permissions.length; i++) {
            if (permissions[i]!=null){
                if (AndPermission.getShouldShowRationalePermissions(act, permissions)) {
                    Toast.makeText(act, "获取" + permissionName + "权限失败", Toast.LENGTH_SHORT).show();
                } else {
                    new AlertDialog.Builder(act)
                            .setTitle("友好提醒")
                            .setMessage("您已拒绝了" + permissionName + "权限，并且下次不再提示，如果要使用此功能，请在设置中为授权" + permissionName + "权限！")
                            .setPositiveButton("好，去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    openAppDetailSettingIntent(act);
                                }
                            })
                            .setNegativeButton("再次拒绝", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }

            }
        }

    }
    public static void showNoPermissionDialog(final AppActivity act, ArrayList<String> list) {
        String [] permissions= new String[list.size()-1];
        for (int i = 0; i <list.size(); i++) {
            if (list.get(i)!=null&&i<list.size()-1){
                permissions[i]=list.get(i);
            }
        }
        if (permissions.length>0){
            if (AndPermission.getShouldShowRationalePermissions(act, permissions)) {
                Toast.makeText(act, "获取" + PERMISSION_NICE_NAME + "权限失败", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(act)
                        .setTitle("友好提醒")
                        .setMessage("您已拒绝了" + PERMISSION_NICE_NAME + "权限，并且下次不再提示，如果要使用此功能，请在设置中为授权" + PERMISSION_NICE_NAME + "权限！")
                        .setPositiveButton("好，去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                openAppDetailSettingIntent(act);
                            }
                        })
                        .setNegativeButton("再次拒绝", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        }

    }
    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            if (act == null) {
                act = (AppActivity) fragment.getActivity();
            }
            new AlertDialog.Builder(act)
                    .setTitle("友好提醒")
                    .setMessage("您已拒绝过访问" + PERMISSION_NICE_NAME + "权限，如果要使用此功能，请把权限赐给我吧！")
                    .setPositiveButton("好，给你", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.resume();
                        }
                    })
                    .setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            rationale.cancel();
                        }
                    }).show();
        }
    };

    public static void openAppDetailSettingIntent(Activity act) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", act.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", act.getPackageName());
        }
        act.startActivity(localIntent);
    }
}
