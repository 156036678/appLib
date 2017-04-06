package com.xiay.applib.util.permission;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.xiay.applib.AppActivity;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.ArrayList;
import java.util.List;


public abstract class PermissionBase {

    public Activity act;
    public Fragment fragment;
    RationaleListener listener;
    public static String PERMISSION_NICE_NAME;
int requestCode;
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
        this.requestCode=requestCode;
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
        List<String> deniedPermissions=new ArrayList<>();
        for (int i = 0; i <permissions.length ; i++) {
            deniedPermissions.add(permissions[i]);
        }
        if (AndPermission.hasAlwaysDeniedPermission(act, deniedPermissions)) {
            Toast.makeText(act, "获取" + permissionName + "权限失败", Toast.LENGTH_SHORT).show();
        } else {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(act, 0).show();
//            new AlertDialog.Builder(act)
//                    .setTitle("友好提醒")
//                    .setMessage("您已拒绝了" + permissionName + "权限，并且下次不再提示，如果要使用此功能，请在设置中为授权" + permissionName + "权限！")
//                    .setPositiveButton("好，去设置", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            openAppDetailSettingIntent(act);
//                        }
//                    })
//                    .setNegativeButton("再次拒绝", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                        }
//                    }).show();
        }

    }
    public static void showNoPermissionDialog(final AppActivity act, ArrayList<String> deniedPermissions) {
            if (AndPermission.hasAlwaysDeniedPermission(act, deniedPermissions)) {
                Toast.makeText(act, "获取" + PERMISSION_NICE_NAME + "权限失败", Toast.LENGTH_SHORT).show();
                // 第一种：用默认的提示语。
            } else {
            //    AndPermission.defaultSettingDialog(==actnull?fragment.getActivity():act, requestCode).show();
//                new AlertDialog.Builder(act)
//                        .setTitle("友好提醒")
//                        .setMessage("您已拒绝了" + PERMISSION_NICE_NAME + "权限，并且下次不再提示，如果要使用此功能，请在设置中为授权" + PERMISSION_NICE_NAME + "权限！")
//                        .setPositiveButton("好，去设置", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                AppHelper.getInstance().openAppDetailSettingIntent(act);
//                            }
//                        })
//                        .setNegativeButton("再次拒绝", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        }).show();
            }


    }
    private RationaleListener rationaleListener = new RationaleListener() {
        @Override
        public void showRequestPermissionRationale(int requestCode, final Rationale rationale) {
            AndPermission.rationaleDialog(act==null?fragment.getActivity():act, rationale).show();
//            new AlertDialog.Builder(act)
//                    .setTitle("友好提醒")
//                    .setMessage("您已拒绝过访问" + PERMISSION_NICE_NAME + "权限，如果要使用此功能，请把权限赐给我吧！")
//                    .setPositiveButton("好，给你", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            rationale.resume();
//                        }
//                    })
//                    .setNegativeButton("我拒绝", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.cancel();
//                            rationale.cancel();
//                        }
//                    }).show();
        }
    };

}
