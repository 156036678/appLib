package com.xiay.applib.ui.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiay.applib.AppApplication;
import com.xiay.applib.R;

import cn.xiay.util.ViewUtil;

public class MyToast {
    private static Toast toast;
    private static ImageView toastImage;
    private static TextView toastText;
    private static int  okIcon,errorIcon;
    public static void init(Context ctx) {
            LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.toast_dialog,null);
            ViewUtil.scaleContentView(layout);
            toastImage = (ImageView) layout.findViewById(R.id.iv_toastImage);
            toastText = (TextView) layout.findViewById(R.id.toastText);
            //text.setText("完全自定义Toast完全自定义Toast");
            toast = new Toast(ctx);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
    }
    public static void   show(String text,int toastIcon){
        toastImage.setBackgroundResource(toastIcon);
        toastText.setText(text);
        toast.show();
    }
    public static void   show(int resId,int toastIcon){
        toastImage.setBackgroundResource(toastIcon);
        toastText.setText(AppApplication.context.getString(resId));
        toast.show();
    }
    public static void   setOkIcon(int okIcon){
        MyToast.okIcon=okIcon;
    }
    public static void   setErrorIcon(int errorIcon){
        MyToast.errorIcon=errorIcon;
    }
    public static void showOk(String text){
        if (okIcon!=0){
            show(text,okIcon);
        }else {
            show(text,R.mipmap.toast_ok);
        }
    }
    public static void showOk(int resId){
        if (okIcon!=0){
            show(resId,okIcon);
        }else {
            show(resId,R.mipmap.toast_ok);
        }
    }
    public static void showError(int resId){
        if (okIcon!=0){
            show(resId,okIcon);
        }else {
            show(resId,R.mipmap.toast_ok);
        }
    }
    public static void showError(String text){
        if (errorIcon!=0){
            show(text,errorIcon);
        }else {
            show(text,R.mipmap.toast_error);
        }
    }
}
