package com.xiay.applib.util;

import android.graphics.Bitmap;

import com.nohttp.BitmapBinary;
import com.nohttp.FileBinary;
import com.nohttp.OnUploadListener;
import com.nohttp.rest.Request;
import com.xiay.applib.AppActivity;
import com.xiay.applib.listener.HttpCallBack;
import com.xiay.applib.ui.dialog.MyProgressDialog;
import com.xiay.applib.util.rxjava.RxUtil;
import com.xiay.applib.util.rxjava.bean.RxTask;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cn.xiay.ui.Toast;
import cn.xiay.util.ImageUtil;

/**
 */
public class UploadUtil {
    private  int nowProgress,totalProgress;
    private MyProgressDialog progressDialog;
    private UploadListener listener;

    public interface UploadListener<T> {
        void onSuccess(T response);
    }
    public  <T> void uploadPic(AppActivity activity, Request<T> request, Map<String, String> params, List<Map<String,Object>> formNameList, final UploadListener<T> listener) {
       uploadPic(activity, request,  params, formNameList, 480,800, "",listener);
    }
    public  <T> void uploadPic(AppActivity activity, Request<T> request, Map<String, String> params, List<Map<String,Object>> formNameList, String progressMsg,final UploadListener<T> listener) {
       uploadPic(activity, request,  params, formNameList, 480,800,progressMsg, listener);
    }

    /**
     *
     * @param activity
     * @param request
     * @param params  请求参数
     * @param formNameList 表单名和图片(图片可以是sd卡路径或Bitmap)
     * @param maxWidth 上传图片最大宽度
     * @param maxHeight 上传图片最大高度
     * @param listener
     */
    public  <T> void uploadPic(final AppActivity activity, final Request<T> request, final Map<String, String> params, final List<Map<String,Object>> formNameList, final int maxWidth, final int maxHeight, String progressMsg, final UploadListener<T> listener) {
        progressDialog = new MyProgressDialog(activity,progressMsg);
        progressDialog.showLoading(progressMsg+"准备中…");
        progressDialog.setCancelable(false);
        this.listener=listener;
        RxUtil.executeRxTask(new RxTask() {
            @Override
            public void doInIOThread() {
                int i=0;
                ImageUtil imageUtil=new ImageUtil(activity);
                for (Map<String,Object>  map:formNameList) {
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        BitmapBinary bitmapBinary = null;
                        if (entry.getValue() instanceof String){
                            File image=imageUtil.compress((String) entry.getValue(),maxWidth,maxHeight);
                            if(image==null)
                                continue;
                            FileBinary fileBinary=new FileBinary(image);
                            fileBinary.setUploadListener(i, onUploadListener);
                            request.add(entry.getKey(),fileBinary);
                            //   bitmapBinary	=new BitmapBinary(bitmap,System.currentTimeMillis()+".png");
                        }else if (entry.getValue() instanceof Bitmap){
                            bitmapBinary	=new BitmapBinary((Bitmap)entry.getValue(),System.currentTimeMillis()+".png");
                            bitmapBinary.setUploadListener(i, onUploadListener);
                        }
                        request.add(entry.getKey(),bitmapBinary);
                    }
                    i++;
                }
                totalProgress=formNameList.size()*100;
            }
            @Override
            public void doInUIThread() {
                activity.sendPost(request, params,null, new HttpCallBack<T>() {
                    @Override
                    public void onSucceed(int what, final T response) {
                        progressDialog.dismiss();
                        listener.onSuccess(response);
                    }
                    @Override
                    public void onFailed(int what, T response) {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

            /**
             * 文件上传监听。
             */
    private OnUploadListener onUploadListener = new OnUploadListener() {

        @Override
        public void onStart(int what) {// 这个文件开始上传。
        }

        @Override
        public void onCancel(int what) {// 这个文件的上传被取消时。
            progressDialog.dismiss();
        }

        @Override
        public void onProgress(int what, int progress) {// 这个文件的上传进度发生边耍
            progress=progress+nowProgress;
            BigDecimal b = new BigDecimal((float)progress/totalProgress);
            float p =b.setScale(2, BigDecimal.ROUND_DOWN).floatValue();
            progressDialog.setProgress(p);
        }

        @Override
        public void onFinish(int what) {// 文件上传完成
            nowProgress+=100;
        }

        @Override
        public void onError(int what, Exception exception) {// 文件上传发生错误。
            exception.printStackTrace();
            progressDialog.dismiss();
            Toast.show("上传失败");
        }
    };
}
