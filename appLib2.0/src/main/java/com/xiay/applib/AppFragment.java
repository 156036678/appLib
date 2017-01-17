package com.xiay.applib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiay.applib.util.rxjava.RxManager;

import java.util.Map;

import cn.xiay.util.ViewUtil;

public abstract class AppFragment extends Fragment {
    protected  boolean isScaleView=true;
    public  boolean isLoadData;
    public AppActivity activity;
    public RxManager rxManager=new RxManager();
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (AppActivity) context;
    }
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException e) {
//            throw new RuntimeException(e);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
    public void setScaleView(boolean scaleView) {
    isScaleView = scaleView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setPageTitle(setTitle());
        setPageHeadColor(setPageHeadColor());
        if (isScaleView)
        ViewUtil.scaleContentView((ViewGroup)getView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      //  getData();
    }

    protected  abstract String setTitle();
    protected  abstract int setPageHeadColor();
    //protected  abstract void   getData();
    /**获取参数对象*/
    public Map<String, String> initParams(String method) {
        return activity.initParams(method);
    }

    /**
     * 替换Fragment
     *
     * @param viewId
     * @param fragment
     * @param isAddToBack
     */
    public void replace(int viewId, Fragment fragment, boolean isAddToBack) {
        if (activity != null) {
            activity.replace(viewId, fragment, isAddToBack);
        }
    }

    /**
     * 替换Fragment
     *
     * @param viewId
     * @param fragment
     */
    public void replace(int viewId, Fragment fragment) {
        replace(viewId, fragment, false);
    }




    /**
     * 跳转activity,代工厂参数
     */
    public void openActivity(Class<?> cls) {
        activity.openActivity(cls);
    }
    /**
     * 跳转activity,代工厂参数
     */
    public void openActivityForResult(Class<?> cls,int requestCode) {
        activity.openActivityForResult(cls,requestCode);
    }
    /**
     * 跳转activity,代工厂参数
     */
    public void openActivityForResult(Intent intent,int requestCode) {
        activity.openActivityForResult(intent,requestCode);
    }

    /**
     * 跳转activity,代工厂参数
     */
    public void openActivity(Intent intent) {
        activity.openActivity(intent);
    }

    protected void setPageTitle(String title) {
        if (title!=null)
        ((TextView) getView().findViewById(R.id.tv_pageHeadName)).setText(title);
    }
    protected void setPageHeadColor(int backgroundColorResources ) {
       View rl_page_head =getView().findViewById(R.id.rl_page_head);
        if (rl_page_head!=null)
        rl_page_head.setBackgroundColor(backgroundColorResources);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rxManager!=null){
            rxManager.clear();
        }
    }

}
